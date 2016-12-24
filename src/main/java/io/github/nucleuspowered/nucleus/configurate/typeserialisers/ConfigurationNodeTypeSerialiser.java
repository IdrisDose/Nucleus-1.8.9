/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.configurate.typeserialisers;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;

/**
 * I'm probably not using configurate the way it's meant to be used...
 */
public class ConfigurationNodeTypeSerialiser implements TypeSerializer<ConfigurationNode> {

    @Override
    public ConfigurationNode deserialize(TypeToken<?> type, ConfigurationNode value) throws ObjectMappingException {
        return value;
    }

    @Override
    public void serialize(TypeToken<?> type, ConfigurationNode obj, ConfigurationNode value) throws ObjectMappingException {
        value.setValue(obj);
    }
}
