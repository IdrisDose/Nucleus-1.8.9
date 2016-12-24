/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.core.commands.itemalias;

import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.argumentparsers.SimpleProviderChoicesArgument;
import io.github.nucleuspowered.nucleus.configurate.datatypes.ItemDataNode;
import io.github.nucleuspowered.nucleus.dataservices.ItemDataService;
import io.github.nucleuspowered.nucleus.internal.annotations.*;
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
@RegisterCommand(value = {"remove", "del"}, subcommandOf = ItemAliasCommand.class)
public class RemoveItemAliasCommand extends io.github.nucleuspowered.nucleus.internal.command.AbstractCommand<CommandSource> {

    @Inject
    private ItemDataService itemDataService;
    private final String alias = "alias";

    @Override
    public CommandElement[] getArguments() {
        return new CommandElement[] {
            GenericArguments.onlyOne(SimpleProviderChoicesArgument.withSetSupplier(Text.of(alias), () -> itemDataService.getAliases()))
        };
    }

    @Override
    public CommandResult executeCommand(CommandSource src, CommandContext args) throws Exception {
        String al = args.<String>getOne(alias).get();
        String id = itemDataService.getIdFromAlias(al).get();
        ItemDataNode node = itemDataService.getDataForItem(id);
        node.removeAlias(al);
        itemDataService.setDataForItem(id, node);

        src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.nucleus.removeitemalias.removed", al, id));
        return CommandResult.success();
    }
}
