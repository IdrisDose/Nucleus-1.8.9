/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.argumentparsers;

import com.google.common.base.Preconditions;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.*;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public final class SimpleProviderChoicesArgument extends CommandElement {

    private final Supplier<Map<String, ?>> choiceSupplier;

    public static SimpleProviderChoicesArgument withSetSupplier(Text key, Supplier<Set<String>> choices) {
        Preconditions.checkNotNull(key);
        Preconditions.checkNotNull(choices);
        return new SimpleProviderChoicesArgument(key, () -> choices.get().stream().collect(Collectors.toMap(k -> k, k -> k)));
    }

    public static SimpleProviderChoicesArgument withMapSupplier(Text key, Supplier<Map<String, ?>> choices) {
        Preconditions.checkNotNull(key);
        Preconditions.checkNotNull(choices);
        return new SimpleProviderChoicesArgument(key, choices);
    }

    private SimpleProviderChoicesArgument(@Nullable Text key, Supplier<Map<String, ?>> choices) {
        super(key);
        choiceSupplier = choices;
    }

    @Nullable
    @Override
    protected Object parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
        return null;
    }

    @Override
    public void parse(CommandSource source, CommandArgs args, CommandContext context) throws ArgumentParseException {
        GenericArguments.choices(getKey(), choiceSupplier.get()).parse(source, args, context);
    }

    @Override
    public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
        return new ArrayList<>(choiceSupplier.get().keySet());
    }
}
