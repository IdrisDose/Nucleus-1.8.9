/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.dataservices.dataproviders;

import static io.github.nucleuspowered.nucleus.configurate.ConfigurateHelper.setOptions;

import com.google.common.base.Preconditions;
import com.google.common.reflect.TypeToken;
import io.github.nucleuspowered.nucleus.configurate.ConfigurateHelper;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.SimpleCommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.function.Supplier;

public class ConfigurateDataProvider<T> implements DataProvider<T> {

    private final TypeToken<T> typeToken;
    private final ConfigurationLoader<?> loader;
    private final Supplier<T> defaultSupplier;
    private final Path file;
    private final boolean requiresChildren;
    private final Path backupFile;

    public ConfigurateDataProvider(TypeToken<T> type, ConfigurationLoader<?> loader, Path file) {
        this(type, loader, file, true);
    }

    public ConfigurateDataProvider(TypeToken<T> type, ConfigurationLoader<?> loader, Path file, boolean requiresChildren) {
        this(type, loader, () -> {
            try {
                return (T)type.getRawType().newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
                return null;
            }
        }, file, requiresChildren);
    }

    public ConfigurateDataProvider(TypeToken<T> type, ConfigurationLoader<?> loader, Supplier<T> defaultSupplier, Path file, boolean requiresChildren) {
        this.typeToken = type;
        this.loader = loader;
        this.defaultSupplier = defaultSupplier;
        this.file = file;
        this.backupFile = Paths.get(file.toAbsolutePath().toString() + ".bak");
        this.requiresChildren = requiresChildren;
    }

    @Override
    public T load() throws Exception {
        return loader.load(setOptions(getOptions())).getValue(typeToken, defaultSupplier);
    }

    @Override
    public void save(T info) throws Exception {
        Preconditions.checkNotNull(info);
        ConfigurationOptions configurateOptions = ConfigurateHelper.setOptions(loader.getDefaultOptions());
        ConfigurationNode node = SimpleCommentedConfigurationNode.root(configurateOptions).setValue(typeToken, info);
        if (node == null) {
            throw getException("Configuration Node is null.");
        } else if (node.isVirtual()) {
            throw getException("Configuration Node is virtual.");
        } else if (requiresChildren && (!node.hasMapChildren() && !node.hasListChildren())) {
            throw getException("Configuration Node has no children.");
        }

        try {
            if (Files.exists(file)) {
                Files.copy(file, backupFile, StandardCopyOption.REPLACE_EXISTING);
            }

            loader.save(node);
        } catch (IOException e) {
            if (Files.exists(backupFile)) {
                Files.copy(backupFile, file, StandardCopyOption.REPLACE_EXISTING);
            }

            throw e;
        }
    }

    @Override
    public void delete() throws Exception {
        Files.delete(file);
    }

    private ConfigurationOptions getOptions() {
        return setOptions(loader.getDefaultOptions());
    }

    private IllegalStateException getException(String message) {
        return new IllegalStateException("The file " + file.getFileName() + " has not been saved.\n" + message);
    }
}
