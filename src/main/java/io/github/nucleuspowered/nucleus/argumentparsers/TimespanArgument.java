/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.argumentparsers;

import com.google.common.collect.Lists;
import io.github.nucleuspowered.nucleus.Nucleus;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses an argument and tries to get a timespan. Returns in seconds.
 *
 * This parser was taken from
 * https://github.com/dualspiral/Hammer/blob/master/HammerCore/src/main/java/uk/co/drnaylor/minecraft/hammer/core/commands/parsers/TimespanParser.java
 */
public class TimespanArgument extends CommandElement {
    private final Pattern timeString = Pattern.compile("^((\\d+)w)?((\\d+)d)?((\\d+)h)?((\\d+)m)?((\\d+)s)?$");

    private final int secondsInMinute = 60;
    private final int secondsInHour = 60 * secondsInMinute;
    private final int secondsInDay = 24 * secondsInHour;
    private final int secondsInWeek = 7 * secondsInDay;

    public TimespanArgument(@Nullable Text key) {
        super(key);
    }

    @Nullable
    @Override
    protected Object parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
        if (!args.hasNext()) {
            throw args.createError(Nucleus.getNucleus().getMessageProvider().getTextMessageWithFormat("args.timespan.notime"));
        }

        String s = args.next();
        Matcher m = timeString.matcher(s);
        if (m.matches()) {
            long time = amount(m.group(2), secondsInWeek);
            time += amount(m.group(4), secondsInDay);
            time += amount(m.group(6), secondsInHour);
            time += amount(m.group(8), secondsInMinute);
            time += amount(m.group(10), 1);

            if (time > 0) {
                return time;
            }
        }

        throw args.createError(Nucleus.getNucleus().getMessageProvider().getTextMessageWithFormat("args.timespan.incorrectformat", s));
    }

    private int amount(String g, int multipler) {
        if (g != null && g.length() > 0) {
            return multipler * Integer.parseInt(g);
        }

        return 0;
    }

    @Override
    public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
        return Lists.newArrayList();
    }
}
