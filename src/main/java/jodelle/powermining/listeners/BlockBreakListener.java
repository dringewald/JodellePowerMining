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
import java.util.Objects;
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

    /** Plugin instance (non-null). */
    private final @Nonnull PowerMining plugin;

    /** Whether durability should be reduced per affected block. */
    private final boolean useDurabilityPerBlock;

    /** Debug message helper (non-null). */
    private final @Nonnull DebuggingMessages debuggingMessages;

    /**
     * Creates and registers the listener.
     *
     * @param plugin The {@link PowerMining} plugin instance.
     */
    public BlockBreakListener(@Nonnull PowerMining plugin) {
        this.plugin = Objects.requireNonNull(plugin, "plugin");
        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);

        this.debuggingMessages = Objects.requireNonNull(this.plugin.getDebuggingMessages(), "debuggingMessages");
        this.useDurabilityPerBlock = this.plugin.getConfig().getBoolean("useDurabilityPerBlock");
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
        final @Nonnull Player player = Objects.requireNonNull(event.getPlayer(), "player");
        final @Nonnull ItemStack handItem = Objects.requireNonNull(player.getInventory().getItemInMainHand(),
                "handItem");

        // Ensure the player is holding an item
        if (handItem.getType() == Material.AIR) {
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

        final @Nonnull Block centerBlock = Objects.requireNonNull(event.getBlock(), "centerBlock");
        final @Nonnull String playerName = Objects.requireNonNull(player.getName(), "playerName");

        final PlayerInteractListener pil = (plugin.getPlayerInteractHandler() != null)
                ? plugin.getPlayerInteractHandler().getListener()
                : null;

        if (pil == null) {
            debuggingMessages.sendConsoleMessage(ChatColor.RED + "PlayerInteractListener is null.");
            return;
        }

        // getBlockFaceByPlayerName may return null depending on your
        // implementation/state
        final BlockFace rawFace = pil.getBlockFaceByPlayerName(playerName);
        if (rawFace == null) {
            debuggingMessages.sendConsoleMessage(ChatColor.RED + "BlockFace is null for player: " + playerName);
            return;
        }
        final @Nonnull BlockFace blockFace = Objects.requireNonNull(rawFace, "blockFace");

        final int radius = Math.max(0, plugin.getConfig().getInt("Radius", Reference.RADIUS) - 1);
        final int depth = Math.max(0, plugin.getConfig().getInt("Depth", Reference.DEPTH) - 1);

        final @Nonnull List<Block> surroundingBlocks = Objects.requireNonNull(
                PowerUtils.getSurroundingBlocks(blockFace, centerBlock, radius, depth),
                "surroundingBlocks");

        // Remove the center block because it is already handled by the original
        // BlockBreakEvent
        surroundingBlocks.removeIf(b -> b.getLocation().equals(centerBlock.getLocation()));

        if (surroundingBlocks.isEmpty()) {
            debuggingMessages.sendConsoleMessage(ChatColor.RED + "No surrounding blocks found.");
            return;
        }

        // Handle durability reduction for the main block first
        if (player.getGameMode() == GameMode.SURVIVAL) {
            // Schedule a delayed inventory update to ensure proper tool breaking
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (player.getInventory().getItemInMainHand().getType() == Material.AIR) {
                    player.updateInventory();
                }
            }, 1L);
        }

        for (Block block : surroundingBlocks) {
            final BreakResult result = checkAndBreakBlock(player, handItem, Objects.requireNonNull(block, "block"));

            // Only reduce durability if a block was actually broken
            if (result.broken() && player.getGameMode() == GameMode.SURVIVAL && !useDurabilityPerBlock) {
                PowerUtils.reduceDurability(player, handItem);
            }

            // Handle XP drops
            if (result.exp() > 0) {
                BlockExpEvent expEvent = new BlockExpEvent(block, result.exp());
                plugin.getServer().getPluginManager().callEvent(expEvent);

                ExperienceOrb orb = block.getWorld().spawn(block.getLocation(), ExperienceOrb.class);
                orb.setExperience(expEvent.getExpToDrop());
            }
        }
    }

    /**
     * @return The amount of XP to drop from breaking the block.
     */
    private record BreakResult(boolean broken, int exp) {
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
    private BreakResult checkAndBreakBlock(@Nonnull Player player, @Nonnull ItemStack handItem, @Nonnull Block block) {
        Objects.requireNonNull(player, "player");
        Objects.requireNonNull(handItem, "handItem");
        Objects.requireNonNull(block, "block");

        if (handItem.getType() == Material.AIR) {
            return new BreakResult(false, 0);
        }

        final @Nonnull Material blockMat = Objects.requireNonNull(block.getType(), "blockMat");
        final @Nonnull Material toolMat = Objects.requireNonNull(handItem.getType(), "toolMat");

        final boolean useHammer = PowerUtils.validateHammer(toolMat, blockMat);
        final boolean useExcavator = !useHammer && PowerUtils.validateExcavator(toolMat, blockMat);

        if (!(useHammer || useExcavator)) {
            return new BreakResult(false, 0);
        }

        if (!PowerUtils.canBreak(plugin, player, block)) {
            return new BreakResult(false, 0);
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
                plugin.getLogger().warning(
                        "XP drops configuration section 'xp-drops' not found. Using default XP of 0 for " + blockMat);
            } else {
                ConfigurationSection blockSection = xpDropsSection.getConfigurationSection(blockMat.toString());
                if (blockSection == null) {
                    debuggingMessages.sendConsoleMessage(
                            "XP drop configuration for block " + blockMat + " not found. Using default XP of 0.");
                } else {
                    int minXp = blockSection.getInt("min", 0);
                    int maxXp = blockSection.getInt("max", minXp);
                    if (maxXp < minXp) {
                        plugin.getLogger().warning("Invalid XP configuration for block " + blockMat
                                + ": max (" + maxXp + ") is less than min (" + minXp + "). Using default XP of 0.");
                    } else {
                        // Calculate a random XP value within the range (inclusive)
                        expToDrop = minXp + new Random().nextInt(maxXp - minXp + 1);
                    }
                }
            }
        } catch (Exception e) {
            plugin.getLogger().severe(
                    "Error while retrieving XP drop configuration for block " + blockMat + ": " + e.getMessage());
        }

        // Break the block naturally if conditions are met
        final boolean broken = block.breakNaturally(handItem);

        if (!broken) {
            return new BreakResult(false, 0);
        }

        // If durability is handled per broken block, apply it here
        if (player.getGameMode() == GameMode.SURVIVAL && useDurabilityPerBlock) {
            PowerUtils.reduceDurability(player, handItem);
        }

        // If Silk Touch is present, do not drop XP
        if (handItem.getItemMeta() != null && handItem.getItemMeta().hasEnchant(Enchantment.SILK_TOUCH)) {
            debuggingMessages.sendConsoleMessage(
                    "Silk Touch detected on tool; no XP will be dropped for block " + blockMat);
            return new BreakResult(true, 0);
        }

        return new BreakResult(true, expToDrop);
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
    private boolean basicVerifications(@Nonnull Player player, @Nonnull ItemStack handItem) {
        Objects.requireNonNull(player, "player");
        Objects.requireNonNull(handItem, "handItem");

        if (handItem.getType() == Material.AIR) {
            return true;
        }

        if (player.isSneaking()) {
            return true;
        }

        if (!PowerUtils.isPowerTool(handItem)) {
            return true;
        }

        final @Nonnull Material handItemType = Objects.requireNonNull(handItem.getType(), "handItemType");
        return !PowerUtils.checkUsePermission(plugin, player, handItemType);
    }
}
