/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.argumentparsers;

import com.google.common.collect.Lists;
import io.github.nucleuspowered.nucleus.Nucleus;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class PlayerConsoleArgument extends CommandElement {

    private final boolean console;
    private final Supplier<Collection<Player>> onlinePlayersSupplier;

    public PlayerConsoleArgument(@Nullable Text key, boolean console) {
        this(key, console, () -> Sponge.getServer().getOnlinePlayers());
    }

    // For testing.
    public PlayerConsoleArgument(@Nullable Text key, boolean console, @Nonnull Supplier<Collection<Player>> onlinePlayerSupplier) {
        super(key);
        this.console = console;
        this.onlinePlayersSupplier = onlinePlayerSupplier;
    }

    @Nullable
    @Override
    protected Object parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
        String name = args.next().toLowerCase();
        return parseInternal(name, args);
    }

    public List<CommandSource> parseInternal(String name, CommandArgs args) throws ArgumentParseException {
        if (console && name.equals("-")) {
            return Lists.newArrayList(Sponge.getServer().getConsole());
        }

        List<CommandSource> players = onlinePlayersSupplier.get().stream().filter(x -> x.getName().toLowerCase().startsWith(name))
                .sorted((x, y) -> x.getName().compareTo(y.getName())).collect(Collectors.toList());
        if (players.isEmpty()) {
            throw args.createError(Nucleus.getNucleus().getMessageProvider().getTextMessageWithFormat("args.playerconsole.noexist"));
        }

        List<CommandSource> exactUser = players.stream().filter(x -> x.getName().equalsIgnoreCase(name)).collect(Collectors.toList());
        if (exactUser.size() == 1) {
            return exactUser;
        }

        return players;
    }

    @Override
    public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
        try {
            return completeInternal(args.peek().toLowerCase(), args, context);
        } catch (ArgumentParseException e) {
            return completeInternal("", args, context);
        }
    }

    List<String> completeInternal(final String name, CommandArgs args, CommandContext context) {
        List<String> list = Sponge.getServer().getOnlinePlayers().stream().map(User::getName).collect(Collectors.toList());
        // Console.
        if (console) {
            list.add("-");
        }

        return list.stream().filter(x -> x.toLowerCase().startsWith(name.toLowerCase())).collect(Collectors.toList());
    }
}
