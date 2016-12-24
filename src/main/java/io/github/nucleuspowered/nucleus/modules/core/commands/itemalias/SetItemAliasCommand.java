/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.core.commands.itemalias;

import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.argumentparsers.ItemAliasArgument;
import io.github.nucleuspowered.nucleus.configurate.datatypes.ItemDataNode;
import io.github.nucleuspowered.nucleus.dataservices.ItemDataService;
import io.github.nucleuspowered.nucleus.internal.annotations.NoCooldown;
import io.github.nucleuspowered.nucleus.internal.annotations.NoCost;
import io.github.nucleuspowered.nucleus.internal.annotations.NoWarmup;
import io.github.nucleuspowered.nucleus.internal.annotations.Permissions;
import io.github.nucleuspowered.nucleus.internal.annotations.RegisterCommand;
import io.github.nucleuspowered.nucleus.internal.annotations.RunAsync;
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
@RegisterCommand(value = "set", subcommandOf = ItemAliasCommand.class)
public class SetItemAliasCommand extends io.github.nucleuspowered.nucleus.internal.command.AbstractCommand<CommandSource> {

    @Inject private ItemDataService itemDataService;

    private final String item = "item";
    private final String alias = "alias";

    @Override
    public CommandElement[] getArguments() {
        return new CommandElement[] {
            GenericArguments.optionalWeak(new ItemAliasArgument(Text.of(item), itemDataService)),
            GenericArguments.string(Text.of(alias))
        };
    }

    @Override
    public CommandResult executeCommand(CommandSource src, CommandContext args) throws Exception {
        // Do we have an item or blockstate?
        String a = args.<String>getOne(alias).get().toLowerCase();
        if (itemDataService.getIdFromAlias(a).isPresent()) {
            src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.nucleus.setitemalias.inuse", a));
            return CommandResult.empty();
        }

        if (!ItemDataNode.ALIAS_PATTERN.matcher(a).matches()) {
            src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.nucleus.setitemalias.notvalid", a));
            return CommandResult.empty();
        }

        CatalogType type = getCatalogTypeFromHandOrArgs(src, item, args);

        // Set the alias.
        String id = type.getId().toLowerCase();
        ItemDataNode idn = itemDataService.getDataForItem(id);
        idn.addAlias(a);
        itemDataService.setDataForItem(id, idn);

        // Tell the user
        src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.nucleus.setitemalias.success", a, id));
        return CommandResult.success();
    }
}
