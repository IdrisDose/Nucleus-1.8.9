/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.internal.interfaces;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;

import java.util.regex.Pattern;

/**
 * This interface represents a parser for selectors.
 *
 * @param <T> The type of result that will be returned.
 */
public interface SelectorParser<T> {

    /**
     * The {@link Pattern} that the selector will match, without the "@" symbol
     *
     * @return The selector regular expression
     */
    Pattern selector();

    /**
     * Gets the result based on the selector input.
     * @param selector The raw input for the selector, without the @ symbol
     * @param source The {@link CommandSource}
     * @param args The {@link CommandArgs}, for exception handling.
     * @return The result
     * @throws ArgumentParseException If the selector could not find a target.
     */
    T get(String selector, CommandSource source, CommandArgs args) throws ArgumentParseException;
}
