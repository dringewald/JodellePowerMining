package jodelle.powermining.integrations;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class TextureInstallerTest {

    private static final String TEXTURE = "assets/jodellepowermining/textures/item/diamond_hammer.png";

    private static Path buildJar(Path folder) throws IOException {
        final Path jarPath = folder.resolve("plugin.jar");
        try (OutputStream output = Files.newOutputStream(jarPath);
                JarOutputStream jar = new JarOutputStream(output)) {
            for (String name : new String[] { "resourcepack/" + TEXTURE, "resourcepack/pack.mcmeta",
                    "resourcepack/assets/../../escape.txt", "config.yml" }) {
                jar.putNextEntry(new JarEntry(name));
                jar.write(name.getBytes(StandardCharsets.UTF_8));
                jar.closeEntry();
            }
        }
        return jarPath;
    }

    @Test
    void copiesOnlyChangedFilesIntoTheTarget(@TempDir Path folder) throws IOException {
        final Path target = folder.resolve("ItemsAdder").resolve("contents").resolve("pack").resolve("assets");
        try (JarFile jar = new JarFile(buildJar(folder).toFile())) {
            assertEquals(1, TextureInstaller.copyFolder(jar, "resourcepack/assets/", target));
            final Path texture = target.getParent().resolve(TEXTURE);
            assertArrayEquals(("resourcepack/" + TEXTURE).getBytes(StandardCharsets.UTF_8), Files.readAllBytes(texture));
            assertFalse(Files.exists(target.getParent().resolve("pack.mcmeta")));
            assertFalse(Files.exists(target.getParent().resolve("escape.txt")));

            assertEquals(0, TextureInstaller.copyFolder(jar, "resourcepack/assets/", target));

            Files.writeString(texture, "edited");
            assertEquals(1, TextureInstaller.copyFolder(jar, "resourcepack/assets/", target));
        }
    }

    @Test
    void copiesTheWholePackForResourcePackManager(@TempDir Path folder) throws IOException {
        final Path target = folder.resolve("JodellePowerMining").resolve("resourcepack");
        try (JarFile jar = new JarFile(buildJar(folder).toFile())) {
            assertEquals(2, TextureInstaller.copyFolder(jar, "resourcepack/", target));
            assertTrue(Files.exists(target.resolve("pack.mcmeta")));
            assertTrue(Files.exists(target.resolve(TEXTURE)));
            assertFalse(Files.exists(target.getParent().resolve("escape.txt")));
            assertFalse(Files.exists(target.resolve("config.yml")));
        }
    }
}
