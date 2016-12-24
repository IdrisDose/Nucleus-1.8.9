/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.internal.command;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.TextMessageException;

/**
 * Simple exception to only send back an error message to the user. Useful in optionals.
 */
public class ReturnMessageException extends TextMessageException {

    public ReturnMessageException(Text text) {
        super(text);
    }
}
