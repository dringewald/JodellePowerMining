package jodelle.powermining.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Scanner;

public class SpigotUpdater {
    private static final int SPIGOT_RESOURCE_ID = 12345; // Replace with your actual Spigot resource ID

    public static void checkForUpdates(JavaPlugin plugin) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                // Convert string URL to URI first, then to URL
                URI uri = new URI("https://api.spigotmc.org/legacy/update.php?resource=" + SPIGOT_RESOURCE_ID);
                URL url = uri.toURL();

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                Scanner scanner = new Scanner(new InputStreamReader(connection.getInputStream()));
                String latestVersion = scanner.nextLine();
                scanner.close();

                String currentVersion = plugin.getDescription().getVersion();

                if (!latestVersion.equals(currentVersion)) {
                    Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[PowerMining] A new version (" + latestVersion + ") is available! Download it at: https://www.spigotmc.org/resources/" + SPIGOT_RESOURCE_ID);
                }
            } catch (MalformedURLException e) {
                plugin.getLogger().severe("Invalid update URL: " + e.getMessage());
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to check for updates: " + e.getMessage());
            }
        });
    }
}