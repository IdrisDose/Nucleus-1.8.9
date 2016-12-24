/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.blacklist.commands;

import io.github.nucleuspowered.nucleus.Util;
import io.github.nucleuspowered.nucleus.configurate.datatypes.item.BlacklistNode;
import io.github.nucleuspowered.nucleus.dataservices.ItemDataService;
import io.github.nucleuspowered.nucleus.internal.annotations.Permissions;
import io.github.nucleuspowered.nucleus.internal.annotations.RegisterCommand;
import io.github.nucleuspowered.nucleus.internal.annotations.RunAsync;
import io.github.nucleuspowered.nucleus.internal.messages.MessageProvider;
import io.github.nucleuspowered.nucleus.internal.permissions.SuggestedLevel;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RunAsync
@Permissions(prefix = "blacklist", suggestedLevel = SuggestedLevel.ADMIN)
@RegisterCommand(value = {"list", "ls"}, subcommandOf = BlacklistCommand.class)
public class BlacklistListCommand extends io.github.nucleuspowered.nucleus.internal.command.AbstractCommand<CommandSource> {

    @Override
    public CommandResult executeCommand(final CommandSource src, CommandContext args) throws Exception {
        ItemDataService itemDataService = this.plugin.getItemDataService();
        Map<String, BlacklistNode> itemsToNodes = itemDataService.getAllBlacklistedItems().entrySet().stream()
            .collect(Collectors.toMap(x -> Util.getTranslatedStringFromItemId(x.getKey()).orElse(x.getKey()), Map.Entry::getValue));

        if (itemsToNodes.isEmpty()) {
            src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.blacklist.list.none"));
            return CommandResult.empty();
        }

        MessageProvider mp = plugin.getMessageProvider();
        Text header = mp.getTextMessageWithFormat("blacklist.title");

        List<Text> lt = new ArrayList<>();
        itemsToNodes.entrySet().stream().sorted(Comparator.comparing(Map.Entry::getKey)).forEach(e -> {
            String types = String.join(", ", new ArrayList<String>() {{
                if (e.getValue().isEnvironment()) {
                    add(mp.getMessageWithFormat("command.blacklist.list.environment"));
                }

                if (e.getValue().isInventory()) {
                    add(mp.getMessageWithFormat("command.blacklist.list.possess"));
                }

                if (e.getValue().isUse()) {
                    add(mp.getMessageWithFormat("command.blacklist.list.use"));
                }
            }});

            lt.add(Text.builder(e.getKey()).color(TextColors.GREEN)
                .onHover(TextActions.showText(plugin.getMessageProvider().getTextMessageWithFormat("blacklist.hover", e.getKey())))
                .append(Text.of(TextColors.YELLOW, " - " + types))
                .build());
        });

        Util.getPaginationBuilder(src).title(Text.of(TextColors.YELLOW, header)).padding(Text.of(TextColors.GREEN, "-")).contents(lt).sendTo(src);
        return CommandResult.success();
    }
}
