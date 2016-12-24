/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.playerinfo.handlers;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.github.nucleuspowered.nucleus.NucleusPlugin;
import io.github.nucleuspowered.nucleus.api.data.seen.SeenInformationProvider;
import io.github.nucleuspowered.nucleus.api.service.NucleusSeenService;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

public class SeenHandler implements NucleusSeenService {

    private final Map<String, List<SeenInformationProvider>> moduleInformationProviders = Maps.newTreeMap();
    private final Map<String, List<SeenInformationProvider>> pluginInformationProviders = Maps.newTreeMap();

    @Override
    public void register(@Nonnull Object plugin, @Nonnull SeenInformationProvider seenInformationProvider) throws IllegalArgumentException {
        Preconditions.checkNotNull(plugin);
        Preconditions.checkNotNull(seenInformationProvider);

        Plugin pl = plugin.getClass().getAnnotation(Plugin.class);
        Preconditions.checkArgument(pl != null, NucleusPlugin.getNucleus().getMessageProvider().getMessageWithFormat("seen.error.requireplugin"));

        String name = pl.name();
        List<SeenInformationProvider> providers;
        if (pluginInformationProviders.containsKey(name)) {
            providers = pluginInformationProviders.get(name);
        } else {
            providers = Lists.newArrayList();
            pluginInformationProviders.put(name, providers);
        }

        providers.add(seenInformationProvider);
    }

    public void register(NucleusPlugin plugin, String module, SeenInformationProvider seenInformationProvider) throws IllegalArgumentException {
        Preconditions.checkNotNull(plugin);
        Preconditions.checkNotNull(module);
        Preconditions.checkNotNull(seenInformationProvider);
        Preconditions.checkArgument(plugin.getClass().getAnnotation(Plugin.class).equals(NucleusPlugin.class.getAnnotation(Plugin.class)));

        List<SeenInformationProvider> providers;
        if (moduleInformationProviders.containsKey(module)) {
            providers = moduleInformationProviders.get(module);
        } else {
            providers = Lists.newArrayList();
            moduleInformationProviders.put(module, providers);
        }

        providers.add(seenInformationProvider);
    }

    public List<Text> buildInformation(final CommandSource requester, final User user) {
        List<Text> information = getModuleText(requester, user);
        information.addAll(getText(requester, user));
        return information;
    }

    private List<Text> getModuleText(final CommandSource requester, final User user) {
        List<Text> information = Lists.newArrayList();

        for (Map.Entry<String, List<SeenInformationProvider>> entry : moduleInformationProviders.entrySet()) {
            entry.getValue().stream().filter(sip -> sip.hasPermission(requester, user)).forEach(sip -> {
                Collection<Text> input = sip.getInformation(requester, user);
                if (input != null && !input.isEmpty()) {
                    information.addAll(input);
                }
            });
        }

        return information;
    }

    private List<Text> getText(final CommandSource requester, final User user) {
        List<Text> information = Lists.newArrayList();

        for (Map.Entry<String, List<SeenInformationProvider>> entry : pluginInformationProviders.entrySet()) {
            entry.getValue().stream().filter(sip -> sip.hasPermission(requester, user)).forEach(sip -> {
                Collection<Text> input = sip.getInformation(requester, user);
                if (input != null && !input.isEmpty()) {
                    if (information.isEmpty()) {
                        information.add(Text.EMPTY);
                        information.add(Text.of("-----"));
                        information.add(NucleusPlugin.getNucleus().getMessageProvider().getTextMessageWithFormat("seen.header.plugins"));
                        information.add(Text.of("-----"));
                    }

                    information.add(Text.EMPTY);
                    information.add(Text.of(TextColors.AQUA, entry.getKey() + ":"));
                    information.addAll(input);
                }
            });
        }

        return information;
    }
}
