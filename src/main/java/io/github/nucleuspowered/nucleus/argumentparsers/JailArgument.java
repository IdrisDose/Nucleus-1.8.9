/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.argumentparsers;

import com.google.common.collect.Lists;
import io.github.nucleuspowered.nucleus.Nucleus;
import io.github.nucleuspowered.nucleus.api.data.LocationData;
import io.github.nucleuspowered.nucleus.modules.jail.handlers.JailHandler;
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

public class JailArgument extends CommandElement {

    private JailHandler handler;

    public JailArgument(@Nullable Text key, JailHandler handler) {
        super(key);
        this.handler = handler;
    }

    @Nullable
    @Override
    protected Object parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
        String a = args.next().toLowerCase();
        Optional<LocationData> owl = handler.getJail(a);
        if (owl.isPresent()) {
            return owl.get();
        }

        throw args.createError(Nucleus.getNucleus().getMessageProvider().getTextMessageWithFormat("args.jail.nojail"));
    }

    @Override
    public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
        try {
            String a = args.peek().toLowerCase();
            return handler.getJails().keySet().stream().filter(x -> x.startsWith(a)).collect(Collectors.toList());
        } catch (ArgumentParseException e) {
            return Lists.newArrayList(handler.getJails().keySet());
        }
    }
}
