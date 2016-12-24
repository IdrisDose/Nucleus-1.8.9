/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.kit.commands;

import com.google.common.collect.Lists;
import io.github.nucleuspowered.nucleus.dataservices.KitService;
import io.github.nucleuspowered.nucleus.internal.annotations.NoCooldown;
import io.github.nucleuspowered.nucleus.internal.annotations.NoCost;
import io.github.nucleuspowered.nucleus.internal.annotations.NoWarmup;
import io.github.nucleuspowered.nucleus.internal.annotations.Permissions;
import io.github.nucleuspowered.nucleus.internal.annotations.RegisterCommand;
import io.github.nucleuspowered.nucleus.internal.annotations.RunAsync;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

@Permissions
@NoWarmup
@NoCooldown
@NoCost
@RunAsync
@RegisterCommand({"firstjoinkit", "starterkit", "joinkit", "firstkit"})
public class FirstKitCommand extends io.github.nucleuspowered.nucleus.internal.command.AbstractCommand<CommandSource> {

    @Inject private KitService gds;

    @Override
    public CommandResult executeCommand(CommandSource src, CommandContext args) throws Exception {
        List<ItemStackSnapshot> stacks = gds.getFirstKit();
        long none = gds.getFirstKit().stream().filter(x -> x.getType() == ItemTypes.NONE).count();

        if (none > 0) {
            src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.firstkit.list.unable", String.valueOf(none)));
            stacks = stacks.stream().filter(x -> x.getType() != ItemTypes.NONE).collect(Collectors.toList());
        }

        if (stacks == null || stacks.isEmpty()) {
            src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.firstkit.list.none"));
            return CommandResult.success();
        }

        List<Text> itemNames = Lists.newArrayList();

        stacks.forEach(x -> itemNames.add(Text.builder(x.getType().getTranslation().get()).color(TextColors.GREEN)
                .append(Text.of(TextColors.GREEN, " (" + x.getType().getId() + "): "))
                .append(Text.of(TextColors.YELLOW, x.getCount())).build()));

        PaginationService service = Sponge.getServiceManager().provideUnchecked(PaginationService.class);
        service.builder().contents(itemNames)
                .title(plugin.getMessageProvider().getTextMessageWithFormat("command.firstkit.list.title")).padding(Text.of(TextColors.GREEN, "-"))
                .sendTo(src);

        return CommandResult.success();
    }
}
