/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.home.commands;

import io.github.nucleuspowered.nucleus.Util;
import io.github.nucleuspowered.nucleus.api.data.LocationData;
import io.github.nucleuspowered.nucleus.dataservices.UserService;
import io.github.nucleuspowered.nucleus.dataservices.loaders.UserDataManager;
import io.github.nucleuspowered.nucleus.internal.annotations.Permissions;
import io.github.nucleuspowered.nucleus.internal.annotations.RegisterCommand;
import io.github.nucleuspowered.nucleus.internal.annotations.RunAsync;
import io.github.nucleuspowered.nucleus.internal.command.ReturnMessageException;
import io.github.nucleuspowered.nucleus.internal.permissions.PermissionInformation;
import io.github.nucleuspowered.nucleus.internal.permissions.SuggestedLevel;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import javax.inject.Inject;

@Permissions(prefix = "home", mainOverride = "set", suggestedLevel = SuggestedLevel.USER)
@RunAsync
@RegisterCommand({"homeset", "sethome"})
public class SetHomeCommand extends io.github.nucleuspowered.nucleus.internal.command.AbstractCommand<Player> {

    private final String homeKey = "home";
    private final Pattern warpName = Pattern.compile("^[a-zA-Z][a-zA-Z0-9]{1,15}$");

    @Inject
    private UserDataManager udm;

    @Override
    public CommandElement[] getArguments() {
        return new CommandElement[] {
            GenericArguments.flags().flag("o", "-overwrite").buildWith(
                GenericArguments.onlyOne(GenericArguments.optional(GenericArguments.string(Text.of(homeKey))))
            )
        };
    }

    @Override
    public Map<String, PermissionInformation> permissionSuffixesToRegister() {
        Map<String, PermissionInformation> m = new HashMap<>();
        m.put("unlimited", new PermissionInformation(plugin.getMessageProvider().getMessageWithFormat("permission.homes.unlimited"), SuggestedLevel.ADMIN));
        return m;
    }

    @Override
    public CommandResult executeCommand(Player src, CommandContext args) throws Exception {
        // Get the home key.
        String home = args.<String>getOne(homeKey).orElse("home").toLowerCase();

        // Get the homes.
        UserService iqsu = udm.get(src).get();
        Map<String, LocationData> msw = iqsu.getHomes();

        if (!warpName.matcher(home).matches()) {
            throw new ReturnMessageException(plugin.getMessageProvider().getTextMessageWithFormat("command.sethome.name"));
        }

        // Does the home exist? You have to explicitly delete the home first.
        boolean hasHome = msw.entrySet().stream().anyMatch(k -> k.getKey().equalsIgnoreCase(home));
        boolean overwrite = hasHome && args.hasAny("o");
        if (hasHome && !overwrite) {
            src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.sethome.seterror", home));
            src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.sethome.tooverwrite", home).toBuilder()
                .onClick(TextActions.runCommand("/sethome " + home + " -o")).build());
            return CommandResult.empty();
        }

        if (!hasHome) {
            int c = getCount(src);
            if (msw.size() >= c) {
                throw new ReturnMessageException(plugin.getMessageProvider().getTextMessageWithFormat("command.sethome.limit", String.valueOf(c)));
            }
        }

        // Just in case.
        if (!iqsu.setHome(home, src.getLocation(), src.getRotation(), overwrite)) {
            throw new ReturnMessageException(plugin.getMessageProvider().getTextMessageWithFormat("command.sethome.seterror", home));
        }

        if (overwrite) {
            src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.sethome.overwrite", home));
        }

        src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.sethome.set", home));
        return CommandResult.success();
    }

    private int getCount(Player src) {
        if (permissions.testSuffix(src, "unlimited")) {
            return Integer.MAX_VALUE;
        }

        Optional<String> count = Util.getOptionFromSubject(src, "home-count", "homes");
        int result = 1;
        if (count.isPresent()) {
            try {
                result = Integer.parseInt(count.get());
            } catch (NumberFormatException e) {
                //
            }
        }

        return Math.max(result, 1);
    }
}
