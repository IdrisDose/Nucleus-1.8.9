/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.config;

import com.google.common.reflect.TypeToken;
import io.github.nucleuspowered.nucleus.config.bases.AbstractStandardNodeConfig;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.commented.SimpleCommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;

import java.nio.file.Path;
import java.util.Map;

public class CommandsConfig extends AbstractStandardNodeConfig<CommentedConfigurationNode, HoconConfigurationLoader> {

    public CommandsConfig(Path file) throws Exception {
        super(file);
    }

    @Override
    protected HoconConfigurationLoader getLoader(Path file, Map<TypeToken<?>, TypeSerializer<?>> typeSerializerList) {
        return HoconConfigurationLoader.builder().setPath(file).build();
    }

    @Override
    protected CommentedConfigurationNode getDefaults() {
        return SimpleCommentedConfigurationNode.root();
    }

    @Override public void load() throws Exception {
        super.load();

        // Check the tp command
        ConfigurationNode n = this.node.getNode("teleport", "use-tp-command");
        if (!n.isVirtual()) {
            this.node.getNode("teleport", "aliases", "tp").setValue(n.getBoolean(true));
            this.node.getNode("teleport").removeChild("use-tp-command");
            save();
        }
    }

    public CommentedConfigurationNode getCommandNode(String command) {
        return node.getNode(command.toLowerCase());
    }

    public void mergeDefaults(CommentedConfigurationNode node) {
        this.node.mergeValuesFrom(node);
    }

    public void mergeDefaultsForCommand(String command, CommentedConfigurationNode node) {
        this.node.getNode(command.toLowerCase()).mergeValuesFrom(node);
    }
}
