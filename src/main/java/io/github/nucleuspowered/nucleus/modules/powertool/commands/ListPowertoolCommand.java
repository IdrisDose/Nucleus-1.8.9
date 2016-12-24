/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.powertool.commands;

import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.dataservices.UserService;
import io.github.nucleuspowered.nucleus.dataservices.loaders.UserDataManager;
import io.github.nucleuspowered.nucleus.internal.annotations.*;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.ClickAction;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Lists the powertools associated with the items.
 *
 * Permission: plugin.powertool.base (uses the base permission)
 */
@Permissions(mainOverride = "powertool")
@RunAsync
@NoCooldown
@NoWarmup
@NoCost
@RegisterCommand(value = {"list", "ls"}, subcommandOf = PowertoolCommand.class)
public class ListPowertoolCommand extends io.github.nucleuspowered.nucleus.internal.command.AbstractCommand<Player> {

    @Inject private UserDataManager loader;
    private PaginationService paginationService = null;

    @Override
    public CommandResult executeCommand(Player src, CommandContext args) throws Exception {
        if (paginationService == null) {
            paginationService = Sponge.getServiceManager().provideUnchecked(PaginationService.class);
        }

        UserService inu = loader.get(src).get();

        boolean toggle = inu.isPowertoolToggled();
        Map<String, List<String>> powertools = inu.getPowertools();

        if (powertools.isEmpty()) {
            src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.powertool.list.none"));
            return CommandResult.success();
        }

        // Generate powertools.
        List<Text> mesl = powertools.entrySet().stream().sorted((a, b) -> a.getKey().compareToIgnoreCase(b.getKey()))
                .map(k -> from(inu, src, k.getKey(), k.getValue())).collect(Collectors.toList());

        // Paginate the tools.
        paginationService.builder().title(plugin.getMessageProvider().getTextMessageWithFormat("command.powertool.list.header", toggle ? "&aenabled" : "&cdisabled"))
                .padding(Text.of(TextColors.YELLOW, "-")).contents(mesl).sendTo(src);

        return CommandResult.success();
    }

    private Text from(final UserService inu, Player src, String powertool, List<String> commands) {
        Optional<ItemType> oit = Sponge.getRegistry().getType(ItemType.class, powertool);

        // Create the click actions.
        ClickAction viewAction = TextActions.executeCallback(pl -> paginationService.builder().title(plugin.getMessageProvider().getTextMessageWithFormat("command.powertool.ind.header", powertool))
                .padding(Text.of(TextColors.GREEN, "-"))
                .contents(commands.stream().map(x -> Text.of(TextColors.YELLOW, x)).collect(Collectors.toList())).sendTo(src));

        ClickAction deleteAction = TextActions.executeCallback(pl -> {
            inu.clearPowertool(powertool);
            pl.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.powertool.removed", powertool));
        });

        TextColor tc = oit.isPresent() ? TextColors.YELLOW : TextColors.GRAY;

        // id - [View] - [Delete]
        return Text.builder().append(Text.of(tc, powertool)).append(Text.of(" - "))
                .append(Text.builder(plugin.getMessageProvider().getMessageWithFormat("standard.view")).color(TextColors.YELLOW).onClick(viewAction).build())
                .append(Text.of(" - "))
                .append(Text.builder(plugin.getMessageProvider().getMessageWithFormat("standard.delete")).color(TextColors.DARK_RED).onClick(deleteAction).build()).build();
    }
}
