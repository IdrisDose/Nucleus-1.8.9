/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.internal.messages;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Optional;

public abstract class MessageProvider {

    public abstract Optional<String> getMessageFromKey(String key);

    public String getMessageWithFormat(String key, String... substitutions) {
        return MessageFormat.format(getMessageFromKey(key).get(), (Object[]) substitutions);
    }

    public Text getTextMessageWithFormat(String key, String... substitutions) {
        return TextSerializers.FORMATTING_CODE.deserialize(MessageFormat.format(getMessageFromKey(key).get(), (Object[]) substitutions));
    }

    public final Text getTextMessageWithTextFormat(String key, Text... substitutions) {
        return getTextMessageWithFormat(key, Arrays.stream(substitutions).map(TextSerializers.FORMATTING_CODE::serialize).toArray(String[]::new));
    }
}
