/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.internal.qsml;

import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.NucleusPlugin;
import io.github.nucleuspowered.nucleus.configurate.ConfigurateHelper;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.SimpleCommentedConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import uk.co.drnaylor.quickstart.config.AbstractAdaptableConfig;
import uk.co.drnaylor.quickstart.config.AbstractConfigAdapter;

public abstract class NucleusConfigAdapter<R> extends AbstractConfigAdapter<R> {

    @Inject private NucleusPlugin plugin;

    @Override
    @SuppressWarnings("unchecked")
    public void onAttach(String module, AbstractAdaptableConfig<?, ?> adapter) {
        plugin.preInjectorUpdate((Class)this.getClass(), this);
    }

    public final R getNodeOrDefault() {
        try {
            R node = getNode();
            if (node != null) {
                return node;
            }
        } catch (ObjectMappingException e) {
            //
        }

        return getDefaultObject();
    }

    protected abstract R getDefaultObject();

    @Override
    @SuppressWarnings("unchecked")
    protected ConfigurationNode generateDefaults(ConfigurationNode node) {
        R o = getDefaultObject();
        try {
            return node.setValue(TypeToken.of((Class<R>)o.getClass()), o);
        } catch (ObjectMappingException e) {
            e.printStackTrace();
            return node;
        }
    }

    public abstract static class Standard<R> extends NucleusConfigAdapter<R> {

        final TypeToken<R> typeToken;

        public Standard(Class<R> clazz) {
            this(TypeToken.of(clazz));
        }

        public Standard(TypeToken<R> typeToken) {
            this.typeToken = typeToken;
        }

        @Override
        protected R convertFromConfigurateNode(ConfigurationNode node) throws ObjectMappingException {
            return node.getValue(typeToken, getDefaultObject());
        }

        @Override
        protected ConfigurationNode insertIntoConfigurateNode(ConfigurationNode newNode, R data) throws ObjectMappingException {
            return newNode.setValue(typeToken, data);
        }
    }

    public abstract static class StandardWithSimpleDefault<R> extends NucleusConfigAdapter.Standard<R> {

        public StandardWithSimpleDefault(Class<R> clazz) {
            super(clazz);
        }

        public StandardWithSimpleDefault(TypeToken<R> typeToken) {
            super(typeToken);
        }

        @Override
        protected R getDefaultObject() {
            try {
                return (R) typeToken.getRawType().newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
