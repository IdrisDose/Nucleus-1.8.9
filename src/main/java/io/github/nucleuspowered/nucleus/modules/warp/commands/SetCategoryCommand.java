/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.warp.commands;

import io.github.nucleuspowered.nucleus.argumentparsers.WarpArgument;
import io.github.nucleuspowered.nucleus.internal.annotations.NoCooldown;
import io.github.nucleuspowered.nucleus.internal.annotations.NoCost;
import io.github.nucleuspowered.nucleus.internal.annotations.NoWarmup;
import io.github.nucleuspowered.nucleus.internal.annotations.Permissions;
import io.github.nucleuspowered.nucleus.internal.annotations.RegisterCommand;
import io.github.nucleuspowered.nucleus.internal.annotations.RunAsync;
import io.github.nucleuspowered.nucleus.internal.command.AbstractCommand;
import io.github.nucleuspowered.nucleus.internal.command.ReturnMessageException;
import io.github.nucleuspowered.nucleus.modules.warp.config.WarpConfigAdapter;
import io.github.nucleuspowered.nucleus.modules.warp.handlers.WarpHandler;
import io.github.nucleuspowered.nucleus.util.Tuples;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.util.Tuple;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;

@RunAsync
@NoCost
@NoCooldown
@NoWarmup
@Permissions(prefix = "warp")
@RegisterCommand(value = {"setcategory"}, subcommandOf = WarpCommand.class)
public class SetCategoryCommand extends AbstractCommand<CommandSource> {

    private final String warpKey = "warp";
    private final String categoryKey = "category";
    @Inject private WarpConfigAdapter warpConfigAdapter;
    @Inject private WarpHandler handler;

    @Override public CommandElement[] getArguments() {
        return new CommandElement[] {
            GenericArguments.flags().flag("r", "-remove", "-delete").flag("n", "-new").buildWith(
                GenericArguments.seq(
                    new WarpArgument(Text.of(warpKey), warpConfigAdapter, false, false, false),
                    GenericArguments.optional(new WarpCategoryArgument(Text.of(categoryKey)))
                )
            )
        };
    }

    @Override public CommandResult executeCommand(CommandSource src, CommandContext args) throws Exception {
        String warpName = args.<WarpArgument.Result>getOne(warpKey).get().warp;
        if (args.hasAny("r")) {
            // Remove the category.
            if (handler.setWarpCategory(warpName, null)) {
                src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.warp.category.removed", warpName));
                return CommandResult.success();
            }

            throw new ReturnMessageException(plugin.getMessageProvider().getTextMessageWithFormat("command.warp.category.noremove", warpName));
        }

        Optional<Tuple<String, Boolean>> categoryOp = args.getOne(categoryKey);
        if (!categoryOp.isPresent()) {
            throw new ReturnMessageException(plugin.getMessageProvider().getTextMessageWithFormat("command.warp.category.required"));
        }

        Tuple<String, Boolean> category = categoryOp.get();
        if (!args.hasAny("n") && !category.getSecond()) {
            src.sendMessage(
                plugin.getMessageProvider().getTextMessageWithFormat("command.warp.category.requirenew", category.getFirst())
                    .toBuilder().onClick(TextActions.runCommand("/warp setcategory -n " + warpName + " " + category.getFirst())).build()
            );

            return CommandResult.empty();
        }

        // Remove the category.
        if (handler.setWarpCategory(warpName, category.getFirst())) {
            src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.warp.category.added", category.getFirst(), warpName));
            return CommandResult.success();
        }

        throw new ReturnMessageException(plugin.getMessageProvider().getTextMessageWithFormat("command.warp.category.couldnotadd", category.getFirst(), warpName));
    }

    private class WarpCategoryArgument extends CommandElement {

        private WarpCategoryArgument(@Nullable Text key) {
            super(key);
        }

        @Nullable @Override protected Object parseValue(@Nonnull CommandSource source, @Nonnull CommandArgs args) throws ArgumentParseException {
            String arg = args.next();
            return Tuples.of(arg, handler.getCategorisedWarps().keySet().contains(arg));
        }

        @Nonnull @Override public List<String> complete(@Nonnull CommandSource src, @Nonnull CommandArgs args, @Nonnull CommandContext context) {
            return handler.getCategorisedWarps().keySet().stream().filter(x -> x == null).collect(Collectors.toList());
        }
    }
}
