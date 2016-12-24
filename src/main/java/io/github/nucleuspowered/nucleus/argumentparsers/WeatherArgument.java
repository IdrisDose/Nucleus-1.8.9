/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.argumentparsers;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.github.nucleuspowered.nucleus.Nucleus;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.weather.Weather;
import org.spongepowered.api.world.weather.Weathers;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class WeatherArgument extends CommandElement {

    private static final Map<String, Weather> weather = Maps.newHashMap();

    static {
        weather.put("clear", Weathers.CLEAR);
        weather.put("c", Weathers.CLEAR);
        weather.put("sun", Weathers.CLEAR);
        weather.put("rain", Weathers.RAIN);
        weather.put("r", Weathers.RAIN);
        weather.put("storm", Weathers.THUNDER_STORM);
        weather.put("thunder", Weathers.THUNDER_STORM);
        weather.put("t", Weathers.THUNDER_STORM);
    }

    public WeatherArgument(@Nullable Text key) {
        super(key);
    }

    @Nullable
    @Override
    protected Object parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
        String arg = args.next().toLowerCase();
        if (weather.containsKey(arg)) {
            return weather.get(arg);
        }

        throw args.createError(Nucleus.getNucleus().getMessageProvider().getTextMessageWithFormat("args.weather.noexist", "clear, rain, storm"));
    }

    @Override
    public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
        try {
            String a = args.peek();
            return weather.entrySet().stream().filter(x -> x.getKey().toLowerCase().startsWith(a)).map(Map.Entry::getKey).collect(Collectors.toList());
        } catch (ArgumentParseException e) {
            e.printStackTrace();
            return Lists.newArrayList(weather.keySet());
        }
    }
}
