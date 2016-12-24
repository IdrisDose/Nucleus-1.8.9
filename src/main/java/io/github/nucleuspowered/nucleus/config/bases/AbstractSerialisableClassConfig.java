/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.config.bases;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Base class that simplifies saving a class annotated with {@link ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable}
 *
 * @param <D> The class to serialise.
 * @param <T> The type of configuration node to use.
 * @param <L> The {@link ConfigurationLoader} to use.
 */
public abstract class AbstractSerialisableClassConfig<D, T extends ConfigurationNode, L extends ConfigurationLoader<T>> extends AbstractConfig<T, L> {

    private final TypeToken<D> dataType;
    protected final L loader;
    private final Supplier<D> defaultData;
    protected D data;

    public AbstractSerialisableClassConfig(Path file, TypeToken<D> dataType, Supplier<D> defaultData) throws Exception {
        this(file, dataType, defaultData, true);
    }

    public AbstractSerialisableClassConfig(Path file, TypeToken<D> dataType, Supplier<D> defaultData, boolean load) throws Exception {
        this(file, dataType, defaultData, load, Maps.newHashMap());
    }

    public AbstractSerialisableClassConfig(Path file, TypeToken<D> dataType, Supplier<D> defaultData, boolean load, Map<TypeToken<?>, TypeSerializer<?>> lts) throws Exception {
        Preconditions.checkNotNull(file);
        Preconditions.checkNotNull(dataType);
        Preconditions.checkNotNull(defaultData);

        this.loader = getLoader(file, lts);
        this.dataType = dataType;
        this.defaultData = defaultData;

        if (load) {
            this.load();
        }
    }

    public void load() throws Exception {
        T node = loader.load();
        data = node.getValue(dataType, defaultData);
    }

    public void save() throws ObjectMappingException, IOException {
        T node = getNode();
        node.setValue(dataType, data);
        loader.save(node);
    }

    protected abstract T getNode();
}
