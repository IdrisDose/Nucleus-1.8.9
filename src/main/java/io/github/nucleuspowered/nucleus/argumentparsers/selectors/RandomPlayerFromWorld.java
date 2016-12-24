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

import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Selector to get all players
 */
public class RandomPlayerFromWorld implements SelectorParser<Player> {

    public static final RandomPlayerFromWorld INSTANCE = new RandomPlayerFromWorld();
    private final Pattern pattern = Pattern.compile("^r\\[([\\w-]+)\\]$");
    private final Random random = new Random();

    private RandomPlayerFromWorld() {}

    @Override
    public Pattern selector() {
        return pattern;
    }

    @Override
    public Player get(String selector, CommandSource source, CommandArgs args) throws ArgumentParseException {
        // Get the regex groups.
        Matcher m = pattern.matcher(selector);
        m.matches();
        String world = m.group(1);

        World spongeWorld = Sponge.getServer().getWorld(world).orElseThrow(() -> args.createError(Nucleus.getNucleus().getMessageProvider().getTextMessageWithFormat("args.selector.noworld", world)));

        List<Player> players = Sponge.getServer().getOnlinePlayers().stream().filter(x -> x.getWorld().getUniqueId().equals(spongeWorld.getUniqueId()))
                .filter(x -> !(source instanceof Player) || ((Player) source).getUniqueId().equals(x.getUniqueId()))
                .sorted((x, y) -> x.getName().compareTo(y.getName())).collect(Collectors.toList());
        if (players.isEmpty()) {
            throw args.createError(Nucleus.getNucleus().getMessageProvider().getTextMessageWithFormat("args.selector.notarget"));
        }

        return players.get(random.nextInt(players.size()));
    }
}
