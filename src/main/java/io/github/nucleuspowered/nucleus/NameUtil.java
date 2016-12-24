/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import io.github.nucleuspowered.nucleus.dataservices.UserService;
import io.github.nucleuspowered.nucleus.modules.chat.config.ChatConfigAdapter;
import io.github.nucleuspowered.nucleus.modules.chat.config.TemplateConfig;
import io.github.nucleuspowered.nucleus.modules.chat.util.TemplateUtil;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyle;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.text.serializer.TextSerializers;
import uk.co.drnaylor.quickstart.exceptions.IncorrectAdapterTypeException;
import uk.co.drnaylor.quickstart.exceptions.NoModuleException;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.Nullable;

public class NameUtil {

    NameUtil(NucleusPlugin plugin) {
        this.plugin = plugin;
    }

    private final NucleusPlugin plugin;

    // An empty optional indicates we checked.
    @SuppressWarnings("all")
    private Optional<ChatConfigAdapter> chatConfigAdapterOptional = null;

    private final static Map<Character, TextColor> colourMap = Maps.newHashMap();
    private final static Map<Character, TextStyle> styleMap = Maps.newHashMap();
    private final static Map<String, TextStyle> styleMapFull = Maps.newHashMap();

    static {
        colourMap.put('0', TextColors.BLACK);
        colourMap.put('1', TextColors.DARK_BLUE);
        colourMap.put('2', TextColors.DARK_GREEN);
        colourMap.put('3', TextColors.DARK_AQUA);
        colourMap.put('4', TextColors.DARK_RED);
        colourMap.put('5', TextColors.DARK_PURPLE);
        colourMap.put('6', TextColors.GOLD);
        colourMap.put('7', TextColors.GRAY);
        colourMap.put('8', TextColors.DARK_GRAY);
        colourMap.put('9', TextColors.BLUE);
        colourMap.put('a', TextColors.GREEN);
        colourMap.put('b', TextColors.AQUA);
        colourMap.put('c', TextColors.RED);
        colourMap.put('d', TextColors.LIGHT_PURPLE);
        colourMap.put('e', TextColors.YELLOW);
        colourMap.put('f', TextColors.WHITE);

        styleMap.put('k', TextStyles.OBFUSCATED);
        styleMap.put('l', TextStyles.BOLD);
        styleMap.put('m', TextStyles.STRIKETHROUGH);
        styleMap.put('n', TextStyles.UNDERLINE);
        styleMap.put('o', TextStyles.ITALIC);

        styleMapFull.put("OBFUSCATED", TextStyles.OBFUSCATED);
        styleMapFull.put("MAGIC", TextStyles.OBFUSCATED);
        styleMapFull.put("BOLD", TextStyles.BOLD);
        styleMapFull.put("STRIKETHROUGH", TextStyles.STRIKETHROUGH);
        styleMapFull.put("UNDERLINE", TextStyles.UNDERLINE);
        styleMapFull.put("ITALIC", TextStyles.ITALIC);
    }

     /**
     * Gets the display name from a {@link User} as Sponge sees it.
     *
     * @param player The {@link User} to get the data from.
     * @return The {@link Text}
     */
    public Text getName(User player) {
        Preconditions.checkNotNull(player);

        TextColor tc = getNameColour(player);
        Optional<Text> dname;
        if (player.isOnline()) {
            dname = player.getPlayer().get().get(Keys.DISPLAY_NAME);
        } else {
            dname = Optional.empty();
        }

        Text.Builder tb = null;
        if (plugin != null) {
            Optional<UserService> userService = plugin.getUserDataManager().get(player);
            if (userService.isPresent()) {
                Optional<Text> n = userService.get().getNicknameWithPrefix();
                if (n.isPresent()) {
                    tb = n.get().toBuilder();
                }
            }
        } else if (dname.isPresent()) {
            tb = dname.get().toBuilder();
        }

        if (tb == null) {
            tb = Text.builder(player.getName());
        }

        tb.onHover(TextActions.showText(Nucleus.getNucleus().getMessageProvider().getTextMessageWithFormat("name.hover.ign", player.getName()))).build();
        return tb.color(tc).build();
    }

    public String getSerialisedName(User player) {
        return TextSerializers.FORMATTING_CODE.serialize(getName(player));
    }

    public String getNameFromUUID(UUID uuid) {
        if (Util.consoleFakeUUID.equals(uuid)) {
            return Sponge.getServer().getConsole().getName();
        }

        UserStorageService uss = Sponge.getServiceManager().provideUnchecked(UserStorageService.class);
        Optional<User> user = uss.get(uuid);
        if (user.isPresent()) {
            return user.get().getName();
        }

        return Nucleus.getNucleus().getMessageProvider().getMessageWithFormat("standard.unknown");
    }

    public TextColor getColourFromString(@Nullable String s) {
        if (s == null || s.length() == 0) {
            return TextColors.NONE;
        }

        if (s.length() == 1) {
            return colourMap.getOrDefault(s.charAt(0), TextColors.NONE);
        } else {
            return Sponge.getRegistry().getType(TextColor.class, s.toUpperCase()).orElse(TextColors.NONE);
        }
    }

    public TextStyle getTextStyleFromString(@Nullable String s) {
        if (s == null || s.length() == 0) {
            return TextStyles.NONE;
        }

        TextStyle ts = TextStyles.NONE;
        for (String split : s.split("\\s*,\\s*")) {
            if (split.length() == 1) {
                ts = ts.and(styleMap.getOrDefault(split.charAt(0), TextStyles.NONE));
            } else {
                ts = ts.and(styleMapFull.getOrDefault(split.toUpperCase(), TextStyles.NONE));
            }
        }

        return ts;
    }

    private TextColor getNameColour(User player) {
        if (chatConfigAdapterOptional == null) {
            try {
                chatConfigAdapterOptional = Optional.of(plugin.getModuleContainer().getConfigAdapterForModule("chat", ChatConfigAdapter.class));
            } catch (NoModuleException | IncorrectAdapterTypeException e) {
                chatConfigAdapterOptional = Optional.empty();
            }
        }

        Optional<String> os = Util.getOptionFromSubject(player, "namecolor", "namecolour");
        if (os.isPresent()) {
            String s = os.get();
            return getColourFromString(s);
        }

        Optional<TemplateUtil> optionalTemplateUtil = plugin.getInternalServiceManager().getService(TemplateUtil.class);
        if (optionalTemplateUtil.isPresent()) {
            return getColourFromString(optionalTemplateUtil.get().getTemplate(player).getNamecolour());
        }

        return TextColors.NONE;
    }
}
