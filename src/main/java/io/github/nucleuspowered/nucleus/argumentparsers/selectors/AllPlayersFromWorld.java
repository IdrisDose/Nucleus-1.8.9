/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.argumentparsers.selectors;

import io.github.nucleuspowered.nucleus.Nucleus;
import io.github.nucleuspowered.nucleus.internal.interfaces.SelectorParser;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.World;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Selector to get all players on a given world
 */
public class AllPlayersFromWorld implements SelectorParser<Collection<Player>> {

    public static final AllPlayersFromWorld INSTANCE = new AllPlayersFromWorld();
    private final Pattern pattern = Pattern.compile("^a\\[([\\w-]+)\\]$");

    private AllPlayersFromWorld() {}

    @Override
    public Pattern selector() {
        return pattern;
    }

    @Override
    public Collection<Player> get(String rawSelector, CommandSource source, CommandArgs args) throws ArgumentParseException {
        // Get the regex groups.
        Matcher m = pattern.matcher(rawSelector);
        m.matches();
        String world = m.group(1);

        World spongeWorld = Sponge.getServer().getWorld(world).orElseThrow(() -> args.createError(Nucleus.getNucleus().getMessageProvider().getTextMessageWithFormat("args.selector.noworld", world)));

        return Sponge.getServer().getOnlinePlayers().stream().filter(x -> x.getLocation().getExtent().getUniqueId().equals(spongeWorld.getUniqueId())).collect(Collectors.toList());
    }
}
