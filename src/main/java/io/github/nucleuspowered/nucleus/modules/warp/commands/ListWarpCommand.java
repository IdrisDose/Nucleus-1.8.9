/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.warp.commands;

import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.Util;
import io.github.nucleuspowered.nucleus.api.data.WarpData;
import io.github.nucleuspowered.nucleus.internal.PermissionRegistry;
import io.github.nucleuspowered.nucleus.internal.annotations.Permissions;
import io.github.nucleuspowered.nucleus.internal.annotations.RegisterCommand;
import io.github.nucleuspowered.nucleus.internal.annotations.RunAsync;
import io.github.nucleuspowered.nucleus.internal.permissions.SuggestedLevel;
import io.github.nucleuspowered.nucleus.modules.warp.config.WarpConfigAdapter;
import io.github.nucleuspowered.nucleus.modules.warp.handlers.WarpHandler;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

/**
 * Lists all the warps that a player can access.
 */
@Permissions(prefix = "warp", suggestedLevel = SuggestedLevel.USER)
@RunAsync
@RegisterCommand(value = {"list"}, subcommandOf = WarpCommand.class, rootAliasRegister = "warps")
public class ListWarpCommand extends io.github.nucleuspowered.nucleus.internal.command.AbstractCommand<CommandSource> {

    @Inject private WarpHandler service;
    @Inject private WarpConfigAdapter adapter;

    @Override public CommandElement[] getArguments() {
        return new CommandElement[] {
            GenericArguments.flags().flag("u").buildWith(GenericArguments.none())
        };
    }

    @Override
    public CommandResult executeCommand(final CommandSource src, CommandContext args) throws Exception {
        if (service.getWarpNames().isEmpty()) {
            src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.warps.list.nowarps"));
            return CommandResult.empty();
        }

        return !args.hasAny("u") && adapter.getNodeOrDefault().isCategoriseWarps() ? categories(src) : noCategories(src);
    }

    private boolean canView(CommandSource src, String warp) {
        return !adapter.getNodeOrDefault().isSeparatePermissions()
                || src.hasPermission(PermissionRegistry.PERMISSIONS_PREFIX + "warps." + warp.toLowerCase());
    }

    private CommandResult categories(final CommandSource src) {
        // Get the warp list.
        Map<String, List<WarpData>> warps = service.getCategorisedWarps(x -> canView(src, x.getName()));
        createMain(src, warps);
        return CommandResult.success();
    }

    private void createMain(final CommandSource src, final Map<String, List<WarpData>> warps) {
        List<Text> lt = warps.keySet().stream().filter(x -> x != null).sorted(Comparator.comparing(Function.identity()))
            .map(s -> Text.builder("> " + s).color(TextColors.GREEN).style(TextStyles.UNDERLINE)
                .onClick(TextActions.executeCallback(source -> createSub(source, s, warps))).build()).collect(Collectors.toList());

        // Uncategorised
        if (warps.containsKey(null)) {
            lt.add(Text.builder("> " + adapter.getNodeOrDefault().getDefaultName()).color(TextColors.GREEN).style(TextStyles.UNDERLINE)
                .onClick(TextActions.executeCallback(source -> createSub(source, null, warps))).build());
        }

        Util.getPaginationBuilder(src)
            .title(plugin.getMessageProvider().getTextMessageWithFormat("command.warps.list.maincategory")).padding(Text.of(TextColors.GREEN, "-"))
            .contents(lt)
            .sendTo(src);
    }

    private void createSub(final CommandSource src, @Nullable final String category, final Map<String, List<WarpData>> warpDataList) {
        final boolean econExists = plugin.getEconHelper().economyServiceExists();
        final int defaultCost = adapter.getNodeOrDefault().getDefaultWarpCost();
        String name = category == null ? adapter.getNodeOrDefault().getDefaultName() : category;

        List<Text> lt = warpDataList.get(category).stream().sorted(Comparator.comparing(WarpData::getName))
            .map(s -> createWarp(s, s.getName(), econExists, defaultCost)).collect(Collectors.toList());

        Util.getPaginationBuilder(src)
            .title(plugin.getMessageProvider().getTextMessageWithFormat("command.warps.list.category", name)).padding(Text.of(TextColors.GREEN, "-"))
            .contents(lt)
            .footer(plugin.getMessageProvider().getTextMessageWithFormat("command.warps.list.back").toBuilder()
                .onClick(TextActions.executeCallback(s -> createMain(s, warpDataList))).build())
            .sendTo(src);
    }

    private CommandResult noCategories(final CommandSource src) {
        // Get the warp list.
        Set<String> ws = service.getWarpNames();
        final boolean econExists = plugin.getEconHelper().economyServiceExists();
        final int defaultCost = adapter.getNodeOrDefault().getDefaultWarpCost();
        List<Text> lt = ws.stream().filter(s -> canView(src, s.toLowerCase())).sorted(String::compareTo).map(s -> {
            Optional<WarpData> wd = service.getWarp(s);
            return createWarp(wd.orElse(null), s, econExists, defaultCost);
        }).collect(Collectors.toList());

        Util.getPaginationBuilder(src)
            .title(plugin.getMessageProvider().getTextMessageWithFormat("command.warps.list.header")).padding(Text.of(TextColors.GREEN, "-"))
            .contents(lt)
            .sendTo(src);

        return CommandResult.success();
    }

    private Text createWarp(@Nullable WarpData data, String name, boolean econExists, int defaultCost) {
        if (data == null || !data.getLocation().isPresent()) {
            return Text.builder(name).color(TextColors.RED).onHover(TextActions.showText(plugin.getMessageProvider().getTextMessageWithFormat("command.warps.unavailable")))
                .build();
        }

        Location<World> world = data.getLocation().get();

        Text.Builder tb =
            Text.builder().append(Text.builder(name).color(TextColors.GREEN).style(TextStyles.UNDERLINE).onClick(TextActions.runCommand("/warp " + name))
                .onHover(TextActions.showText(plugin.getMessageProvider().getTextMessageWithFormat("command.warps.warpprompt", name))).build())
                .append(plugin.getMessageProvider().getTextMessageWithFormat("command.warps.warploc",
                        world.getExtent().getName(), world.getBlockPosition().toString()
                    ));

        if (econExists) {
            int cost = data.getCost().orElse(defaultCost);
            if (cost > 0) {
                tb.append(plugin.getMessageProvider().getTextMessageWithFormat("command.warps.list.cost", plugin.getEconHelper().getCurrencySymbol(cost)));
            }
        }

        return tb.build();
    }
}
