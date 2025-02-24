package jodelle.powermining.listeners;

import jodelle.powermining.PowerMining;
import jodelle.powermining.lib.DebuggingMessages;
import jodelle.powermining.lib.Reference;
import jodelle.powermining.utils.PowerUtils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExpEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Random;

import javax.annotation.Nonnull;

/**
 * Listener for handling {@link BlockBreakEvent} in the PowerMining plugin.
 * 
 * <p>
 * This class manages the mechanics of breaking blocks using custom PowerTools
 * such as Hammers and Excavators. It determines the area of effect, reduces
 * tool durability, grants experience, and integrates with job-tracking plugins.
 * </p>
 */
public class BlockBreakListener implements Listener {
    public final PowerMining plugin;
    public final boolean useDurabilityPerBlock;
    public final DebuggingMessages debuggingMessages;

    public BlockBreakListener(@Nonnull PowerMining plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        debuggingMessages = plugin.getDebuggingMessages();
        useDurabilityPerBlock = plugin.getConfig().getBoolean("useDurabilityPerBlock");
    }

    /**
     * Handles block breaking events and applies PowerTool effects.
     * 
     * <p>
     * This method verifies that the player is using a PowerTool, determines the
     * affected blocks, reduces durability, grants XP, and integrates with
     * JobsReborn if enabled.
     * </p>
     * 
     * @param event The {@link BlockBreakEvent} triggered when a block is broken.
     */
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack handItem = player.getInventory().getItemInMainHand();

        // Ensure the player is holding an item
        if (handItem == null || handItem.getType() == Material.AIR) {
            return;
        }

        // Debug message if tool has a custom name
        if (handItem.getItemMeta() != null && handItem.getItemMeta().hasDisplayName()) {
            debuggingMessages.sendConsoleMessage(
                    ChatColor.RED + "Broke a block with item: " + handItem.getItemMeta().getDisplayName());
        }

        // Perform basic verifications (permissions, tool type, sneaking)
        if (basicVerifications(player, handItem)) {
            return;
        }

        final Block centerBlock = event.getBlock();
        final String playerName = player.getName();
        final PlayerInteractListener pil = (plugin.getPlayerInteractHandler() != null)
                ? plugin.getPlayerInteractHandler().getListener()
                : null;

        if (pil == null) {
            debuggingMessages.sendConsoleMessage(ChatColor.RED + "PlayerInteractListener is null.");
            return;
        }

        final BlockFace blockFace = pil.getBlockFaceByPlayerName(playerName);

        int radius = Math.max(0, plugin.getConfig().getInt("Radius", Reference.RADIUS) - 1);
        int depth = Math.max(0, plugin.getConfig().getInt("Depth", Reference.DEPTH) - 1);

        List<Block> surroundingBlocks = PowerUtils.getSurroundingBlocks(blockFace, centerBlock, radius, depth);

        if (surroundingBlocks.isEmpty()) {
            debuggingMessages.sendConsoleMessage(ChatColor.RED + "No surrounding blocks found.");
            return;
        }

        // Handle durability reduction for the main block first
        if (player.getGameMode().equals(GameMode.SURVIVAL)) {
            PowerUtils.reduceDurability(player, handItem);

            // Schedule a delayed inventory update to ensure proper tool breaking
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (player.getInventory().getItemInMainHand().getType() == Material.AIR) {
                    player.updateInventory();
                }
            }, 1L);
        }

        for (Block block : surroundingBlocks) {
            int exp = checkAndBreakBlock(player, handItem, block);

            // Handle durability reduction per surrounding block
            if (player.getGameMode().equals(GameMode.SURVIVAL)) {
                PowerUtils.reduceDurability(player, handItem);

                // Schedule a delayed inventory update to prevent tool reappearing
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if (player.getInventory().getItemInMainHand().getType() == Material.AIR) {
                        player.updateInventory();
                    }
                }, 1L);
            }

            // Handle XP drops
            if (exp > 0) {
                BlockExpEvent expEvent = new BlockExpEvent(block, exp);
                plugin.getServer().getPluginManager().callEvent(expEvent);

                ExperienceOrb orb = block.getWorld().spawn(block.getLocation(), ExperienceOrb.class);
                orb.setExperience(expEvent.getExpToDrop());
            }
        }
    }

    /**
     * Checks and breaks a block if it is compatible with the PowerTool.
     * 
     * <p>
     * This method validates whether the tool can break the given block, notifies
     * JobsReborn (if enabled), and determines the experience points (XP) to drop.
     * </p>
     * 
     * @param player   The player breaking the block.
     * @param handItem The tool being used to break the block.
     * @param block    The {@link Block} being broken.
     * @return The amount of XP to drop from breaking the block.
     */
    private int checkAndBreakBlock(Player player, ItemStack handItem, @Nonnull Block block) {
        if (handItem == null || handItem.getType() == Material.AIR) {
            return 0; // No tool, no block breaking
        }

        Material blockMat = block.getType();
        boolean useHammer = PowerUtils.validateHammer(handItem.getType(), blockMat);
        boolean useExcavator = !useHammer && PowerUtils.validateExcavator(handItem.getType(), blockMat);

        if (useHammer || useExcavator) {
            if (!PowerUtils.canBreak(plugin, player, block)) {
                return 0;
            }

            // Notify Jobs Reborn BEFORE breaking the block
            if (plugin.getJobsHook() != null) {
                plugin.getJobsHook().notifyJobs(player, block);
                debuggingMessages
                        .sendConsoleMessage(ChatColor.GREEN + "✅ Jobs Reborn notified for block: " + block.getType());
            }

            // Retrieve XP drop configuration for this block
            int expToDrop = 0;
            try {
                ConfigurationSection xpDropsSection = plugin.getConfig().getConfigurationSection("xp-drops");
                if (xpDropsSection == null) {
                    plugin.getLogger().warning("XP drops configuration section 'xp-drops' not found. Using default XP of 0 for " + blockMat);
                } else {
                    ConfigurationSection blockSection = xpDropsSection.getConfigurationSection(blockMat.toString());
                    if (blockSection == null) {
                        debuggingMessages.sendConsoleMessage("XP drop configuration for block " + blockMat + " not found. Using default XP of 0.");
                    } else {
                        int minXp = blockSection.getInt("min", 0);
                        int maxXp = blockSection.getInt("max", minXp);
                        if (maxXp < minXp) {
                            plugin.getLogger().warning("Invalid XP configuration for block " + blockMat + ": max (" + maxXp + ") is less than min (" + minXp + "). Using default XP of 0.");
                        } else {
                            // Calculate a random XP value within the range (inclusive)
                            expToDrop = minXp + new Random().nextInt(maxXp - minXp + 1);
                        }
                    }
                }
            } catch (Exception e) {
                plugin.getLogger().severe("Error while retrieving XP drop configuration for block " + blockMat + ": " + e.getMessage());
            }

            // Break the block naturally if conditions are met
            if (block.breakNaturally(handItem) && player.getGameMode().equals(GameMode.SURVIVAL)) {
                if (plugin.getConfig().getBoolean("useDurabilityPerBlock")) {
                    PowerUtils.reduceDurability(player, handItem);
                }
            }

            if (handItem != null && handItem.getItemMeta() != null && handItem.getItemMeta().hasEnchant(Enchantment.SILK_TOUCH)) {
                debuggingMessages.sendConsoleMessage("Silk Touch detected on tool; no XP will be dropped for block " + blockMat);
                return expToDrop = 0;
            }
            else {
                return expToDrop;
            }
        }

        return 0; // Return 0 XP if conditions aren't met
    }

    /**
     * Performs basic verifications before applying PowerTool effects.
     * 
     * <p>
     * This method ensures the player is using a valid PowerTool, has permissions,
     * and is not sneaking to bypass the special tool mechanics.
     * </p>
     * 
     * @param player   The player using the tool.
     * @param handItem The tool being used.
     * @return {@code true} if the tool should behave normally, {@code false} if
     *         PowerTool mechanics apply.
     */
    private boolean basicVerifications(Player player, ItemStack handItem) {
        if (handItem == null || handItem.getType() == Material.AIR) {
            return true;
        }

        if (player.isSneaking()) {
            return true;
        }

        Material handItemType = handItem.getType();

        if (!PowerUtils.isPowerTool(handItem)) {
            return true;
        }

        if (!PowerUtils.checkUsePermission(plugin, player, handItemType)) {
            return true;
        }

        return false;
    }
}
