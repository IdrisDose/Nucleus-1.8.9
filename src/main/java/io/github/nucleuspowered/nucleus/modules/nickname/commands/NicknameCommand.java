/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.nickname.commands;

import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.dataservices.UserService;
import io.github.nucleuspowered.nucleus.dataservices.loaders.UserDataManager;
import io.github.nucleuspowered.nucleus.internal.annotations.Permissions;
import io.github.nucleuspowered.nucleus.internal.annotations.RegisterCommand;
import io.github.nucleuspowered.nucleus.internal.permissions.PermissionInformation;
import io.github.nucleuspowered.nucleus.internal.permissions.SuggestedLevel;
import io.github.nucleuspowered.nucleus.modules.nickname.config.NicknameConfigAdapter;
import io.github.nucleuspowered.nucleus.modules.nickname.events.ChangeNicknameEvent;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

@RegisterCommand({"nick", "nickname"})
@Permissions
public class NicknameCommand extends io.github.nucleuspowered.nucleus.internal.command.AbstractCommand<CommandSource> {

    @Inject private UserDataManager loader;
    @Inject private NicknameConfigAdapter nicknameConfigAdapter;

    private final String playerKey = "player";
    private final String nickName = "nickname";

    private final Pattern colourPattern = Pattern.compile("&[0-9a-f]", Pattern.CASE_INSENSITIVE);
    private final Pattern stylePattern = Pattern.compile("&[omnl]", Pattern.CASE_INSENSITIVE);
    private final Pattern magicPattern = Pattern.compile("&k", Pattern.CASE_INSENSITIVE);

    @Override
    public Map<String, PermissionInformation> permissionSuffixesToRegister() {
        Map<String, PermissionInformation> m = new HashMap<>();
        m.put("others", new PermissionInformation(plugin.getMessageProvider().getMessageWithFormat("permission.nick.others"), SuggestedLevel.ADMIN));
        m.put("colour", new PermissionInformation(plugin.getMessageProvider().getMessageWithFormat("permission.nick.colour"), SuggestedLevel.ADMIN));
        m.put("color", new PermissionInformation(plugin.getMessageProvider().getMessageWithFormat("permission.nick.colour"), SuggestedLevel.ADMIN));
        m.put("style", new PermissionInformation(plugin.getMessageProvider().getMessageWithFormat("permission.nick.style"), SuggestedLevel.ADMIN));
        m.put("magic", new PermissionInformation(plugin.getMessageProvider().getMessageWithFormat("permission.nick.magic"), SuggestedLevel.ADMIN));
        return m;
    }

    @Override
    public CommandElement[] getArguments() {
        return new CommandElement[] {
                GenericArguments.optionalWeak(GenericArguments.requiringPermission(
                        GenericArguments.onlyOne(GenericArguments.user(Text.of(playerKey))), permissions.getPermissionWithSuffix("others"))),
                GenericArguments.onlyOne(GenericArguments.string(Text.of(nickName)))};
    }

    @Override
    public CommandResult executeCommand(CommandSource src, CommandContext args) throws Exception {
        User pl = this.getUserFromArgs(User.class, src, playerKey, args);
        String name = args.<String>getOne(nickName).get();

        // Does the user exist?
        Optional<User> match = Sponge.getServiceManager().provideUnchecked(UserStorageService.class).get(TextSerializers.FORMATTING_CODE.stripCodes(name));

        // The only person who can use such a name is oneself.
        if (match.isPresent() && !match.get().getUniqueId().equals(pl.getUniqueId())) {
            // Fail - cannot use another's name.
            src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.nick.nameinuse", name));
            return CommandResult.empty();
        }

        // Giving player must have the colour permissions and whatnot. Also,
        // colour and color are the two spellings we support. (RULE BRITANNIA!)
        if (colourPattern.matcher(name).find() && !(permissions.testSuffix(src, "colour") || permissions.testSuffix(src, "color"))) {
            src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.nick.colour.noperms"));
            return CommandResult.empty();
        }

        // Giving player must have the magic permissions and whatnot.
        if (magicPattern.matcher(name).find() && !permissions.testSuffix(src, "magic")) {
            src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.nick.magic.noperms"));
            return CommandResult.empty();
        }

        // Giving player must have the style permissions and whatnot.
        if (stylePattern.matcher(name).find() && !permissions.testSuffix(src, "style")) {
            src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.nick.style.noperms"));
            return CommandResult.empty();
        }

        int strippedNameLength = name.replaceAll("&[0-9a-fomlnk]", "").length();

        // Do a regex remove to check minimum length requirements.
        if (strippedNameLength < Math.max(nicknameConfigAdapter.getNodeOrDefault().getMinNicknameLength(), 1)) {
            src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.nick.tooshort"));
            return CommandResult.empty();
        }

        // Do a regex remove to check maximum length requirements. Will be at least the minimum length
        if (strippedNameLength > Math.max(nicknameConfigAdapter.getNodeOrDefault().getMaxNicknameLength(), nicknameConfigAdapter.getNodeOrDefault().getMinNicknameLength())) {
            src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.nick.toolong"));
            return CommandResult.empty();
        }

        // Send an event
        ChangeNicknameEvent cne = new ChangeNicknameEvent(Cause.of(NamedCause.source(src)), TextSerializers.FORMATTING_CODE.deserialize(name), pl);
        if (Sponge.getEventManager().post(cne)) {
            src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.nick.eventcancel", pl.getName()));
            return CommandResult.empty();
        }

        UserService nucleusUser = loader.get(pl).get();
        nucleusUser.setNickname(name);
        Text set = nucleusUser.getNicknameAsText().get();

        if (!src.equals(pl)) {
            src.sendMessage(Text.builder().append(plugin.getMessageProvider().getTextMessageWithFormat("command.nick.success.other", pl.getName()))
                    .append(Text.of(" - ", TextColors.RESET, set)).build());
        }

        if (pl.isOnline()) {
            pl.getPlayer().get().sendMessage(Text.builder().append(plugin.getMessageProvider().getTextMessageWithFormat("command.nick.success.base"))
                    .append(Text.of(" - ", TextColors.RESET, set)).build());
        }

        return CommandResult.success();
    }
}
