/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.argumentparsers;

import io.github.nucleuspowered.nucleus.Nucleus;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.difficulty.Difficulty;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * There is currently a bug when using the Difficulty CatalogType, so we handle it ourselves for now.
 *
 * <p>
 *     See https://github.com/SpongePowered/SpongeAPI/issues/1254 and https://github.com/SpongePowered/SpongeCommon/issues/739
 * </p>
 */
public class DifficultyArgument extends CommandElement {

    public DifficultyArgument(@Nullable Text key) {
        super(key);
    }

    @Nullable
    @Override
    protected Object parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
        String arg = args.next();
        Optional<Difficulty> d = Sponge.getRegistry().getType(Difficulty.class, arg.toUpperCase());
        if (d.isPresent()) {
            return d.get();
        }

        throw args.createError(Nucleus.getNucleus().getMessageProvider().getTextMessageWithFormat("args.difficulty.notfound", arg));
    }

    @Override
    public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
        List<String> ls = Sponge.getRegistry().getAllOf(Difficulty.class).stream().map(CatalogType::getName).collect(Collectors.toList());
        try {
            String a = args.peek();
            return ls.stream().filter(a::equalsIgnoreCase).collect(Collectors.toList());
        } catch (ArgumentParseException e) {
            return ls;
        }
    }
}
