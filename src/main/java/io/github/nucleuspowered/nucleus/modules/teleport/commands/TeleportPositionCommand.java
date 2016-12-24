/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.teleport.commands;

import io.github.nucleuspowered.nucleus.Nucleus;
import io.github.nucleuspowered.nucleus.argumentparsers.BoundedIntegerArgument;
import io.github.nucleuspowered.nucleus.argumentparsers.SelectorWrapperArgument;
import io.github.nucleuspowered.nucleus.internal.annotations.NoCooldown;
import io.github.nucleuspowered.nucleus.internal.annotations.NoCost;
import io.github.nucleuspowered.nucleus.internal.annotations.NoWarmup;
import io.github.nucleuspowered.nucleus.internal.annotations.Permissions;
import io.github.nucleuspowered.nucleus.internal.annotations.RegisterCommand;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.storage.WorldProperties;

@Permissions(prefix = "teleport", supportsSelectors = true)
@NoWarmup
@NoCooldown
@NoCost
@RegisterCommand({"tppos"})
public class TeleportPositionCommand extends io.github.nucleuspowered.nucleus.internal.command.AbstractCommand<CommandSource> {

    private final String key = "player";
    private final String location = "world";
    private final String x = "x";
    private final String y = "y";
    private final String z = "z";

    @Override
    public CommandElement[] getArguments() {
        return new CommandElement[] {
                GenericArguments.onlyOne(new SelectorWrapperArgument(GenericArguments.playerOrSource(Text.of(key)), permissions, SelectorWrapperArgument.SINGLE_PLAYER_SELECTORS)),
                GenericArguments.onlyOne(GenericArguments.optional(GenericArguments.world(Text.of(location)))),
                GenericArguments.onlyOne(new BoundedIntegerArgument(Text.of(x), Integer.MIN_VALUE, Integer.MAX_VALUE)),
                GenericArguments.onlyOne(new BoundedIntegerArgument(Text.of(y), 0, 255)),
                GenericArguments.onlyOne(new BoundedIntegerArgument(Text.of(z), Integer.MIN_VALUE, Integer.MAX_VALUE)),
                GenericArguments.flags().flag("f").buildWith(GenericArguments.none())
        };
    }

    @Override
    public CommandResult executeCommand(CommandSource src, CommandContext args) throws Exception {
        Player pl = args.<Player>getOne(key).get();
        WorldProperties wp = args.<WorldProperties>getOne(location).orElse(pl.getWorld().getProperties());
        World world = Sponge.getServer().getWorld(wp.getUniqueId()).get();

        int yy = args.<Integer>getOne(y).get();
        if (yy < 0) {
            src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.tppos.ysmall"));
            return CommandResult.empty();
        }

        // Create the location
        Location<World> loc = new Location<>(world, args.<Integer>getOne(x).get(), yy, args.<Integer>getOne(z).get());

        // Don't bother with the safety if the flag is set.
        if (args.<Boolean>getOne("f").orElse(false)) {
            pl.setLocation(loc);
            pl.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.tppos.success.self"));
            if (!src.equals(pl)) {
                src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.tppos.success.other", pl.getName()));
            }

            return CommandResult.success();
        }

        if (Nucleus.getNucleus().getTeleportHandler().teleportPlayer(pl, loc)) {
            pl.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.tppos.success.self"));
            if (!src.equals(pl)) {
                src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.tppos.success.other", pl.getName()));
            }

            return CommandResult.success();
        } else {
            src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.tppos.nosafe"));
            return CommandResult.empty();
        }
    }
}
