package jodelle.powermining.listeners;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.ItemStack;

import jodelle.powermining.PowerMining;
import jodelle.powermining.lib.Reference;
import jodelle.powermining.utils.PowerUtils;

/**
 * The sickle: breaking a ripe crop harvests every ripe crop of the same kind
 * of field around it and plants them again.
 *
 * <p>
 * Every crop is broken through {@link Player#breakBlock(Block)}, so
 * protection plugins, CoreProtect and Jobs see each one. Replanting costs one
 * seed from the drops, like a player would replant by hand. A crop whose drops
 * hold no seed stays empty.
 * </p>
 */
public class SickleListener implements Listener {

    /** Crops the sickle harvests and the item that plants them again. */
    private static final Map<Material, Material> SEEDS = Map.of(
            Material.WHEAT, Material.WHEAT_SEEDS,
            Material.CARROTS, Material.CARROT,
            Material.POTATOES, Material.POTATO,
            Material.BEETROOTS, Material.BEETROOT_SEEDS,
            Material.NETHER_WART, Material.NETHER_WART);

    private final PowerMining plugin;

    /** Crops broken by a sickle whose drops have not been handled yet. */
    private final Set<Location> harvesting = new HashSet<>();

    /** Crops whose drops paid the seed for replanting. */
    private final Set<Location> paid = new HashSet<>();

    /**
     * Creates the listener and registers it.
     *
     * @param plugin the plugin instance
     */
    public SickleListener(@Nonnull PowerMining plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Harvests the ripe crops around a ripe crop broken with a sickle.
     *
     * @param event the break event of the crop the player hit
     */
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onHarvest(BlockBreakEvent event) {
        final Player player = event.getPlayer();
        if (PowerUtils.isSimulating(player)) {
            return;
        }
        final ItemStack sickle = player.getInventory().getItemInMainHand();
        final Material sickleType = sickle.getType();
        final Block center = event.getBlock();
        if (!Reference.SWORDS.contains(sickleType) || !PowerUtils.isPowerTool(sickle) || !isRipe(center)
                || PowerUtils.isAreaActionBlocked(plugin, player)
                || !PowerUtils.checkUsePermission(plugin, player, sickleType)) {
            return;
        }

        // The center crop disappears only after this event, so it is planted again one tick later
        final Material centerType = center.getType();
        harvesting.add(center.getLocation());
        plugin.getServer().getScheduler().runTask(plugin, () -> finishHarvest(center, centerType));

        final boolean survival = player.getGameMode() == GameMode.SURVIVAL;
        final boolean durabilityPerBlock = plugin.getConfig().getBoolean("useDurabilityPerBlock");
        final int radius = Math.max(0, PowerUtils.getToolSize(plugin, sickleType, "Radius", 2) - 1);
        int harvested = 0;
        for (Block crop : PowerUtils.getSurroundingBlocksFarm(BlockFace.UP, center, radius)) {
            if (player.getInventory().getItemInMainHand().getType() != sickleType) {
                break;
            }
            if (crop.equals(center) || !isRipe(crop)) {
                continue;
            }
            final Material cropType = crop.getType();
            harvesting.add(crop.getLocation());
            final boolean broken;
            PowerUtils.startSimulating(player);
            try {
                broken = player.breakBlock(crop);
            } finally {
                PowerUtils.stopSimulating(player);
            }
            finishHarvest(crop, cropType);
            if (broken) {
                harvested++;
                if (survival && durabilityPerBlock) {
                    PowerUtils.reduceDurability(player, player.getInventory().getItemInMainHand());
                }
            }
        }
        // Crops break instantly, so vanilla never damages the sickle: one use for the center crop, or for the
        // whole harvest when durability is not counted per block
        if (survival && player.getInventory().getItemInMainHand().getType() == sickleType) {
            PowerUtils.reduceDurability(player, player.getInventory().getItemInMainHand());
        }
        plugin.getDebuggingMessages().sendConsoleMessage("Sickle harvested " + harvested + " extra crops");
    }

    /**
     * Takes one seed from the drops of a crop the sickle harvests, as payment for
     * planting it again.
     *
     * @param event the drop event of a broken block
     */
    @EventHandler(ignoreCancelled = true)
    public void paySeed(BlockDropItemEvent event) {
        final Location location = event.getBlock().getLocation();
        final Material seed = SEEDS.get(event.getBlockState().getType());
        if (seed == null || !harvesting.contains(location)) {
            return;
        }
        final Iterator<Item> drops = event.getItems().iterator();
        while (drops.hasNext()) {
            final Item drop = drops.next();
            final ItemStack stack = drop.getItemStack();
            if (stack.getType() != seed) {
                continue;
            }
            if (stack.getAmount() == 1) {
                drops.remove();
            } else {
                stack.setAmount(stack.getAmount() - 1);
                drop.setItemStack(stack);
            }
            paid.add(location);
            return;
        }
    }

    private void finishHarvest(@Nonnull Block block, @Nonnull Material cropType) {
        final Location location = block.getLocation();
        harvesting.remove(location);
        if (paid.remove(location) && block.getType().isAir()) {
            block.setType(cropType);
        }
    }

    private static boolean isRipe(@Nonnull Block block) {
        return SEEDS.containsKey(block.getType()) && block.getBlockData() instanceof Ageable crop
                && crop.getAge() == crop.getMaximumAge();
    }
}
