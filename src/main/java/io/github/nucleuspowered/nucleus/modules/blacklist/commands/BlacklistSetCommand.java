/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.blacklist.commands;

import com.google.common.collect.Lists;
import io.github.nucleuspowered.nucleus.Util;
import io.github.nucleuspowered.nucleus.argumentparsers.ItemAliasArgument;
import io.github.nucleuspowered.nucleus.configurate.datatypes.ItemDataNode;
import io.github.nucleuspowered.nucleus.dataservices.ItemDataService;
import io.github.nucleuspowered.nucleus.internal.annotations.Permissions;
import io.github.nucleuspowered.nucleus.internal.annotations.RegisterCommand;
import io.github.nucleuspowered.nucleus.internal.command.AbstractCommand;
import io.github.nucleuspowered.nucleus.internal.messages.MessageProvider;
import io.github.nucleuspowered.nucleus.internal.permissions.SuggestedLevel;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.text.Text;

import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import javax.inject.Inject;

@Permissions(prefix = "blacklist", suggestedLevel = SuggestedLevel.ADMIN)
@RegisterCommand(value = {"set"}, subcommandOf = BlacklistCommand.class)
public class BlacklistSetCommand extends AbstractCommand<CommandSource> {

    @Inject private ItemDataService itemDataService;
    private final String typeKey = "action to block";
    private final String itemKey = "item";
    private final String blacklistKey = "blacklist";

    @Override public CommandElement[] getArguments() {
        return new CommandElement[] {
            GenericArguments.flags().valueFlag(GenericArguments.enumValue(Text.of(typeKey), Type.class), "t", "-type")
                .buildWith(
                    GenericArguments.seq(
                        GenericArguments.optionalWeak(GenericArguments.onlyOne(new ItemAliasArgument(Text.of(itemKey), itemDataService))),
                        GenericArguments.onlyOne(GenericArguments.bool(Text.of(blacklistKey)))))
        };
    }

    @Override public CommandResult executeCommand(CommandSource src, CommandContext args) throws Exception {
        CatalogType type = getCatalogTypeFromHandOrArgs(src, itemKey, args);
        Collection<Type> typesToUpdate = args.hasAny(typeKey) ? args.getAll(typeKey) : Lists.newArrayList(Type.values());
        boolean blacklist = args.<Boolean>getOne(blacklistKey).get();
        ItemDataService dataStore = this.plugin.getItemDataService();
        ItemDataNode dataNode = dataStore.getDataForItem(type.getId());

        typesToUpdate.forEach(x -> x.consumer.accept(dataNode, blacklist));
        dataStore.setDataForItem(type.getId(), dataNode);
        dataStore.save();

        MessageProvider mp = plugin.getMessageProvider();
        src.sendMessage(mp.getTextMessageWithFormat("command.blacklist.set.success",
            Util.getTranslatedStringFromItemId(type.getId()).orElse(Util.getTranslatableIfPresentOnCatalogType(type)),
            String.join(", ", typesToUpdate.stream().map(x -> x.toString().toLowerCase()).collect(Collectors.toList())),
            blacklist ? mp.getMessageWithFormat("command.blacklist.set.added") : mp.getMessageWithFormat("command.blacklist.set.removed")
        ));

        return CommandResult.success();
    }

    private enum Type {
        ENVIRONMENT((i, b) -> i.getBlacklist().setEnvironment(b)),
        POSSESSION((i, b) -> i.getBlacklist().setInventory(b)),
        USE((i, b) -> i.getBlacklist().setUse(b));

        private final BiConsumer<ItemDataNode, Boolean> consumer;

        Type(BiConsumer<ItemDataNode, Boolean> consumer) {
            this.consumer = consumer;
        }
    }
}
