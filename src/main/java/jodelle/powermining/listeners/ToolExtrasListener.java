package jodelle.powermining.listeners;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import jodelle.powermining.PowerMining;
import jodelle.powermining.crafting.CraftItem;
import jodelle.powermining.lib.Reference;
import jodelle.powermining.utils.PowerUtils;

/**
 * Smaller PowerTool features that do not belong to one tool: recipe book
 * entries, picking up drops and the netherite upgrade at the smithing table.
 */
public class ToolExtrasListener implements Listener {
    private final PowerMining plugin;

    /**
     * Creates the listener and registers it.
     *
     * @param plugin the plugin instance
     */
    public ToolExtrasListener(@Nonnull PowerMining plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Puts the PowerTool recipes the player may craft into the recipe book, so
     * players find them without reading the documentation.
     *
     * @param event the join event
     */
    @EventHandler
    public void unlockRecipes(PlayerJoinEvent event) {
        if (!plugin.getConfig().getBoolean("unlockRecipes", true) || plugin.getRecipeManager() == null) {
            return;
        }
        final Player player = event.getPlayer();
        final List<NamespacedKey> craftable = new ArrayList<>();
        for (NamespacedKey key : plugin.getRecipeManager().getRegisteredRecipes()) {
            final Recipe recipe = plugin.getServer().getRecipe(key);
            if (recipe == null) {
                continue;
            }
            final String permission = Reference.CRAFT_PERMISSIONS.get(recipe.getResult().getType());
            if (permission != null && player.hasPermission(permission)) {
                craftable.add(key);
            }
        }
        player.discoverRecipes(craftable);
    }

    /**
     * Moves the drops of blocks broken with a PowerTool straight into the
     * inventory. Runs last, so other plugins have changed the drops already.
     * Whatever does not fit stays on the ground.
     *
     * @param event the drop event of a block a player broke
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void pickUpDrops(BlockDropItemEvent event) {
        if (!plugin.getConfig().getBoolean("autoPickup", false)) {
            return;
        }
        final Player player = event.getPlayer();
        if (!PowerUtils.isPowerTool(player.getInventory().getItemInMainHand())
                || PowerUtils.isWorldDisabled(plugin, player.getWorld())) {
            return;
        }
        final Iterator<Item> drops = event.getItems().iterator();
        while (drops.hasNext()) {
            final Item drop = drops.next();
            final Map<Integer, ItemStack> leftover = player.getInventory().addItem(drop.getItemStack());
            if (leftover.isEmpty()) {
                drops.remove();
            } else {
                drop.setItemStack(leftover.values().iterator().next());
            }
        }
    }

    /**
     * Turns the vanilla netherite upgrade of a diamond PowerTool into the
     * matching netherite PowerTool. Enchantments and damage stay, name, lore,
     * item model and mining speed are set for the new tool.
     *
     * @param event the smithing preview event
     */
    @EventHandler
    public void upgradeToNetherite(PrepareSmithingEvent event) {
        final ItemStack base = event.getInventory().getItem(1);
        final ItemStack result = event.getResult();
        if (base == null || result == null || !PowerUtils.isPowerTool(base)) {
            return;
        }
        final String toolName = PowerUtils.getPowerToolName(result.getType());
        if (!plugin.getConfig().getBoolean("netheriteSmithing", true) || toolName == null
                || !toolName.startsWith("NETHERITE_")) {
            // Vanilla would hand out a netherite tool that still carries the name of the diamond PowerTool
            event.setResult(null);
            return;
        }
        final HumanEntity viewer = event.getView().getPlayer();
        final String permission = Reference.CRAFT_PERMISSIONS.get(result.getType());
        if (permission != null && !viewer.hasPermission(permission)) {
            event.setResult(null);
            return;
        }
        final ItemStack upgraded = result.clone();
        new CraftItem(plugin).modifyItemMeta(upgraded, toolName);
        event.setResult(upgraded);
    }
}
