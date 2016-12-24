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

import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Selector to get all players
 */
public class RandomPlayer implements SelectorParser<Player> {

    public static final RandomPlayer INSTANCE = new RandomPlayer();
    private final Pattern pattern = Pattern.compile("^r$");
    private final Random random = new Random();

    private RandomPlayer() {}

    @Override
    public Pattern selector() {
        return pattern;
    }

    @Override
    public Player get(String selector, CommandSource source, CommandArgs args) throws ArgumentParseException {
        List<Player> players = Sponge.getServer().getOnlinePlayers().stream()
                .filter(x -> !(source instanceof Player) || ((Player) source).getUniqueId().equals(x.getUniqueId()))
                .sorted((x, y) -> x.getName().compareTo(y.getName())).collect(Collectors.toList());
        if (players.isEmpty()) {
            throw args.createError(Nucleus.getNucleus().getMessageProvider().getTextMessageWithFormat("args.selector.notarget"));
        }

        return players.get(random.nextInt(players.size()));
    }
}
