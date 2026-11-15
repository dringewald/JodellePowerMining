package jodelle.powermining.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import jodelle.powermining.PowerMining;
import jodelle.powermining.TestServer;
import jodelle.powermining.lib.Reference;

class PowerUtilsTest {

    @BeforeAll
    static void installServer() {
        TestServer.install();
    }

    @AfterEach
    void resetBlockLists() {
        Reference.CUSTOM_BLOCK_LIST = false;
        Reference.MINABLE.clear();
        Reference.DIGGABLE.clear();
    }

    @Test
    void hammerFollowsVanillaToolTiers() {
        assertTrue(PowerUtils.validateHammer(Material.WOODEN_PICKAXE, Material.STONE));
        assertFalse(PowerUtils.validateHammer(Material.WOODEN_PICKAXE, Material.IRON_ORE));
        assertTrue(PowerUtils.validateHammer(Material.COPPER_PICKAXE, Material.IRON_ORE));
        assertFalse(PowerUtils.validateHammer(Material.COPPER_PICKAXE, Material.DIAMOND_ORE));
        assertFalse(PowerUtils.validateHammer(Material.GOLDEN_PICKAXE, Material.IRON_ORE));
        assertTrue(PowerUtils.validateHammer(Material.IRON_PICKAXE, Material.DIAMOND_ORE));
        assertFalse(PowerUtils.validateHammer(Material.IRON_PICKAXE, Material.OBSIDIAN));
        assertTrue(PowerUtils.validateHammer(Material.NETHERITE_PICKAXE, Material.OBSIDIAN));
    }

    @Test
    void hammerRefusesUnbreakableAndForeignBlocks() {
        assertFalse(PowerUtils.validateHammer(Material.NETHERITE_PICKAXE, Material.BEDROCK));
        assertFalse(PowerUtils.validateHammer(Material.DIAMOND_PICKAXE, Material.DIRT));
        assertFalse(PowerUtils.validateHammer(Material.DIAMOND_SHOVEL, Material.STONE));
    }

    @Test
    void excavatorUsesTheShovelTag() {
        assertTrue(PowerUtils.validateExcavator(Material.WOODEN_SHOVEL, Material.GRAVEL));
        assertTrue(PowerUtils.validateExcavator(Material.COPPER_SHOVEL, Material.DIRT));
        assertFalse(PowerUtils.validateExcavator(Material.WOODEN_SHOVEL, Material.STONE));
        assertFalse(PowerUtils.validateExcavator(Material.WOODEN_PICKAXE, Material.DIRT));
    }

    @Test
    void customBlockListReplacesTheTags() {
        Reference.CUSTOM_BLOCK_LIST = true;
        Reference.MINABLE.put(Material.OBSIDIAN, new ArrayList<>(List.of(Material.STONE_PICKAXE)));
        Reference.MINABLE.put(Material.DIRT, null);
        Reference.DIGGABLE.add(Material.STONE);

        assertTrue(PowerUtils.validateHammer(Material.STONE_PICKAXE, Material.OBSIDIAN));
        assertFalse(PowerUtils.validateHammer(Material.DIAMOND_PICKAXE, Material.OBSIDIAN));
        assertTrue(PowerUtils.validateHammer(Material.WOODEN_PICKAXE, Material.DIRT));
        assertFalse(PowerUtils.validateHammer(Material.WOODEN_PICKAXE, Material.STONE));
        assertTrue(PowerUtils.validateExcavator(Material.WOODEN_SHOVEL, Material.STONE));
        assertFalse(PowerUtils.validateExcavator(Material.WOODEN_SHOVEL, Material.GRAVEL));
    }

    @Test
    void powerToolNamesFollowTheVanillaTool() {
        assertEquals("COPPER_HAMMER", PowerUtils.getPowerToolName(Material.COPPER_PICKAXE));
        assertEquals("DIAMOND_EXCAVATOR", PowerUtils.getPowerToolName(Material.DIAMOND_SHOVEL));
        assertEquals("NETHERITE_PLOW", PowerUtils.getPowerToolName(Material.NETHERITE_HOE));
        assertEquals("GOLDEN_SICKLE", PowerUtils.getPowerToolName(Material.GOLDEN_SWORD));
        assertNull(PowerUtils.getPowerToolName(Material.STICK));
    }

    @Test
    void toolSizesOverrideTheGlobalValues() {
        final YamlConfiguration config = new YamlConfiguration();
        config.set("Radius", 3);
        config.set("toolSizes.NETHERITE_HAMMER.Radius", 5);
        final PowerMining plugin = mock(PowerMining.class);
        when(plugin.getConfig()).thenReturn(config);

        assertEquals(5, PowerUtils.getToolSize(plugin, Material.NETHERITE_PICKAXE, "Radius", 2));
        assertEquals(3, PowerUtils.getToolSize(plugin, Material.DIAMOND_PICKAXE, "Radius", 2));
        assertEquals(1, PowerUtils.getToolSize(plugin, Material.NETHERITE_PICKAXE, "Depth", 1));
    }
}
