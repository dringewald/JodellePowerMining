package jodelle.powermining.managers;

import jodelle.powermining.PowerMining;
import jodelle.powermining.lib.Reference;
import org.bukkit.configuration.file.FileConfiguration;
import java.util.*;

/**
 * Handles the migration of the configuration file to ensure compatibility with
 * newer versions of the PowerMining plugin.
 * 
 * <p>
 * This class checks for outdated configuration formats and updates them to the
 * latest version.
 * It ensures that required settings are present, renames deprecated keys, and
 * converts the recipe format
 * to a structured format while enforcing a consistent order of tools.
 * </p>
 */
public class ConfigMigrator {

    private final PowerMining plugin;

    /**
     * The current version of the configuration format.
     */
    private static final String CURRENT_VERSION = "1.3";

    /**
     * Constructs a {@code ConfigMigrator} instance.
     * 
     * @param plugin The instance of {@link PowerMining} responsible for managing
     *               the configuration.
     */
    public ConfigMigrator(PowerMining plugin) {
        this.plugin = plugin;
    }

    /**
     * Checks and migrates the configuration file if it is outdated.
     * 
     * <p>
     * This method ensures that required configuration keys exist, renames
     * deprecated keys,
     * removes old sections, and converts recipes into the new structured format
     * while maintaining
     * a predefined order for tools.
     * </p>
     */
    public void migrateConfig() {
        FileConfiguration config = plugin.getConfig();
        String configVersionStr = config.getString("configVersion", null);
        double configVersion = 0.0;
        if (configVersionStr != null) {
            try {
                configVersion = Double.parseDouble(configVersionStr);
            } catch (NumberFormatException e) {
                plugin.getLogger()
                        .warning("Invalid configVersion format (" + configVersionStr + "). Defaulting version to 0.");
            }
        }

        // If config is already updated, stop migration
        if (configVersion >= 1.3) {
            plugin.getLogger().info("Config is already up-to-date (v" + configVersion + "). No migration needed.");
            return;
        }

        plugin.getLogger().info("Migrating config to version " + CURRENT_VERSION + "...");

        // Add missing keys if not present
        if (!config.contains("language")) {
            config.set("language", "en_US");
            plugin.getLogger().info("Added missing config key: language (default: en_US)");
        }

        if (!config.contains("debug")) {
            config.set("debug", false);
            plugin.getLogger().info("Added missing config key: debug (default: false)");
        }

        // Rename 'Deep' to 'Depth' if 'Deep' exists
        if (config.contains("Deep")) {
            int depthValue = config.getInt("Deep", 0);
            config.set("Depth", depthValue);
            config.set("Deep", null);
            plugin.getLogger().info("Renamed 'Deep' to 'Depth' (Value: " + depthValue + ")");
        }

        if (configVersion < 1.0) {
            // STEP 1: Extract Old Recipes Before Deleting
            List<?> oldRecipes = config.getList("Recipes"); // Store before deleting

            // STEP 2: Remove Old "Recipes" Section
            if (config.contains("Recipes")) {
                config.set("Recipes", null);
                plugin.getLogger().info("Removed old 'Recipes' section.");
            }

            // STEP 3: Convert Old Recipe Format to New Format
            Map<String, Map<String, Object>> newRecipes = new HashMap<>();

            if (oldRecipes != null) {
                for (Object entry : oldRecipes) {
                    if (!(entry instanceof LinkedHashMap))
                        continue;
                    LinkedHashMap<?, ?> rawMap = (LinkedHashMap<?, ?>) entry;

                    for (Map.Entry<?, ?> rawEntry : rawMap.entrySet()) {
                        if (!(rawEntry.getKey() instanceof String) || !(rawEntry.getValue() instanceof List)) {
                            plugin.getLogger().warning("Skipping invalid recipe format: " + rawEntry);
                            continue;
                        }

                        String toolName = (String) rawEntry.getKey();
                        List<?> rawList = (List<?>) rawEntry.getValue();
                        Map<String, String> ingredients = new LinkedHashMap<>();

                        String[] layout = { "", "", "" };
                        for (int i = 0; i < rawList.size(); i++) {
                            String value = (String) rawList.get(i);
                            int row = i / 3;
                            char symbol = (char) ('A' + i);

                            if (value.equalsIgnoreCase("EMPTY") || value.equalsIgnoreCase("AIR")) {
                                ingredients.put(String.valueOf(symbol), "AIR");
                            } else {
                                ingredients.put(String.valueOf(symbol), value);
                            }

                            layout[row] += symbol;
                        }

                        Map<String, Object> orderedRecipeData = new LinkedHashMap<>();
                        orderedRecipeData.put("recipe-ingredients", ingredients);
                        orderedRecipeData.put("recipe-shape", Arrays.asList(layout));

                        newRecipes.put(toolName, orderedRecipeData);
                    }
                }
            }

            // STEP 4: Enforce Tool Order
            List<String> toolOrder = Arrays.asList(
                    "WOODEN_HAMMER", "STONE_HAMMER", "IRON_HAMMER", "GOLDEN_HAMMER", "DIAMOND_HAMMER",
                    "NETHERITE_HAMMER",
                    "WOODEN_EXCAVATOR", "STONE_EXCAVATOR", "IRON_EXCAVATOR", "GOLDEN_EXCAVATOR", "DIAMOND_EXCAVATOR",
                    "NETHERITE_EXCAVATOR",
                    "WOODEN_PLOW", "STONE_PLOW", "IRON_PLOW", "GOLDEN_PLOW", "DIAMOND_PLOW", "NETHERITE_PLOW",
                    "COPPER_HAMMER", "COPPER_EXCAVATOR", "COPPER_PLOW");

            Map<String, Map<String, Object>> migratedRecipes = new LinkedHashMap<>();

            for (String toolName : toolOrder) {
                if (!newRecipes.containsKey(toolName))
                    continue;

                Map<String, Object> recipeData = newRecipes.get(toolName);

                Map<String, Object> orderedRecipeData = new LinkedHashMap<>();
                if (recipeData.containsKey("recipe-ingredients")) {
                    orderedRecipeData.put("recipe-ingredients", recipeData.get("recipe-ingredients"));
                }
                if (recipeData.containsKey("recipe-shape")) {
                    orderedRecipeData.put("recipe-shape", recipeData.get("recipe-shape"));
                }

                migratedRecipes.put(toolName, orderedRecipeData);
            }

            // STEP 5: Store the Correctly Ordered Recipes in "recipes"
            config.set("recipes", migratedRecipes);
            plugin.getLogger().info("Recipes migrated successfully with enforced tool order.");
        }

        if (configVersion < 1.2) {
            // STEP 6: Migrate config to version 1.2 by adding the xp-drops section
            if (!config.contains("xp-drops") || config.getConfigurationSection("xp-drops").getKeys(false).isEmpty()) {
                config.createSection("xp-drops");
                config.set("xp-drops.COAL_ORE.min", 0);
                config.set("xp-drops.COAL_ORE.max", 2);
                config.set("xp-drops.DEEPSLATE_COAL_ORE.min", 0);
                config.set("xp-drops.DEEPSLATE_COAL_ORE.max", 2);
                config.set("xp-drops.DIAMOND_ORE.min", 3);
                config.set("xp-drops.DIAMOND_ORE.max", 7);
                config.set("xp-drops.DEEPSLATE_DIAMOND_ORE.min", 3);
                config.set("xp-drops.DEEPSLATE_DIAMOND_ORE.max", 7);
                config.set("xp-drops.EMERALD_ORE.min", 3);
                config.set("xp-drops.EMERALD_ORE.max", 7);
                config.set("xp-drops.DEEPSLATE_EMERALD_ORE.min", 3);
                config.set("xp-drops.DEEPSLATE_EMERALD_ORE.max", 7);
                config.set("xp-drops.LAPIS_ORE.min", 2);
                config.set("xp-drops.LAPIS_ORE.max", 5);
                config.set("xp-drops.DEEPSLATE_LAPIS_ORE.min", 2);
                config.set("xp-drops.DEEPSLATE_LAPIS_ORE.max", 5);
                config.set("xp-drops.REDSTONE_ORE.min", 1);
                config.set("xp-drops.REDSTONE_ORE.max", 5);
                config.set("xp-drops.DEEPSLATE_REDSTONE_ORE.min", 1);
                config.set("xp-drops.DEEPSLATE_REDSTONE_ORE.max", 5);
                config.set("xp-drops.NETHER_QUARTZ_ORE.min", 2);
                config.set("xp-drops.NETHER_QUARTZ_ORE.max", 5);
                config.set("xp-drops.NETHER_GOLD_ORE.min", 0);
                config.set("xp-drops.NETHER_GOLD_ORE.max", 1);
                config.set("xp-drops.SCULK.min", 1);
                config.set("xp-drops.SCULK.max", 1);
                config.set("xp-drops.SCULK_SENSOR.min", 5);
                config.set("xp-drops.SCULK_SENSOR.max", 5);
                config.set("xp-drops.SCULK_SHRIEKER.min", 5);
                config.set("xp-drops.SCULK_SHRIEKER.max", 5);
                config.set("xp-drops.SCULK_CATALYST.min", 5);
                config.set("xp-drops.SCULK_CATALYST.max", 5);
                config.set("xp-drops.CALIBRATED_SCULK_SENSOR.min", 5);
                config.set("xp-drops.CALIBRATED_SCULK_SENSOR.max", 5);
                config.set("xp-drops.SPAWNER.min", 15);
                config.set("xp-drops.SPAWNER.max", 43);
                plugin.getLogger().info("Added new xp-drops section to config with default values.");
            } else {
                plugin.getLogger().info("xp-drops section already exists, skipping creation.");
            }
        }

        if (configVersion < 1.3) {
            // STEP 7: New options with their defaults
            final Map<String, Object> newOptions = new LinkedHashMap<>();
            newOptions.put("legacyMining", false);
            newOptions.put("customBlockList", false);
            newOptions.put("disabledWorlds", new ArrayList<String>());
            newOptions.put("unlockRecipes", true);
            newOptions.put("autoPickup", false);
            newOptions.put("miningSpeed", 1.0);
            newOptions.put("netheriteSmithing", true);
            newOptions.put("customItemModels", false);
            newOptions.put("installTextures", true);
            for (Map.Entry<String, Object> option : newOptions.entrySet()) {
                // contains(path, true) ignores the defaults of the bundled config, so the keys land in the file
                if (!config.contains(option.getKey(), true)) {
                    config.set(option.getKey(), option.getValue());
                }
            }
            addNewRecipes(config);
            addCopperPickaxeToMinable(config);
        }

        config.set("configVersion", CURRENT_VERSION);
        plugin.getLogger().info("Config version updated to " + CURRENT_VERSION + ".");

        // STEP 6: Save Config (Now That Everything is Set)
        plugin.saveConfig();
        plugin.reloadConfig(); // Reload config, so recipes are correctly loaded
        plugin.getLogger().info("Config migration complete.");
    }

    /**
     * Adds the recipes of the copper tools and the sickles to a config that
     * defines its own recipes. Without a "recipes" section the built in defaults
     * already contain them.
     *
     * @param config the configuration to update
     */
    private void addNewRecipes(FileConfiguration config) {
        if (!config.contains("recipes", true)) {
            return;
        }
        final List<String> newTools = new ArrayList<>(List.of("COPPER_HAMMER", "COPPER_EXCAVATOR", "COPPER_PLOW"));
        newTools.addAll(Reference.SICKLES);
        for (String toolName : newTools) {
            final String path = "recipes." + toolName;
            if (config.contains(path, true)) {
                continue;
            }
            config.set(path + ".recipe-ingredients.I", Reference.getTierIngredient(toolName));
            config.set(path + ".recipe-ingredients.T", vanillaToolOf(toolName));
            config.set(path + ".recipe-ingredients.E", "EMPTY");
            config.set(path + ".recipe-shape", Arrays.asList("EIE", "ITI", "EIE"));
            plugin.getLogger().info("Added recipe for " + toolName + ".");
        }
    }

    private static String vanillaToolOf(String toolName) {
        return toolName.replace("_HAMMER", "_PICKAXE").replace("_EXCAVATOR", "_SHOVEL").replace("_PLOW", "_HOE")
                .replace("_SICKLE", "_SWORD");
    }

    /**
     * Lets the copper pickaxe mine every block of the custom "Minable" list that
     * a stone pickaxe can mine, as in vanilla.
     *
     * @param config the configuration to update
     */
    private void addCopperPickaxeToMinable(FileConfiguration config) {
        final List<?> minable = config.contains("Minable", true) ? config.getList("Minable") : null;
        if (minable == null) {
            return;
        }
        final List<Object> updated = new ArrayList<>();
        for (Object entry : minable) {
            if (!(entry instanceof Map<?, ?> blockEntry)) {
                updated.add(entry);
                continue;
            }
            final Map<Object, Object> copy = new LinkedHashMap<>();
            for (Map.Entry<?, ?> block : blockEntry.entrySet()) {
                Object tools = block.getValue();
                if (tools instanceof List<?> toolList && toolList.contains("STONE_PICKAXE")
                        && !toolList.contains("COPPER_PICKAXE")) {
                    final List<Object> extended = new ArrayList<>(toolList);
                    extended.add(extended.indexOf("STONE_PICKAXE") + 1, "COPPER_PICKAXE");
                    tools = extended;
                }
                copy.put(block.getKey(), tools);
            }
            updated.add(copy);
        }
        config.set("Minable", updated);
    }
}