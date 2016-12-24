/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.chat.config;

import com.google.common.collect.Lists;
import io.github.nucleuspowered.nucleus.internal.qsml.NucleusConfigAdapter;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;

import java.util.List;

public class ChatConfigAdapter extends NucleusConfigAdapter.StandardWithSimpleDefault<ChatConfig> {

    public ChatConfigAdapter() {
        super(ChatConfig.class);
    }

    @Override
    protected List<Transformation> getTransformations() {
        return Lists.newArrayList(
            new Transformation(new Object[] {"template"}, ((inputPath, valueAtPath) -> new Object[] { "templates", "default" })),
            new Transformation(new Object[] {"group-templates"}, ((inputPath, valueAtPath) -> new Object[] { "templates", "group-templates" })),
            new Transformation(new Object[] {"modifychat"}, ((inputPath, valueAtPath) -> new Object[] { "modify-chat" })),
            new Transformation(new Object[] {"templates", "group-templates"}, (((inputPath, valueAtPath) -> {
                if (valueAtPath instanceof CommentedConfigurationNode) {
                    CommentedConfigurationNode ccn = (CommentedConfigurationNode)valueAtPath;
                    ccn.getComment().ifPresent(x -> {
                        if (x.equals("Group templates override the default chat template based on the users group. Note that the group name is case sensitive.")) {
                            ccn.setComment(null);
                        }
                    });
                }

                return null;
            }))));
    }
}
