/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.warn.commands;

import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.Util;
import io.github.nucleuspowered.nucleus.api.data.WarnData;
import io.github.nucleuspowered.nucleus.internal.annotations.*;
import io.github.nucleuspowered.nucleus.internal.permissions.SuggestedLevel;
import io.github.nucleuspowered.nucleus.modules.warn.handlers.WarnHandler;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Checks the warnings of a player.
 *
 * Command Usage: /checkwarnings user Permission: plugin.checkwarnings.base
 */
@Permissions(suggestedLevel = SuggestedLevel.MOD)
@RunAsync
@NoWarmup
@NoCooldown
@NoCost
@RegisterCommand({"checkwarnings", "checkwarn", "warnings"})
public class CheckWarningsCommand extends io.github.nucleuspowered.nucleus.internal.command.AbstractCommand<CommandSource> {

    @Inject private WarnHandler handler;
    private final String playerKey = "player";

    @Override
    public CommandElement[] getArguments() {
        return new CommandElement[] {GenericArguments.flags().flag("-all", "a").flag("-expired", "e").buildWith(
                GenericArguments.onlyOne(GenericArguments.user(Text.of(playerKey))))};
    }

    @Override
    public CommandResult executeCommand(CommandSource src, CommandContext args) throws Exception {
        User user = args.<User>getOne(playerKey).get();

        handler.updateWarnings(user);
        List<WarnData> warnings;
        final List<WarnData> allWarnings = handler.getWarnings(user);
        if (args.hasAny("all")) {
            warnings = allWarnings;
        } else if (args.hasAny("expired")) {
            warnings = allWarnings.stream().filter(WarnData::isExpired).collect(Collectors.toList());
        } else {
            warnings = allWarnings.stream().filter(x -> !x.isExpired()).collect(Collectors.toList());
        }

        if (warnings.isEmpty()) {
            src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.checkwarnings.none", user.getName()));
            return CommandResult.success();
        }

        List<Text> messages = warnings.stream().sorted((a, b) -> a.getDate().compareTo(b.getDate())).map(x -> createMessage(allWarnings, x, user)).collect(Collectors.toList());
        messages.add(0, plugin.getMessageProvider().getTextMessageWithFormat("command.checkwarnings.info"));

        PaginationService paginationService = Sponge.getGame().getServiceManager().provideUnchecked(PaginationService.class);
        paginationService.builder()
                .title(
                        Text.builder()
                                .color(TextColors.GOLD)
                                .append(Text.of(plugin.getMessageProvider().getMessageWithFormat("command.checkwarnings.header", user.getName())))
                                .build())
                .padding(
                        Text.builder()
                                .color(TextColors.YELLOW)
                                .append(Text.of("="))
                                .build())
                .contents(messages)
                .sendTo(src);

        return CommandResult.success();
    }

    private Text createMessage(List<WarnData> allData, WarnData warning, User user) {
        String name;
        if (warning.getWarner().equals(Util.consoleFakeUUID)) {
            name = Sponge.getServer().getConsole().getName();
        } else {
            Optional<User> ou = Sponge.getServiceManager().provideUnchecked(UserStorageService.class).get(warning.getWarner());
            name = ou.isPresent() ? ou.get().getName() : plugin.getMessageProvider().getMessageWithFormat("standard.unknown");
        }

        //Get the remaining length of the warning
        String time;
        if (warning.getEndTimestamp().isPresent()) {
            time = Util.getTimeStringFromSeconds(Instant.now().until(warning.getEndTimestamp().get(), ChronoUnit.SECONDS));
        } else if (warning.getTimeFromNextLogin().isPresent()) {
            time = Util.getTimeStringFromSeconds(warning.getTimeFromNextLogin().get().getSeconds());
        } else {
            time = plugin.getMessageProvider().getMessageWithFormat("standard.restoftime");
        }

        //Get the ID of the warning, its index in the users List<WarnData>
        int id = allData.indexOf(warning) + 1;

        //Action buttons, for a non expired warning this should look like 'Action > [Delete] - [Expire] - [Return] <'
        Text.Builder actions = plugin.getMessageProvider().getTextMessageWithFormat("command.checkwarnings.action").toBuilder();

        //Add separation between the word 'Action' and action buttons
        actions.append(Text.of(TextColors.GOLD, " > "));

        //Add the delete button [Delete]
        actions.append(Text.builder().append(Text.of(TextColors.RED, plugin.getMessageProvider().getMessageWithFormat("standard.action.delete")))
                .onHover(TextActions.showText(plugin.getMessageProvider().getTextMessageWithFormat("command.checkwarnings.hover.delete")))
                .onClick(TextActions.runCommand("/removewarning --remove " + user.getName() + " " + id))
                .build());

        //Add a - to separate it from the next action button
        actions.append(Text.of(TextColors.GOLD, " - "));

        //Add the expire button if the warning isn't expired [Expire]
        if (!warning.isExpired()) {
            actions.append(Text.builder().append(Text.of(TextColors.YELLOW, plugin.getMessageProvider().getMessageWithFormat("standard.action.expire")))
                    .onHover(TextActions.showText(plugin.getMessageProvider().getTextMessageWithFormat("command.checkwarnings.hover.expire")))
                    .onClick(TextActions.runCommand("/removewarning " + user.getName() + " " + id))
                    .build());

            //Add a - to separate it from the next action button
            actions.append(Text.of(TextColors.GOLD, " - "));
        }

        //Add the return button [Return]
        actions.append(Text.builder().append(Text.of(TextColors.GREEN, plugin.getMessageProvider().getMessageWithFormat("standard.action.return")))
                .onHover(TextActions.showText(plugin.getMessageProvider().getTextMessageWithFormat("command.checkwarnings.hover.return")))
                .onClick(TextActions.runCommand("/checkwarnings " + user.getName()))
                .build());

        //Add a < to end the actions button list
        actions.append(Text.of(TextColors.GOLD, " < "));

        //Get and format the date of the warning
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MMM dd, yyyy").withZone(ZoneId.systemDefault());
        String date = dtf.format(warning.getDate());

        //Create a clickable name providing more information about the warning
        Text.Builder information = Text.builder(name)
                .onHover(TextActions.showText(plugin.getMessageProvider().getTextMessageWithFormat("command.checkwarnings.hover.check")))
                .onClick(TextActions.executeCallback(commandSource -> {
                    commandSource.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.checkwarnings.id", String.valueOf(id)));
                    commandSource.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.checkwarnings.date", date));
                    commandSource.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.checkwarnings.remaining", time));
                    commandSource.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.checkwarnings.warner", name));
                    commandSource.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.checkwarnings.warning", warning.getReason()));
                    commandSource.sendMessage(actions.build());
                }));

        //Create the warning message
        Text.Builder message = Text.builder()
                .append(Text.of(TextColors.GREEN, information.build()))
                .append(Text.of(": "))
                .append(Text.of(TextColors.YELLOW, warning.getReason()));

        //Add the remaining length of the warning
        if (warning.isExpired()) {
            message.append(Text.of(TextColors.GRAY, " " + plugin.getMessageProvider().getMessageWithFormat("standard.status.expired")));
        } else {
            message.append(Text.of(TextColors.GREEN, " " + plugin.getMessageProvider().getMessageWithFormat("standard.for") + " "));
            if (Character.isLetter(time.charAt(0))) {
                message.append(Text.of(TextColors.YELLOW, time.substring(0, 1).toLowerCase() + time.substring(1)));
            } else {
                message.append(Text.of(TextColors.YELLOW, time));
            }
        }

        return message.build();
    }
}
