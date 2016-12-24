/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.argumentparsers;

import com.google.common.base.Preconditions;
import io.github.nucleuspowered.nucleus.Nucleus;
import io.github.nucleuspowered.nucleus.dataservices.ItemDataService;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.text.Text;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Note that the ItemAlias argument may return either a {@link BlockState} or an {@link ItemType}, depending on what
 * is returned. As they are both {@link org.spongepowered.api.CatalogType}s, this is the simplest way to retrieve the
 * information.
 */
public class ItemAliasArgument extends CommandElement {

    private final ItemDataService itemDataService;

    public ItemAliasArgument(@Nullable Text key, ItemDataService itemDataService) {
        super(key);
        Preconditions.checkNotNull(key);
        this.itemDataService = itemDataService;
    }

    @Nullable
    @Override
    protected Object parseValue(@Nonnull CommandSource source, @Nonnull CommandArgs args) throws ArgumentParseException {
        // The error is ignored anyway, so might as well stick with the empty text.
        String arg = args.next().toLowerCase();
        Optional<CatalogType> result = parseAlias(arg, args);
        if (result.isPresent()) {
            return result.get();
        }

        // OK, if it doesn't contain a colon, add the prefix now
        final String modifiedArg;
        if (!arg.contains(":")) {
            modifiedArg = "minecraft:" + arg;
        } else {
            modifiedArg = arg;
        }

        // Check for ItemType.
        Optional<ItemType> oit = Sponge.getRegistry().getAllOf(ItemType.class).stream().filter(x -> x.getId().equalsIgnoreCase(modifiedArg)).findFirst();
        if (oit.isPresent()) {
            return oit.get();
        }

        // BlockState, you're up next!
        Optional<BlockState> obs = Sponge.getRegistry().getAllOf(BlockState.class).stream().filter(x -> x.getId().equalsIgnoreCase(modifiedArg)).findFirst();
        if (obs.isPresent()) {
            return obs.get();
        }

        throw args.createError(Nucleus.getNucleus().getMessageProvider().getTextMessageWithFormat("args.itemarg.nomatch", arg));
    }

    private Optional<CatalogType> parseAlias(String arg, @Nonnull CommandArgs args) throws ArgumentParseException {
        Optional<String> oid = itemDataService.getIdFromAlias(arg);
        if (!oid.isPresent()) {
            return Optional.empty();
        }

        String id = oid.get();

        // Is it an Item?
        Optional<ItemType> obs = Sponge.getRegistry().getAllOf(ItemType.class).stream().filter(x -> x.getId().equalsIgnoreCase(id)).findFirst();
        if (obs.isPresent()) {
            return Optional.of(obs.get());
        }

        // Well, hopefully it's a blockstate then.
        return Optional.of(Sponge.getRegistry().getAllOf(BlockState.class).stream().filter(x -> x.getId().equalsIgnoreCase(id)).findFirst()
                .orElseThrow(() -> args.createError(Nucleus.getNucleus().getMessageProvider().getTextMessageWithFormat("args.itemarg.orphanedarg", arg, id))));
    }

    @Override
    @Nonnull
    public List<String> complete(@Nonnull CommandSource src, @Nonnull CommandArgs args, @Nonnull CommandContext context) {
        try {
            String arg = args.peek().toLowerCase();
            return itemDataService.getAliases().stream().filter(x -> x.startsWith(arg)).sorted().collect(Collectors.toList());
        } catch (ArgumentParseException e) {
            return new ArrayList<>(itemDataService.getAliases());
        }
    }
}
