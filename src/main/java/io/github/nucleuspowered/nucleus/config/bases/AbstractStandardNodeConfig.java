/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.config.bases;

import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

public abstract class AbstractStandardNodeConfig<T extends ConfigurationNode, L extends ConfigurationLoader<T>> extends AbstractConfig<T, L> {

    protected final L loader;
    protected T node;

    protected AbstractStandardNodeConfig(Path file) throws Exception {
        this(file, Maps.newHashMap(), true);
    }

    protected AbstractStandardNodeConfig(Path file, boolean loadNow) throws Exception {
        this(file, Maps.newHashMap(), loadNow);
    }

    protected AbstractStandardNodeConfig(Path file, Map<TypeToken<?>, TypeSerializer<?>> serializerMap, boolean loadNow) throws Exception {
        loader = getLoader(file, serializerMap);
        if (loadNow) {
            load();
        }
    }

    public void save() throws IOException, ObjectMappingException {
        loader.save(node);
    }

    public void load() throws Exception {
        node = loader.load();
        node.mergeValuesFrom(getDefaults());
        save();
    }

    protected abstract T getDefaults();
}
