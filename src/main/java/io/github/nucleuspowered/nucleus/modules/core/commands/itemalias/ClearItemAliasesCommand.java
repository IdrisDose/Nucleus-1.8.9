/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.core.commands.itemalias;

import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.argumentparsers.ItemAliasArgument;
import io.github.nucleuspowered.nucleus.configurate.datatypes.ItemDataNode;
import io.github.nucleuspowered.nucleus.dataservices.ItemDataService;
import io.github.nucleuspowered.nucleus.internal.annotations.*;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.text.Text;

@RunAsync
@NoCooldown
@NoCost
@NoWarmup
@Permissions(prefix = "nucleus.itemalias")
@RegisterCommand(value = {"clear"}, subcommandOf = ItemAliasCommand.class)
public class ClearItemAliasesCommand extends io.github.nucleuspowered.nucleus.internal.command.AbstractCommand<CommandSource> {

    @Inject
    private ItemDataService itemDataService;
    private final String item = "item";

    @Override
    public CommandElement[] getArguments() {
        return new CommandElement[] {
            GenericArguments.onlyOne(new ItemAliasArgument(Text.of(item), itemDataService))
        };
    }

    @Override
    public CommandResult executeCommand(CommandSource src, CommandContext args) throws Exception {
        CatalogType al = args.<CatalogType>getOne(item).get();
        String id = al.getId().toLowerCase();
        ItemDataNode node = itemDataService.getDataForItem(id);
        node.clearAliases();
        itemDataService.setDataForItem(id, node);

        src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.nucleus.removeitemalias.cleared", id));
        return CommandResult.success();
    }
}
