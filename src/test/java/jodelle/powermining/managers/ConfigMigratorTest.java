package jodelle.powermining.managers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import jodelle.powermining.PowerMining;
import jodelle.powermining.TestServer;

class ConfigMigratorTest {

    private static final String CONFIG_1_2 = String.join("\n",
            "configVersion: '1.2'",
            "language: de_DE",
            "debug: false",
            "Minable:",
            "- STONE:",
            "  - any",
            "- IRON_ORE:",
            "  - STONE_PICKAXE",
            "  - IRON_PICKAXE",
            "recipes:",
            "  STONE_HAMMER:",
            "    recipe-ingredients:",
            "      S: STONE",
            "    recipe-shape:",
            "    - ESE",
            "");

    @BeforeAll
    static void installServer() {
        TestServer.install();
    }

    private static PowerMining pluginWith(YamlConfiguration config) {
        final PowerMining plugin = mock(PowerMining.class);
        when(plugin.getConfig()).thenReturn(config);
        when(plugin.getLogger()).thenReturn(Logger.getLogger("ConfigMigratorTest"));
        return plugin;
    }

    @Test
    void migratesVersion12To13() throws InvalidConfigurationException {
        final YamlConfiguration config = new YamlConfiguration();
        config.loadFromString(CONFIG_1_2);
        final PowerMining plugin = pluginWith(config);

        new ConfigMigrator(plugin).migrateConfig();

        assertEquals("1.3", config.getString("configVersion"));
        assertEquals("de_DE", config.getString("language"));
        assertTrue(config.contains("legacyMining", true));
        assertFalse(config.getBoolean("legacyMining", true));
        assertFalse(config.getBoolean("customBlockList", true));
        assertEquals("STONE", config.getString("recipes.STONE_HAMMER.recipe-ingredients.S"));
        assertEquals("COPPER_INGOT", config.getString("recipes.COPPER_HAMMER.recipe-ingredients.I"));
        assertEquals("COPPER_PICKAXE", config.getString("recipes.COPPER_HAMMER.recipe-ingredients.T"));
        assertEquals("COPPER_HOE", config.getString("recipes.COPPER_PLOW.recipe-ingredients.T"));
        assertEquals(List.of("EIE", "ITI", "EIE"), config.getStringList("recipes.COPPER_EXCAVATOR.recipe-shape"));
        assertEquals("OAK_LOG", config.getString("recipes.WOODEN_SICKLE.recipe-ingredients.I"));
        assertEquals("NETHERITE_SWORD", config.getString("recipes.NETHERITE_SICKLE.recipe-ingredients.T"));
        assertFalse(config.getBoolean("autoPickup", true));
        assertTrue(config.getBoolean("netheriteSmithing", false));
        assertTrue(config.contains("installTextures", true));
        assertEquals(1.0, config.getDouble("miningSpeed", 0));
        assertTrue(config.getStringList("disabledWorlds").isEmpty());

        final List<?> minable = config.getList("Minable");
        assertEquals(List.of("any"), ((Map<?, ?>) minable.get(0)).get("STONE"));
        assertEquals(List.of("STONE_PICKAXE", "COPPER_PICKAXE", "IRON_PICKAXE"),
                ((Map<?, ?>) minable.get(1)).get("IRON_ORE"));
        verify(plugin).saveConfig();
    }

    @Test
    void leavesACurrentConfigAlone() throws InvalidConfigurationException {
        final YamlConfiguration config = new YamlConfiguration();
        config.loadFromString("configVersion: '1.3'\nlegacyMining: true\n");
        final PowerMining plugin = pluginWith(config);

        new ConfigMigrator(plugin).migrateConfig();

        assertTrue(config.getBoolean("legacyMining"));
        verify(plugin, never()).saveConfig();
    }
}
