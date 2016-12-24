/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.chat.config;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ChatTemplateConfig {

    @Setting(value = "default-chatcolour", comment = "loc:config.chat.template.chatcolour")
    private String chatcolour = "";

    @Setting(value = "default-chatstyle", comment = "loc:config.chat.template.chatstyle")
    private String chatstyle = "";

    @Setting(value = "default-namecolour", comment = "loc:config.chat.template.namecolour")
    private String namecolour = "";

    @Setting(comment = "loc:config.chat.template.prefix")
    private String prefix = "{{prefix}} {{displayname}}&f: ";

    @Setting(comment = "loc:config.chat.template.suffix")
    private String suffix = " {{suffix}}";

    public String getPrefix() {
        return prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public String getChatcolour() {
        return chatcolour;
    }

    public String getChatstyle() {
        return chatstyle;
    }

    public String getNamecolour() {
        return namecolour;
    }
}
