/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.playerinfo.commands;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.Util;
import io.github.nucleuspowered.nucleus.internal.annotations.Permissions;
import io.github.nucleuspowered.nucleus.internal.annotations.RegisterCommand;
import io.github.nucleuspowered.nucleus.internal.annotations.RunAsync;
import io.github.nucleuspowered.nucleus.internal.permissions.PermissionInformation;
import io.github.nucleuspowered.nucleus.internal.permissions.SuggestedLevel;
import io.github.nucleuspowered.nucleus.modules.afk.handlers.AFKHandler;
import io.github.nucleuspowered.nucleus.modules.playerinfo.config.PlayerInfoConfigAdapter;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RunAsync
@Permissions(suggestedLevel = SuggestedLevel.USER)
@RegisterCommand({"list", "listplayers", "ls"})
public class ListPlayerCommand extends io.github.nucleuspowered.nucleus.internal.command.AbstractCommand<CommandSource> {

    private AFKHandler handler;
    @Inject private PlayerInfoConfigAdapter config;

    private Text afk = null;
    private Text hidden = null;

    private void init() {
        if (afk == null) {
            afk = plugin.getMessageProvider().getTextMessageWithFormat("command.list.afk");
            hidden = plugin.getMessageProvider().getTextMessageWithFormat("command.list.hidden");
            handler = plugin.getInternalServiceManager().getService(AFKHandler.class).orElse(null);
        }
    }

    @Override
    public Map<String, PermissionInformation> permissionSuffixesToRegister() {
        Map<String, PermissionInformation> m = new HashMap<>();
        m.put("seevanished", new PermissionInformation(plugin.getMessageProvider().getMessageWithFormat("permission.list.seevanished"), SuggestedLevel.ADMIN));
        return m;
    }

    @Override
    public CommandResult executeCommand(CommandSource src, CommandContext args) throws Exception {
        init();
        boolean showVanished = permissions.testSuffix(src, "seevanished");

        Collection<Player> players = Sponge.getServer().getOnlinePlayers();
        long playerCount = players.size();
        long hiddenCount = players.stream().filter(x -> x.get(Keys.INVISIBLE).orElse(false)).count();

        Text header;
        if (showVanished && hiddenCount > 0) {
            header = plugin.getMessageProvider().getTextMessageWithFormat("command.list.playercount.hidden", String.valueOf(playerCount),
                    String.valueOf(Sponge.getServer().getMaxPlayers()), String.valueOf(hiddenCount));
        } else {
            header = plugin.getMessageProvider().getTextMessageWithFormat("command.list.playercount.base", String.valueOf(playerCount - hiddenCount),
                    String.valueOf(Sponge.getServer().getMaxPlayers()));
        }

        src.sendMessage(header);

        Optional<PermissionService> optPermissionService = Sponge.getServiceManager().provide(PermissionService.class);
        if (config.getNodeOrDefault().getList().isGroupByPermissionGroup() && optPermissionService.isPresent()) {
            listByPermissionGroup(optPermissionService.get(), players, src, showVanished);
        } else {
            // If we have players, send them on.
            getPlayerList(players, showVanished).ifPresent(src::sendMessage);
        }

        return CommandResult.success();
    }

    private void listByPermissionGroup(final PermissionService service, Collection<Player> players, CommandSource src, boolean showVanished) {
        // Get the groups
        List<Subject> groups = Lists.newArrayList(service.getGroupSubjects().getAllSubjects());

        // Sort them in reverse order - that way we get the most inherited
        // groups first and display them first.
        groups.sort((x, y) -> y.getParents().size() - x.getParents().size());

        // Keep a copy of the players that we will remove from.
        final List<Player> playersToList = new ArrayList<>(players);

        // We're sorting by name though, so we need to do that later.
        final Map<String, Text> messages = Maps.newHashMap();

        groups.forEach(x -> {
            // Get the players in the group.
            Collection<Player> cp = playersToList.stream().filter(pl -> Util.getParentSubjects(pl).contains(x)).collect(Collectors.toList());
            playersToList.removeAll(cp);

            if (!cp.isEmpty()) {
                // Get and put the player list into the map, if there is a
                // player to show. There might not be, they might be vanished!
                getPlayerList(cp, showVanished).ifPresent(y -> messages.put(x.getIdentifier(),
                        Text.builder().append(Text.of(TextColors.YELLOW, x.getIdentifier() + ": ")).append(y).build()));
            }
        });

        // Now sort by identifier and send the messages in that order.
        messages.entrySet().stream().sorted((x, y) -> x.getKey().compareToIgnoreCase(y.getKey())).forEach(x -> src.sendMessage(x.getValue()));

        if (!playersToList.isEmpty()) {
            // Show any unknown groups last.
            getPlayerList(playersToList, showVanished).ifPresent(y -> src.sendMessage(
                    Text.builder().append(Text.of(TextColors.YELLOW, config.getNodeOrDefault().getList().getDefaultGroupName() + ": ")).append(y).build()));
        }
    }

    /**
     * Gets {@link Text} that represents the provided player list.
     *
     * @param playersToList The {@link Player}s to list.
     * @param showVanished <code>true</code> if those who are vanished are to be
     *        shown.
     * @return An {@link Optional} of {@link Text} objects, returning
     *         <code>empty</code> if the player list is of zero length.
     */
    private Optional<Text> getPlayerList(Collection<Player> playersToList, boolean showVanished) {
        List<Text> playerList = playersToList.stream().filter(x -> showVanished || !x.get(Keys.INVISIBLE).orElse(false))
                .sorted((x, y) -> x.getName().compareToIgnoreCase(y.getName())).map(x -> {
                    Text.Builder tb = Text.builder();
                    boolean appendSpace = false;
                    if (handler != null && handler.isAfk(x)) {
                        tb.append(afk);
                        appendSpace = true;
                    }

                    if (x.get(Keys.INVISIBLE).orElse(false)) {
                        tb.append(hidden);
                        appendSpace = true;
                    }

                    if (appendSpace) {
                        tb.append(Text.of(" "));
                    }

                    return tb.append(plugin.getNameUtil().getName(x)).build();
                }).collect(Collectors.toList());

        if (!playerList.isEmpty()) {
            boolean isFirst = true;
            Text.Builder tb = Text.builder();
            for (Text text : playerList) {
                if (!isFirst) {
                    tb.append(Text.of(TextColors.WHITE, ", "));
                }

                tb.append(text);
                isFirst = false;
            }

            return Optional.of(tb.build());
        }

        return Optional.empty();
    }
}
