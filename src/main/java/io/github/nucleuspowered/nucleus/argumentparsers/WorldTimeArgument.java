/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.argumentparsers;

import com.google.common.collect.Maps;
import io.github.nucleuspowered.nucleus.Nucleus;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Parses an argument for world time. Could be either:
 *
 * <ul>
 *     <li>[0-23]h, for 24 hour time.</li>
 *     <li>[1-12][am|pm], for 12 hour time.</li>
 *     <li>[0-23999], for ticks.</li>
 * </ul>
 *
 * It could also be one of the pre-defined keywords:
 *
 * <ul>
 *     <li>dawn, sunrise: 0 ticks (6 am)</li>
 *     <li>day, daytime, morning: 1000 ticks (7 am)</li>
 *     <li>noon, afternoon: 6000 ticks (12 pm)</li>
 *     <li>dusk, evening, sunset: 12000 ticks (6 pm)</li>
 *     <li>night: 14000 ticks (8 pm)</li>
 *     <li>midnight: 18000 ticks (12 am)</li>
 * </ul>
 */
public class WorldTimeArgument extends CommandElement {

    private static final HashMap<String, Integer> tickAliases = Maps.newHashMap();
    private static final Pattern tfh = Pattern.compile("^(\\d{1,2})[hH]$");
    private static final Pattern ampm = Pattern.compile("^(\\d{1,2})(a[m]?|p[m]?)$", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    private static final Pattern ticks = Pattern.compile("^(\\d{1,5})$");

    // Thanks to http://minecraft.gamepedia.com/Day-night_cycle
    static {
        tickAliases.put("dawn", 0);
        tickAliases.put("sunrise", 0);
        tickAliases.put("morning", 1000);
        tickAliases.put("day", 1000);
        tickAliases.put("daytime", 1000);
        tickAliases.put("noon", 6000);
        tickAliases.put("afternoon", 6000);
        tickAliases.put("dusk", 12000);
        tickAliases.put("sunset", 12000);
        tickAliases.put("evening", 12000);
        tickAliases.put("night", 14000);
        tickAliases.put("midnight", 18000);
    }

    public WorldTimeArgument(@Nullable Text key) {
        super(key);
    }

    @Nullable
    @Override
    protected Object parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
        String arg = args.next().toLowerCase();
        if (tickAliases.containsKey(arg)) {
            return tickAliases.get(arg);
        }

        // <number>h
        Matcher m1 = tfh.matcher(arg);
        if (m1.matches()) {
            // Get the number, multiply by 1000, return.
            int i = Integer.parseInt(m1.group(1));
            if (i > 23 || i < 0) {
                throw args.createError(Nucleus.getNucleus().getMessageProvider().getTextMessageWithFormat("args.worldtime.24herror"));
            }

            i -= 6;
            if (i < 0) {
                i += 24;
            }

            return i * 1000;
        }

        // <number>am,pm
        Matcher m2 = ampm.matcher(arg);
        if (m2.matches()) {
            // Get the number, multiply by 1000, return.
            int i = Integer.parseInt(m2.group(1));
            if (i > 12 || i < 1) {
                throw args.createError(Nucleus.getNucleus().getMessageProvider().getTextMessageWithFormat("args.worldtime.12herror"));
            }

            // Modify to 24 hour time, based on am/pm
            String id = m2.group(2).toLowerCase();
            if (id.startsWith("p") && i < 12) {
                // 11 pm -> 23, 12 pm -> 12.
                i += 12;
            } else if (id.startsWith("a") && i == 12) {
                // 12 am -> 0
                i = 0;
            }

            // Adjust for Minecraft time.
            i -= 6;
            if (i < 0) {
                i += 24;
            }

            return i * 1000;
        }

        // 0 -> 23999
        if (ticks.matcher(arg).matches()) {
            int i = Integer.parseInt(arg);
            if (i >= 0 && i <= 23999) {
                return i;
            }

            throw args.createError(Nucleus.getNucleus().getMessageProvider().getTextMessageWithFormat("args.worldtime.ticks"));
        }

        throw args.createError(Nucleus.getNucleus().getMessageProvider().getTextMessageWithFormat("args.worldtime.error", arg));
    }

    @Override
    public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
        try {
            String a = args.peek().toLowerCase();
            return tickAliases.keySet().stream().filter(x -> x.startsWith(a)).collect(Collectors.toList());
        } catch (ArgumentParseException e) {
            return tickAliases.keySet().stream().collect(Collectors.toList());
        }
    }
}
