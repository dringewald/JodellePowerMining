package jodelle.powermining.utils;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import jodelle.powermining.PowerMining;
import jodelle.powermining.lib.Reference;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

/**
 * Utility class for handling PowerTools-related logic in the PowerMining
 * plugin.
 * 
 * <p>
 * This class provides a collection of static methods to check tool types,
 * validate permissions,
 * handle durability reduction, and interact with blocks in the game world. It
 * also integrates
 * with WorldGuard to enforce region protection rules.
 * </p>
 */
public class PowerUtils {

    /**
     * Constructs a {@code PowerUtils} instance.
     * 
     * <p>
     * This constructor is private since all methods in this class are static,
     * meaning an instance of {@code PowerUtils} is never needed.
     * </p>
     */
    public PowerUtils() {
    }

    /**
     * Players whose block and interact events are currently fired by the plugin
     * itself. The plugin's own listeners skip these events, otherwise every extra
     * block would start another area action.
     */
    private static final Set<UUID> SIMULATING = new HashSet<>();

    /**
     * Marks that the plugin is about to fire events for this player itself.
     *
     * @param player the player the events are fired for
     */
    public static void startSimulating(@Nonnull Player player) {
        SIMULATING.add(player.getUniqueId());
    }

    /**
     * Ends a section started with {@link #startSimulating(Player)}.
     *
     * @param player the player the events were fired for
     */
    public static void stopSimulating(@Nonnull Player player) {
        SIMULATING.remove(player.getUniqueId());
    }

    /**
     * Tells whether the current event was fired by the plugin itself.
     *
     * @param player the player of the event
     * @return {@code true} while the plugin fires events for this player
     */
    public static boolean isSimulating(@Nonnull Player player) {
        return SIMULATING.contains(player.getUniqueId());
    }

    /**
     * Tells whether the player turned area mining off with
     * {@code /powermining toggle}. The choice is stored on the player, so it
     * survives restarts.
     *
     * @param player the player to check
     * @return {@code true} if the PowerTools act like their vanilla tools
     */
    public static boolean isAreaMiningDisabled(@Nonnull Player player) {
        return player.getPersistentDataContainer().has(areaMiningKey(), PersistentDataType.BYTE);
    }

    /**
     * Switches area mining on or off for the player.
     *
     * @param player the player who toggles
     * @return {@code true} if area mining is on afterwards
     */
    public static boolean toggleAreaMining(@Nonnull Player player) {
        final PersistentDataContainer data = player.getPersistentDataContainer();
        if (data.has(areaMiningKey(), PersistentDataType.BYTE)) {
            data.remove(areaMiningKey());
            return true;
        }
        data.set(areaMiningKey(), PersistentDataType.BYTE, (byte) 1);
        return false;
    }

    /**
     * Tells whether PowerTools act like their vanilla tools in this world
     * ({@code disabledWorlds} in the config).
     *
     * @param plugin the plugin with the config
     * @param world  the world to check
     * @return {@code true} if the world is listed
     */
    public static boolean isWorldDisabled(@Nonnull PowerMining plugin, @Nonnull World world) {
        return plugin.getConfig().getStringList("disabledWorlds").contains(world.getName());
    }

    /**
     * Collects the reasons for a single block action instead of an area: the
     * player sneaks, turned area mining off, or is in a disabled world.
     *
     * @param plugin the plugin with the config
     * @param player the player using a PowerTool
     * @return {@code true} if the PowerTool must act like its vanilla tool
     */
    public static boolean isAreaActionBlocked(@Nonnull PowerMining plugin, @Nonnull Player player) {
        return player.isSneaking() || isAreaMiningDisabled(player) || isWorldDisabled(plugin, player.getWorld());
    }

    private static @Nonnull NamespacedKey areaMiningKey() {
        return new NamespacedKey(PowerMining.getInstance(), "areaMiningDisabled");
    }

    /**
     * Maps a vanilla tool to the name of the PowerTool built from it.
     *
     * @param toolType the vanilla pickaxe, shovel or hoe
     * @return for example {@code DIAMOND_HAMMER}, or {@code null} for other items
     */
    public static @Nullable String getPowerToolName(@Nonnull Material toolType) {
        int index = Reference.PICKAXES.indexOf(toolType);
        if (index >= 0) {
            return Reference.HAMMERS.get(index);
        }
        index = Reference.SHOVELS.indexOf(toolType);
        if (index >= 0) {
            return Reference.EXCAVATORS.get(index);
        }
        index = Reference.HOES.indexOf(toolType);
        if (index >= 0) {
            return Reference.PLOWS.get(index);
        }
        index = Reference.SWORDS.indexOf(toolType);
        return index >= 0 ? Reference.SICKLES.get(index) : null;
    }

    /**
     * Reads "Radius" or "Depth" for a PowerTool: the entry under
     * {@code toolSizes.<tool>} if there is one, otherwise the global value.
     *
     * @param plugin   the plugin with the config
     * @param toolType the vanilla tool the PowerTool is based on
     * @param key      {@code Radius} or {@code Depth}
     * @param fallback the value used when the config has neither
     * @return the configured size, 1 meaning only the clicked block
     */
    public static int getToolSize(@Nonnull PowerMining plugin, @Nonnull Material toolType, @Nonnull String key,
            int fallback) {
        final int globalSize = plugin.getConfig().getInt(key, fallback);
        final String toolName = getPowerToolName(toolType);
        return toolName == null ? globalSize : plugin.getConfig().getInt("toolSizes." + toolName + "." + key, globalSize);
    }

    /**
     * Checks if an {@link ItemStack} is a PowerTool by verifying its
     * {@link PersistentDataContainer}.
     * 
     * <p>
     * This method looks for a specific {@link NamespacedKey} in the item's metadata
     * to determine if
     * it is a PowerTool.
     * </p>
     * 
     * @param item The {@link ItemStack} to check.
     * @return {@code true} if the item is a PowerTool, {@code false} otherwise.
     */
    public static boolean isPowerTool(@Nonnull ItemStack item) {

        PowerMining plugin = PowerMining.getInstance();
        ItemMeta itemMeta = item.getItemMeta();

        if (itemMeta == null) {
            return false;
        }

        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        NamespacedKey isPowerTool = new NamespacedKey(plugin, "isPowerTool");

        return container.has(isPowerTool, PersistentDataType.STRING);
    }

    /**
     * Reduces the durability of a tool when it is used.
     * 
     * <p>
     * If the tool has the {@link Enchantment#UNBREAKING} enchantment, there is a
     * chance
     * that durability loss will be prevented based on the enchantment level. If the
     * tool
     * reaches zero durability, it is removed from the player's inventory and a
     * breaking
     * sound effect is played.
     * </p>
     * 
     * @param player The {@link Player} using the tool.
     * @param item   The {@link ItemStack} representing the tool.
     */
    public static void reduceDurability(@Nonnull Player player, @Nonnull ItemStack item) {

        if (item.getEnchantments().containsKey(Enchantment.UNBREAKING)) {
            Random rand = new Random();
            Integer unbreakingLevel = item.getEnchantments().get(Enchantment.UNBREAKING);
            if (rand.nextInt(unbreakingLevel + 1) != 0) {
                return;
            }
        }
        ItemMeta itemMeta = item.getItemMeta();

        if (!(itemMeta instanceof Damageable)) {
            return;
        }

        Damageable damageable = (Damageable) itemMeta;

        // increasing the damage by one reduces the durability by one
        damageable.setDamage(damageable.getDamage() + 1);
        item.setItemMeta(itemMeta);

        /*
         * Reducing the durability doesn't cause the item to be broken when it gets
         * below zero.
         * That said, it is needed to implement this behavior manually.
         * If the damage is higher than the item durability, the item is remover from
         * the player inventory and a breaking sound is played
         */
        if (damageable.getDamage() > item.getType().getMaxDurability() - 1) {
            player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));  
            player.updateInventory(); 
    
            // Play item break sound
            Location loc = player.getEyeLocation();
            World world = loc.getWorld();
            if (world != null) {
                world.playSound(loc, Sound.ENTITY_ITEM_BREAK, 1.0F, 1.0F);
            }
        }
    }

    /**
     * Determines whether a given block type is minable.
     * 
     * @param blockType The {@link Material} of the block.
     * @return {@code true} if the block is minable, {@code false} otherwise.
     */
    public static boolean isMineable(@Nonnull Material blockType) {
        return Reference.MINABLE.containsKey(blockType);
    }

    /**
     * Determines whether a given block type is diggable.
     * 
     * @param blockType The {@link Material} of the block.
     * @return {@code true} if the block is diggable, {@code false} otherwise.
     */
    public static boolean isDigable(@Nonnull Material blockType) {
        return Reference.DIGGABLE.contains(blockType);
    }

    /**
     * Determines whether a given block type is tillable.
     * 
     * @param blockType The {@link Material} of the block.
     * @return {@code true} if the block is tillable, {@code false} otherwise.
     */
    public static boolean isTillable(@Nonnull Material blockType) {
        return Reference.TILLABLE.contains(blockType);
    }

    /**
     * Determines whether a given block type is pathable.
     * 
     * @param blockType The {@link Material} of the block.
     * @return {@code true} if the block is pathable, {@code false} otherwise.
     */
    public static boolean isPathable(@Nonnull Material blockType) {
        return Reference.PATHABLE.contains(blockType);
    }

    /**
     * Retrieves a list of surrounding blocks based on the block face clicked, the
     * target block,
     * and the specified radius and depth.
     * 
     * <p>
     * This method determines which blocks should be affected by the player's
     * action, depending
     * on the block face interacted with. It generates a list of blocks in a cubic
     * or rectangular
     * shape, trimming any null values.
     * </p>
     * 
     * @param blockFace   The {@link BlockFace} indicating the direction the player
     *                    interacted with.
     * @param targetBlock The {@link Block} the player broke.
     * @param radius      The radius of the area to affect.
     * @param depth       The depth of the area to affect.
     * @return A list of surrounding {@link Block}s affected by the player's action.
     */
    @Nonnull
    public static ArrayList<Block> getSurroundingBlocks(@Nonnull BlockFace blockFace, @Nonnull Block targetBlock,
            @Nonnull Integer radius, @Nonnull Integer depth) {
        // Array that will store the blocks surrounding the center block
        final ArrayList<Block> blocks = new ArrayList<>();
        World world = targetBlock.getWorld();

        int bx, by, bz;
        bx = targetBlock.getX();
        by = targetBlock.getY();
        bz = targetBlock.getZ();

        // Check the block face from which the block is being broken in order to get the
        // correct surrounding blocks
        switch (blockFace) {
            case UP:
                for (int x = bx - radius; x <= bx + radius; x++) {
                    for (int y = by - depth; y <= by; y++) {
                        for (int z = bz - radius; z <= bz + radius; z++) {
                            blocks.add(world.getBlockAt(x, y, z));
                        }
                    }
                }
                break;
            case DOWN:
                for (int x = bx - radius; x <= bx + radius; x++) {
                    for (int y = by; y <= by + depth; y++) {
                        for (int z = bz - radius; z <= bz + radius; z++) {
                            blocks.add(world.getBlockAt(x, y, z));
                        }
                    }
                }
                break;
            case EAST:
                for (int x = bx - depth; x <= bx; x++) {
                    for (int y = by - radius; y <= by + radius; y++) {
                        for (int z = bz - radius; z <= bz + radius; z++) {
                            blocks.add(world.getBlockAt(x, y, z));
                        }
                    }
                }
                break;
            case WEST:
                for (int x = bx; x <= bx + depth; x++) {
                    for (int y = by - radius; y <= by + radius; y++) {
                        for (int z = bz - radius; z <= bz + radius; z++) {
                            blocks.add(world.getBlockAt(x, y, z));
                        }
                    }
                }
                break;
            case NORTH:
                for (int x = bx - radius; x <= bx + radius; x++) {
                    for (int y = by - radius; y <= by + radius; y++) {
                        for (int z = bz; z <= bz + depth; z++) {
                            blocks.add(world.getBlockAt(x, y, z));
                        }
                    }
                }
                break;
            case SOUTH:
                // logs.sendMessage(ChatColor.AQUA + "SOUTH");
                for (int x = bx - radius; x <= bx + radius; x++) {
                    for (int y = by - radius; y <= by + radius; y++) {
                        for (int z = bz - depth; z <= bz; z++) {
                            blocks.add(world.getBlockAt(x, y, z));
                        }
                    }
                }
                break;
            default:
                break;
        }

        // Trim the nulls from the list
        blocks.removeAll(Collections.singleton(null));
        return blocks;
    }

    /**
     * Retrieves a list of surrounding blocks for farming purposes based on the
     * block face clicked.
     * 
     * <p>
     * This method is a specialized version of {@link #getSurroundingBlocks} used
     * primarily for
     * plowing or path creation. It only checks horizontally, as farmland and paths
     * are typically
     * affected on a flat plane.
     * </p>
     * 
     * @param blockFace   The {@link BlockFace} indicating the direction the player
     *                    interacted with.
     * @param targetBlock The {@link Block} being affected.
     * @param radius      The radius of the area to affect.
     * @return A list of surrounding {@link Block}s affected by the farming tool.
     */
    public static ArrayList<Block> getSurroundingBlocksFarm(BlockFace blockFace, Block targetBlock, Integer radius) {
        ArrayList<Block> blocks = new ArrayList<>();
        World world = targetBlock.getWorld();
        int bx, by, bz;
        bx = targetBlock.getX();
        by = targetBlock.getY();
        bz = targetBlock.getZ();

        // Check the block face from which the block is being broken in order to get the
        // correct surrounding blocks
        if (blockFace == BlockFace.UP) {
            for (int x = bx - radius; x <= bx + radius; x++) {
                for (int z = bz - radius; z <= bz + radius; z++) {
                    blocks.add(world.getBlockAt(x, by, z));
                }
            }
        }

        // Trim the nulls from the list
        blocks.removeAll(Collections.singleton(null));
        return blocks;
    }

    /**
     * Checks if the player has permission to craft a PowerTool.
     * 
     * @param plugin   The {@link PowerMining} instance managing permissions.
     * @param player   The {@link Player} attempting to craft the tool.
     * @param itemType The {@link Material} type of the tool being crafted.
     * @return {@code true} if the player has permission, {@code false} otherwise.
     * @throws NoSuchElementException if the tool is not listed in the craft
     *                                permissions.
     */
    public static boolean checkCraftPermission(@Nonnull PowerMining plugin, @Nonnull Player player,
            @Nonnull Material itemType) {
        if (!Reference.CRAFT_PERMISSIONS.containsKey(itemType)) {
            throw new NoSuchElementException();
        }

        String perm = Reference.CRAFT_PERMISSIONS.get(itemType);

        if (player.hasPermission(perm)) {
            return true;
        }

        player.sendMessage(plugin.getLangFile().getMessage("no-craft-permission",
                "&cYou don't have permissions to craft this PowerTool!", true));
        return false;
    }

    /**
     * Checks if the player has permission to use a PowerTool.
     * 
     * @param plugin   The {@link PowerMining} instance managing permissions.
     * @param player   The {@link Player} attempting to use the tool.
     * @param itemType The {@link Material} type of the tool being used.
     * @return {@code true} if the player has permission, {@code false} otherwise.
     */
    public static boolean checkUsePermission(@Nonnull PowerMining plugin, @Nonnull Player player,
            @Nonnull Material itemType) {
        if (!Reference.USE_PERMISSIONS.containsKey(itemType)) {
            throw new NoSuchElementException();
        }

        String perm = Reference.USE_PERMISSIONS.get(itemType);

        if (player.hasPermission(perm)) {
            return true;
        }

        player.sendMessage(plugin.getLangFile().getMessage("no-use-permission",
                "&cYou don't have permissions to use this PowerTool!", true));
        return false;
    }

    /**
     * Checks if the player has permission to enchant a PowerTool.
     * 
     * @param plugin   The {@link PowerMining} instance managing permissions.
     * @param player   The {@link Player} attempting to enchant the tool.
     * @param itemType The {@link Material} type of the tool being enchanted.
     * @return {@code true} if the player has permission, {@code false} otherwise.
     */
    public static boolean checkEnchantPermission(@Nonnull PowerMining plugin, @Nonnull Player player,
            @Nonnull Material itemType) {
        if (!Reference.ENCHANT_PERMISSIONS.containsKey(itemType)) {
            throw new NoSuchElementException();
        }
        String perm = Reference.ENCHANT_PERMISSIONS.get(itemType);

        if (player.hasPermission(perm)) {
            return true;
        }

        player.sendMessage(plugin.getLangFile().getMessage("no-enchant-permission",
                "&cYou don't have permissions to enchant this PowerTool!", true));
        return false;
    }

    /**
     * Checks if the player is allowed to destroy the target block based on
     * WorldGuard region protections.
     * 
     * <p>
     * This method verifies whether the player has permission to break a block in a
     * protected region using WorldGuard. If WorldGuard is not enabled, it assumes
     * the player is allowed to break the block.
     * </p>
     * 
     * @param plugin The {@link PowerMining} instance managing WorldGuard
     *               integration.
     * @param player The {@link Player} attempting to break the block.
     * @param block  The {@link Block} the player is trying to destroy.
     * @return {@code true} if the player is allowed to break the block,
     *         {@code false} otherwise.
     */
    public static boolean canBreak(@Nonnull PowerMining plugin, @Nonnull Player player, @Nonnull Block block) {

        if (plugin.getWorldGuard() == null) {
            return true;
        }

        LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
        com.sk89q.worldedit.util.Location loc = BukkitAdapter.adapt(block.getLocation());
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();

        if (!query.testBuild(loc, localPlayer, Flags.BUILD)) {
            plugin.getDebuggingMessages().sendConsoleMessage("Can't build");
            return false;
        }

        plugin.getDebuggingMessages().sendConsoleMessage("Can build");
        return true;
    }

    /**
     * Determines if the given tool is a valid Hammer for breaking a specific block
     * type.
     * 
     * <p>
     * This method checks whether the specified hammer type is allowed to mine the
     * given
     * block type, ensuring compatibility with the configured list of minable
     * materials.
     * </p>
     * 
     * @param hammerType The {@link Material} representing the hammer.
     * @param blockType  The {@link Material} representing the block to be mined.
     * @return {@code true} if the hammer is valid for breaking the block,
     *         {@code false} otherwise.
     */
    public static boolean validateHammer(@Nonnull Material hammerType, @Nonnull Material blockType) {
        if (!Reference.PICKAXES.contains(hammerType)) {
            return false;
        }
        if (!Reference.CUSTOM_BLOCK_LIST) {
            return Tag.MINEABLE_PICKAXE.isTagged(blockType) && isCorrectTier(hammerType, blockType);
        }
        return isMineable(blockType) &&
                (Reference.MINABLE.get(blockType) == null || Reference.MINABLE.get(blockType).contains(hammerType));
    }

    /**
     * Checks the tool tier the way vanilla does for drops: a block in the
     * {@code incorrect_for_<tier>_tool} tag needs a better tool. Unbreakable
     * blocks (negative hardness) are never allowed.
     *
     * @param toolType  the vanilla tool the PowerTool is based on
     * @param blockType the block to break
     * @return {@code true} if the tool tier is high enough for the block
     */
    private static boolean isCorrectTier(@Nonnull Material toolType, @Nonnull Material blockType) {
        if (blockType.getHardness() < 0) {
            return false;
        }
        final Tag<Material> incorrectBlocks = incorrectBlocksFor(toolType);
        return incorrectBlocks == null || !incorrectBlocks.isTagged(blockType);
    }

    /**
     * Maps a vanilla tool to the tag of blocks that are too hard for its tier.
     *
     * @param toolType the vanilla pickaxe, shovel or hoe
     * @return the matching {@code incorrect_for_*_tool} tag, or {@code null} for
     *         unknown tools
     */
    private static @Nullable Tag<Material> incorrectBlocksFor(@Nonnull Material toolType) {
        return switch (toolType) {
            case WOODEN_PICKAXE, WOODEN_SHOVEL, WOODEN_HOE -> Tag.INCORRECT_FOR_WOODEN_TOOL;
            case STONE_PICKAXE, STONE_SHOVEL, STONE_HOE -> Tag.INCORRECT_FOR_STONE_TOOL;
            case COPPER_PICKAXE, COPPER_SHOVEL, COPPER_HOE -> Tag.INCORRECT_FOR_COPPER_TOOL;
            case IRON_PICKAXE, IRON_SHOVEL, IRON_HOE -> Tag.INCORRECT_FOR_IRON_TOOL;
            case GOLDEN_PICKAXE, GOLDEN_SHOVEL, GOLDEN_HOE -> Tag.INCORRECT_FOR_GOLD_TOOL;
            case DIAMOND_PICKAXE, DIAMOND_SHOVEL, DIAMOND_HOE -> Tag.INCORRECT_FOR_DIAMOND_TOOL;
            case NETHERITE_PICKAXE, NETHERITE_SHOVEL, NETHERITE_HOE -> Tag.INCORRECT_FOR_NETHERITE_TOOL;
            default -> null;
        };
    }

    /**
     * Determines if the given tool is a valid Excavator for digging a specific
     * block type.
     * 
     * <p>
     * This method checks if the specified excavator type is appropriate for digging
     * the given block type, ensuring it aligns with the predefined list of diggable
     * materials.
     * </p>
     * 
     * @param excavatorType The {@link Material} representing the excavator.
     * @param blockType     The {@link Material} representing the block to be
     *                      excavated.
     * @return {@code true} if the excavator is valid for digging the block,
     *         {@code false} otherwise.
     */
    public static boolean validateExcavator(@Nonnull Material excavatorType, @Nonnull Material blockType) {
        if (!Reference.SPADES.contains(excavatorType)) {
            return false;
        }
        if (!Reference.CUSTOM_BLOCK_LIST) {
            return Tag.MINEABLE_SHOVEL.isTagged(blockType) && isCorrectTier(excavatorType, blockType);
        }
        return isDigable(blockType);
    }

    /**
     * Determines if the given tool is a valid Plow for tilling a specific block
     * type.
     * 
     * <p>
     * This method checks if the specified plow type can till the given block type,
     * ensuring it matches the configured list of tillable materials.
     * </p>
     * 
     * @param plowType  The {@link Material} representing the plow.
     * @param blockType The {@link Material} representing the block to be tilled.
     * @return {@code true} if the plow is valid for tilling the block,
     *         {@code false} otherwise.
     */
    public static boolean validatePlow(@Nonnull Material plowType, @Nonnull Material blockType) {
        return (isTillable(blockType) && Reference.HOES.contains(plowType));
    }

    /**
     * Determines if the given tool is a valid Excavator for creating paths on a
     * specific block type.
     * 
     * <p>
     * This method checks if the specified excavator type is suitable for converting
     * the
     * given block type into a dirt path, ensuring it is part of the predefined list
     * of pathable materials.
     * </p>
     * 
     * @param excavatorType The {@link Material} representing the excavator.
     * @param blockType     The {@link Material} representing the block to be
     *                      converted into a path.
     * @return {@code true} if the excavator is valid for creating a path,
     *         {@code false} otherwise.
     */
    public static boolean validatePath(@Nonnull Material excavatorType, @Nonnull Material blockType) {
        return (isPathable(blockType) && Reference.SPADES.contains(excavatorType));
    }
}
