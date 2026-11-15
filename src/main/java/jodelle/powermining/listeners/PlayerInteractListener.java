package jodelle.powermining.listeners;

import java.util.HashMap;
import java.util.UUID;

import jodelle.powermining.PowerMining;
import jodelle.powermining.lib.DebuggingMessages;
import jodelle.powermining.utils.PowerUtils;

import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import javax.annotation.Nonnull;

/**
 * Listener for handling {@link PlayerInteractEvent} to track the block face a
 * player interacts with.
 * 
 * <p>
 * This class records the {@link BlockFace} that a player clicks on, allowing
 * other
 * parts of the plugin to retrieve this data. This information is useful for
 * determining
 * the direction in which a player is interacting with blocks, such as for
 * advanced
 * block placement or mining mechanics.
 * </p>
 */
public class PlayerInteractListener implements Listener {
    private final PowerMining plugin;
    private final HashMap<UUID, BlockFace> faces = new HashMap<>();
    private final DebuggingMessages debuggingMessages;

    /**
     * Constructs a {@code PlayerInteractListener} and registers it as an event
     * listener.
     * 
     * <p>
     * Additionally, this method logs a message to confirm that the listener has
     * been successfully registered.
     * </p>
     * 
     * @param plugin The instance of {@link PowerMining} used for event
     *               registration.
     */
    public PlayerInteractListener(@Nonnull PowerMining plugin) {
        debuggingMessages = plugin.getDebuggingMessages();
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
        // Example usage: Log to confirm the listener is being registered
        debuggingMessages.sendConsoleMessage("PlayerInteractListener initialized");
    }

    /**
     * Captures and stores the {@link BlockFace} a player interacts with.
     * 
     * <p>
     * When a player interacts with a block, this method records the block face
     * they clicked on, storing it in a map for later retrieval.
     * </p>
     * 
     * @param event The {@link PlayerInteractEvent} triggered when a player
     *              interacts with a block.
     */
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void saveBlockFace(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        // Interact events fired by the plow for its extra blocks must not overwrite the face the player looks at
        if (PowerUtils.isSimulating(player)) {
            return;
        }
        faces.put(player.getUniqueId(), event.getBlockFace());
    }

    /**
     * Forgets the stored face of a player who leaves, so the map does not grow
     * with every player that ever joined.
     *
     * @param event the quit event
     */
    @EventHandler
    public void forgetBlockFace(PlayerQuitEvent event) {
        faces.remove(event.getPlayer().getUniqueId());
    }

    /**
     * Retrieves the last recorded {@link BlockFace} a player interacted with.
     * 
     * @param playerId The UUID of the player whose block face interaction is
     *                 being retrieved.
     * @return The {@link BlockFace} associated with the player, or {@code null} if
     *         no interaction is recorded.
     */
    public BlockFace getBlockFace(@Nonnull final UUID playerId) {
        return faces.get(playerId);
    }
}
