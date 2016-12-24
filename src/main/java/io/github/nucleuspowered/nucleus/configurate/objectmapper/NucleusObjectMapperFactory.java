/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.configurate.objectmapper;

import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import ninja.leaping.configurate.objectmapping.ObjectMapperFactory;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

import java.util.concurrent.ExecutionException;

public class NucleusObjectMapperFactory implements ObjectMapperFactory {

    private static final NucleusObjectMapperFactory INSTANCE = new NucleusObjectMapperFactory();
    private final LoadingCache<Class<?>, NucleusObjectMapper<?>> mapperCache = CacheBuilder.newBuilder().weakKeys()
            .maximumSize(500).build(new CacheLoader<Class<?>, NucleusObjectMapper<?>>() {
                @Override
                public NucleusObjectMapper<?> load(Class<?> key) throws Exception {
                    return new NucleusObjectMapper<>(key);
                }
            });

    @Override
    @SuppressWarnings("unchecked")
    public <T> NucleusObjectMapper<T> getMapper(Class<T> type) throws ObjectMappingException {
        Preconditions.checkNotNull(type, "type");
        try {
            return (NucleusObjectMapper<T>) mapperCache.get(type);
        } catch (ExecutionException e) {
            if (e.getCause() instanceof ObjectMappingException) {
                throw (ObjectMappingException) e.getCause();
            } else {
                throw new ObjectMappingException(e);
            }
        }
    }

    public static ObjectMapperFactory getInstance() {
        return INSTANCE;
    }
}
