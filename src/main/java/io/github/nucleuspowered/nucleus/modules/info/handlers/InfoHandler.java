/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.info.handlers;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import io.github.nucleuspowered.nucleus.NucleusPlugin;
import io.github.nucleuspowered.nucleus.internal.TextFileController;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.asset.AssetManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InfoHandler {

    private final Map<String, TextFileController> infoFiles = Maps.newHashMap();
    private final NucleusPlugin plugin;
    private final Pattern validFile = Pattern.compile("[a-zA-Z0-9_\\.\\-]+\\.txt", Pattern.CASE_INSENSITIVE);

    public InfoHandler(NucleusPlugin plugin) {
        this.plugin = plugin;
    }

    public Set<String> getInfoSections() {
        return ImmutableSet.copyOf(infoFiles.keySet());
    }

    /**
     * Gets the text associated with the specified key, if it exists.
     *
     * <p>
     *     While it is not normal to return an optional of a collection (usually preferring an empty collection)
     *     in this case, a "not present" value is different to an "empty file" value.
     * </p>
     *
     * @param name The name of the section to retrieve the keys from.
     * @return An {@link Optional} potentially containing the list of strings.
     *
     */
    public Optional<List<String>> getSection(String name) {
        Optional<String> os = infoFiles.keySet().stream().filter(name::equalsIgnoreCase).findFirst();
        if (os.isPresent()) {
            return Optional.of(infoFiles.get(name).getFileContents());
        }

        return Optional.empty();
    }

    public void onReload() throws Exception {
        // Get the config directory, check to see if "info/" exists.
        Path infoDir = plugin.getConfigDirPath().resolve("info");
        if (!Files.exists(infoDir)) {
            Files.createDirectories(infoDir);
            AssetManager am = Sponge.getAssetManager();

            // They exist.
            am.getAsset(plugin, "info.txt").get().copyToFile(infoDir.resolve("info.txt"));
            am.getAsset(plugin, "colors.txt").get().copyToFile(infoDir.resolve("colors.txt"));
            am.getAsset(plugin, "links.txt").get().copyToFile(infoDir.resolve("links.txt"));
        } else if (!Files.isDirectory(infoDir)) {
            throw new IllegalStateException("The file " + infoDir.toAbsolutePath().toString() + " should be a directory.");
        }

        // Get all txt files.
        List<Path> files;
        try (Stream<Path> sp = Files.list(infoDir)) {
            files = sp.filter(Files::isRegularFile)
              .filter(x -> validFile.matcher(x.getFileName().toString()).matches()).collect(Collectors.toList());
        }

        // Collect them and put the resultant controllers into a temporary map.
        Map<String, TextFileController> mst = Maps.newHashMap();
        files.forEach(x -> {
            try {
                String name = x.getFileName().toString();
                name = name.substring(0, name.length() - 4);
                if (mst.keySet().stream().anyMatch(name::equalsIgnoreCase)) {
                    plugin.getLogger().warn(NucleusPlugin.getNucleus().getMessageProvider().getMessageWithFormat("info.load.duplicate", x.getFileName().toString()));

                    // This is a function, so return is appropriate, not break.
                    return;
                }

                mst.put(name, new TextFileController(x));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // All good - replace it all!
        infoFiles.clear();
        infoFiles.putAll(mst);
    }
}
