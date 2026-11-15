package jodelle.powermining.integrations;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.bukkit.plugin.Plugin;

import jodelle.powermining.PowerMining;

/**
 * Hands the textures bundled in the plugin jar to the resource pack plugins that are installed:
 * ItemsAdder gets them copied into its contents folder, ResourcePackManager gets a pack folder
 * in the data folder of this plugin registered through its API.
 */
public final class TextureInstaller {

    static final String JAR_FOLDER = "resourcepack/";

    private TextureInstaller() {
    }

    /**
     * Installs or updates the textures for every supported resource pack plugin, if the option is on.
     *
     * @param plugin    The plugin instance.
     * @param pluginJar The jar file of this plugin, which holds the textures.
     */
    public static void install(PowerMining plugin, File pluginJar) {
        if (!plugin.getConfig().getBoolean("installTextures", true)) {
            return;
        }
        final Plugin itemsAdder = plugin.getServer().getPluginManager().getPlugin("ItemsAdder");
        final boolean resourcePackManager = plugin.getServer().getPluginManager().isPluginEnabled("ResourcePackManager");
        if (itemsAdder == null && !resourcePackManager) {
            return;
        }
        try (JarFile jar = new JarFile(pluginJar)) {
            if (itemsAdder != null) {
                final Path target = itemsAdder.getDataFolder().toPath()
                        .resolve("contents").resolve("jodellepowermining").resolve("resourcepack").resolve("assets");
                logChanges(plugin, copyFolder(jar, JAR_FOLDER + "assets/", target), "ItemsAdder", "/iazip");
            }
            if (resourcePackManager) {
                final Path target = plugin.getDataFolder().toPath().resolve("resourcepack");
                logChanges(plugin, copyFolder(jar, JAR_FOLDER, target), "ResourcePackManager", "/rspm reload");
                ResourcePackManagerHook.register(plugin, target);
            }
        } catch (IOException error) {
            plugin.getLogger().warning("Could not install the textures: " + error.getMessage());
        }
    }

    private static void logChanges(PowerMining plugin, int changed, String target, String command) {
        if (changed > 0) {
            plugin.getLogger().info("Installed " + changed + " texture files for " + target + ". If the pack is not rebuilt"
                    + " on its own, run " + command
                    + (plugin.getConfig().getBoolean("customItemModels") ? "." : ", then set customItemModels: true."));
        }
    }

    /**
     * Writes every file below the given folder of the jar into the target folder,
     * skipping files that are already up to date.
     *
     * @return How many files were written.
     */
    static int copyFolder(JarFile jar, String folder, Path target) throws IOException {
        final Path root = target.toAbsolutePath().normalize();
        int changed = 0;
        for (JarEntry entry : Collections.list(jar.entries())) {
            if (entry.isDirectory() || !entry.getName().startsWith(folder)) {
                continue;
            }
            final Path file = root.resolve(entry.getName().substring(folder.length())).normalize();
            // Never write outside the target folder, even for an entry name with "../"
            if (!file.startsWith(root)) {
                continue;
            }
            final byte[] content;
            try (InputStream input = jar.getInputStream(entry)) {
                content = input.readAllBytes();
            }
            if (Files.isRegularFile(file) && Arrays.equals(Files.readAllBytes(file), content)) {
                continue;
            }
            Files.createDirectories(file.getParent());
            Files.write(file, content);
            changed++;
        }
        return changed;
    }
}
