/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.config.bases;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

public abstract class AbstractConfig<T extends ConfigurationNode, L extends ConfigurationLoader<T>> {

    public abstract void load() throws Exception;

    public abstract void save() throws IOException, ObjectMappingException;

    protected abstract L getLoader(Path file, Map<TypeToken<?>, TypeSerializer<?>> typeSerializerList);
}
