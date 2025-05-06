package jodelle.powermining.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

public class GitHubUpdater {
    private static final String GITHUB_API_URL = "https://api.github.com/repos/dringewald/JodellePowerMining/releases/latest";

    public static void checkForUpdates(JavaPlugin plugin) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                // Convert String URL to URI first, then to URL (avoiding deprecated constructor)
                URI uri = new URI(GITHUB_API_URL);
                URL url = uri.toURL();

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("User-Agent", "Mozilla/5.0");

                if (connection.getResponseCode() != 200) {
                    plugin.getLogger().warning("Could not fetch update information. HTTP Response: " + connection.getResponseCode());
                    return;
                }

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                // Extract version from JSON response
                String latestVersion = response.toString().split("\"tag_name\":\"")[1].split("\"")[0];
                String currentVersion = plugin.getDescription().getVersion();

                if (!latestVersion.equalsIgnoreCase(currentVersion)) {
                    Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[PowerMining] A new version (" + latestVersion + ") is available! Download it at: https://github.com/dringewald/JodellePowerMining/releases");
                }
            } catch (MalformedURLException e) {
                plugin.getLogger().severe("Invalid update URL: " + e.getMessage());
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to check for updates: " + e.getMessage());
            }
        });
    }
}
