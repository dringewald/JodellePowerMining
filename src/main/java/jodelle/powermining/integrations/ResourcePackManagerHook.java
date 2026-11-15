package jodelle.powermining.integrations;

import java.nio.file.Path;

import com.magmaguy.resourcepackmanager.api.ResourcePackManagerAPI;

import jodelle.powermining.PowerMining;

/**
 * Registers the texture pack with ResourcePackManager. Kept in its own class, so the
 * ResourcePackManager classes are only loaded when that plugin is enabled.
 */
final class ResourcePackManagerHook {

    private ResourcePackManagerHook() {
    }

    /**
     * Registers the unzipped pack folder, ResourcePackManager zips, merges and hosts it.
     *
     * @param plugin     The plugin instance.
     * @param packFolder The folder that holds pack.mcmeta and assets.
     */
    static void register(PowerMining plugin, Path packFolder) {
        final Path pluginsFolder = plugin.getDataFolder().toPath().getParent();
        // The API expects the path relative to the plugins folder, with forward slashes on every OS
        final String localPath = pluginsFolder.relativize(packFolder).toString().replace('\\', '/');
        ResourcePackManagerAPI.registerLocalResourcePack(plugin.getName(), localPath, false, false, false, "");
    }
}
