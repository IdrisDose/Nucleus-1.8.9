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
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ImprovedGameModeArgument extends CommandElement {

    private final static Map<String, GameMode> gameModeMap = Maps.newHashMap();

    static {
        gameModeMap.put("survival", GameModes.SURVIVAL);
        gameModeMap.put("s", GameModes.SURVIVAL);
        gameModeMap.put("su", GameModes.SURVIVAL);
        gameModeMap.put("0", GameModes.SURVIVAL);
        gameModeMap.put("creative", GameModes.CREATIVE);
        gameModeMap.put("c", GameModes.CREATIVE);
        gameModeMap.put("1", GameModes.CREATIVE);
        gameModeMap.put("adventure", GameModes.ADVENTURE);
        gameModeMap.put("a", GameModes.ADVENTURE);
        gameModeMap.put("2", GameModes.ADVENTURE);
        gameModeMap.put("spectator", GameModes.SPECTATOR);
        gameModeMap.put("sp", GameModes.SPECTATOR);
        gameModeMap.put("3", GameModes.SPECTATOR);
    }

    public ImprovedGameModeArgument(@Nullable Text key) {
        super(key);
    }

    @Nullable
    @Override
    protected Object parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
        String arg = args.next();
        GameMode mode = gameModeMap.get(arg.toLowerCase());

        if (mode == null) {
            throw args.createError(Nucleus.getNucleus().getMessageProvider().getTextMessageWithFormat("args.gamemode.error", arg));
        }

        return mode;
    }

    @Override
    public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
        try {
            String arg = args.peek();
            return gameModeMap.keySet().stream().filter(x -> arg.startsWith(arg.toLowerCase())).collect(Collectors.toList());
        } catch (ArgumentParseException e) {
            return Lists.newArrayList(gameModeMap.keySet());
        }
    }
}
