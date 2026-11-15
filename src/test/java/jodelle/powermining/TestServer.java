package jodelle.powermining;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Server;
import org.bukkit.Tag;
import org.bukkit.block.BlockType;

/**
 * A fake Bukkit server for unit tests: block tags, registries and block
 * hardness are answered from small tables, everything else is a plain mock.
 */
public final class TestServer {

    private TestServer() {
    }

    /**
     * A small slice of the vanilla block tags, enough to tell the tool tiers
     * apart. Bedrock is put into the pickaxe tag on purpose to check that
     * unbreakable blocks are refused anyway.
     */
    private static final Map<String, Set<Material>> TAGS = Map.of(
            "mineable/pickaxe", Set.of(Material.STONE, Material.IRON_ORE, Material.DIAMOND_ORE, Material.OBSIDIAN,
                    Material.BEDROCK),
            "mineable/shovel", Set.of(Material.DIRT, Material.GRAVEL),
            "incorrect_for_wooden_tool", Set.of(Material.IRON_ORE, Material.DIAMOND_ORE, Material.OBSIDIAN),
            "incorrect_for_stone_tool", Set.of(Material.DIAMOND_ORE, Material.OBSIDIAN),
            "incorrect_for_copper_tool", Set.of(Material.DIAMOND_ORE, Material.OBSIDIAN),
            "incorrect_for_iron_tool", Set.of(Material.OBSIDIAN),
            "incorrect_for_gold_tool", Set.of(Material.IRON_ORE, Material.DIAMOND_ORE, Material.OBSIDIAN));

    /**
     * Installs the fake server once per test run.
     */
    public static void install() {
        if (Bukkit.getServer() != null) {
            return;
        }
        final Server server = mock(Server.class);
        when(server.getLogger()).thenReturn(Logger.getLogger("TestServer"));
        when(server.getTag(anyString(), any(), any())).thenAnswer(invocation -> fakeTag(
                TAGS.getOrDefault(invocation.<NamespacedKey>getArgument(1).getKey(), Set.of())));
        when(server.getRegistry(any())).thenAnswer(invocation -> fakeRegistry(invocation.getArgument(0)));
        Bukkit.setServer(server);
    }

    @SuppressWarnings("unchecked")
    private static Tag<Keyed> fakeTag(Set<Material> values) {
        final Tag<Keyed> tag = mock(Tag.class);
        when(tag.isTagged(any())).thenAnswer(invocation -> values.contains(invocation.getArgument(0)));
        return tag;
    }

    @SuppressWarnings("unchecked")
    private static Registry<Keyed> fakeRegistry(Class<?> type) {
        final Registry<Keyed> registry = mock(Registry.class);
        when(registry.get(any(NamespacedKey.class))).thenAnswer(invocation -> fakeEntry(type,
                invocation.getArgument(0)));
        return registry;
    }

    private static Object fakeEntry(Class<?> type, NamespacedKey key) {
        if (type != BlockType.class) {
            return mock(type);
        }
        final BlockType block = mock(BlockType.class);
        when(block.getHardness()).thenReturn(key.getKey().equals("bedrock") ? -1f : 1.5f);
        return block;
    }
}
