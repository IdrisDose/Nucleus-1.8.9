/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.argumentparsers;

import com.google.common.collect.Lists;
import io.github.nucleuspowered.nucleus.Nucleus;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class GameProfileArgument extends CommandElement {

    private final Pattern p = Pattern.compile("[a-zA-Z0-9_]{1,16}");

    public GameProfileArgument(@Nullable Text key) {
        super(key);
    }

    @Nullable
    @Override
    protected Object parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
        String name = args.next();
        if (!p.matcher(name).matches()) {
            throw args.createError(Nucleus.getNucleus().getMessageProvider().getTextMessageWithFormat("args.gameprofile.format"));
        }

        List<GameProfile> lgp = Sponge.getServiceManager().provideUnchecked(UserStorageService.class).getAll()
                .stream().filter(x -> x.getName().isPresent() && x.getName().get().equalsIgnoreCase(name))
                .collect(Collectors.toList());

        if (lgp.isEmpty()) {
            throw args.createError(Nucleus.getNucleus().getMessageProvider().getTextMessageWithFormat("args.gameprofile.none", name));
        }

        if (lgp.size() == 1) {
            return lgp.get(0);
        }

        return lgp;
    }

    @Override
    public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
        return Lists.newArrayList();
    }
}
