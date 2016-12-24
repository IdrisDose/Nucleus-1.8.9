/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.argumentparsers;

import com.google.common.collect.Lists;
import io.github.nucleuspowered.nucleus.Nucleus;
import io.github.nucleuspowered.nucleus.api.data.Kit;
import io.github.nucleuspowered.nucleus.internal.PermissionRegistry;
import io.github.nucleuspowered.nucleus.modules.kit.config.KitConfigAdapter;
import io.github.nucleuspowered.nucleus.modules.kit.handlers.KitHandler;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class KitArgument extends CommandElement {

    private final KitConfigAdapter config;
    private final KitHandler kitConfig;
    private final boolean permissionCheck;

    public KitArgument(@Nullable Text key, KitConfigAdapter config, KitHandler kitConfig, boolean permissionCheck) {
        super(key);
        this.config = config;
        this.kitConfig = kitConfig;
        this.permissionCheck = permissionCheck;
    }

    @Nullable
    @Override
    protected Object parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
        String kitName = args.next();
        if (kitName.isEmpty()) {
            throw args.createError(Nucleus.getNucleus().getMessageProvider().getTextMessageWithFormat("args.kit.noname"));
        }

        Optional<Kit> kit = kitConfig.getKit(kitName);

        if (!kit.isPresent()) {
            throw args.createError(Nucleus.getNucleus().getMessageProvider().getTextMessageWithFormat("args.kit.noexist"));
        }

        if (!checkPermission(source, kitName)) {
            throw args.createError(Nucleus.getNucleus().getMessageProvider().getTextMessageWithFormat("args.kit.noperms"));
        }

        return new KitInfo(kit.get(), kitName);
    }

    @Override
    public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
        try {
            String name = args.peek().toLowerCase();
            return kitConfig.getKitNames().stream().filter(s -> s.toLowerCase().startsWith(name))
                    .filter(x -> checkPermission(src, name)).collect(Collectors.toList());
        } catch (ArgumentParseException e) {
            return Lists.newArrayList();
        }
    }

    private boolean checkPermission(CommandSource src, String name) {
        if (!permissionCheck || !config.getNodeOrDefault().isSeparatePermissions()) {
            return true;
        }

        // No permissions, no entry!
        return src.hasPermission(PermissionRegistry.PERMISSIONS_PREFIX + "kits." + name.toLowerCase());
    }

    public final class KitInfo {
        public final Kit kit;
        public final String name;

        public KitInfo(Kit kit, String name) {
            this.kit = kit;
            this.name = name;
        }
    }
}
