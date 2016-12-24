/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.argumentparsers.selectors;

import io.github.nucleuspowered.nucleus.internal.interfaces.SelectorParser;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Collection;
import java.util.regex.Pattern;

/**
 * Selector to get all players
 */
public class AllPlayers implements SelectorParser<Collection<Player>> {

    public static final AllPlayers INSTANCE = new AllPlayers();
    private final Pattern pattern = Pattern.compile("^a$");

    private AllPlayers() {}

    @Override
    public Pattern selector() {
        return pattern;
    }

    @Override
    public Collection<Player> get(String selector, CommandSource source, CommandArgs args) throws ArgumentParseException {
        return Sponge.getServer().getOnlinePlayers();
    }
}
