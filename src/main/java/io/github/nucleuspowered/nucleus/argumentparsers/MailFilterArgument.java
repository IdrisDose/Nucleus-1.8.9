/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.argumentparsers;

import com.google.common.collect.Lists;
import io.github.nucleuspowered.nucleus.Nucleus;
import io.github.nucleuspowered.nucleus.Util;
import io.github.nucleuspowered.nucleus.api.data.mail.MailFilter;
import io.github.nucleuspowered.nucleus.api.exceptions.NoSuchPlayerException;
import io.github.nucleuspowered.nucleus.modules.mail.handlers.MailHandler;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MailFilterArgument extends CommandElement {

    private static final Pattern before = Pattern.compile("b:(\\d+)]");
    private static final Pattern after = Pattern.compile("a:(\\d+)]");
    private static final Pattern message = Pattern.compile("m:(.+?)(?= [abmp]:|$)");
    private static final Pattern player = Pattern.compile("p:([a-zA-Z0-9_]{1,16})");
    private final MailHandler handler;

    public MailFilterArgument(@Nullable Text key, MailHandler handler) {
        super(key);
        this.handler = handler;
    }

    @Nullable
    @Override
    protected Object parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
        // Get all the arguments in list.
        List<String> array = Lists.newArrayList();
        while (args.hasNext()) {
            array.add(args.next());
        }

        String together = String.join(" ", array);

        List<MailFilter> lmf = Lists.newArrayList();

        // Get the arguments
        // Players
        Matcher players = player.matcher(together);
        if (players.find()) {
            do {
                UUID u = player(players.group(1));
                if (u != null) {
                    if (Util.consoleFakeUUID.equals(u)) {
                        lmf.add(handler.createConsoleFilter());
                    } else {
                        try {
                            lmf.add(handler.createPlayerFilter(u));
                        } catch (NoSuchPlayerException e) {
                            throw args.createError(Nucleus.getNucleus().getMessageProvider().getTextMessageWithFormat("args.mailfilter.player", players.group(1)));
                        }
                    }
                }
            } while (players.find());
        }

        // Message
        Matcher m = message.matcher(together);
        if (m.find()) {
            do {
                lmf.add(handler.createMessageFilter(m.group(1)));
            } while (m.find());
        }

        // Before
        Matcher b = before.matcher(together);
        Instant before1 = null;
        Instant after1 = null;
        if (b.find()) {
            // Days before
            before1 = Instant.now().minus(Integer.parseInt(b.group(1)), ChronoUnit.DAYS);
        }

        Matcher a = after.matcher(together);
        if (a.find()) {
            after1 = Instant.now().minus(Integer.parseInt(a.group(1)), ChronoUnit.DAYS);
        }

        if (before1 != null || after1 != null) {
            if (before1 != null && after1 != null && before1.isAfter(after1)) {
                lmf.add(handler.createDateFilter(after1, before1));
            } else {
                lmf.add(handler.createDateFilter(before1, after1));
            }
        }

        return lmf.isEmpty() ? null : lmf;
    }

    @Override
    public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
        return Lists.newArrayList();
    }

    private UUID player(String text) {
        if (text.equalsIgnoreCase("server") || (text.equalsIgnoreCase("console"))) {
            return Util.consoleFakeUUID;
        }

        UserStorageService uss = Sponge.getServiceManager().provideUnchecked(UserStorageService.class);
        Optional<User> ou = uss.get(text);
        return ou.isPresent() ? ou.get().getUniqueId() : null;
    }

    private Instant getDateOnly(Instant i) {
        return i.truncatedTo(ChronoUnit.DAYS);
    }
}
