/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.internal.guice;

import com.google.inject.AbstractModule;
import io.github.nucleuspowered.nucleus.ChatUtil;
import io.github.nucleuspowered.nucleus.NucleusPlugin;
import io.github.nucleuspowered.nucleus.config.CommandsConfig;
import io.github.nucleuspowered.nucleus.dataservices.GeneralService;
import io.github.nucleuspowered.nucleus.dataservices.ItemDataService;
import io.github.nucleuspowered.nucleus.dataservices.KitService;
import io.github.nucleuspowered.nucleus.dataservices.loaders.UserDataManager;
import io.github.nucleuspowered.nucleus.dataservices.loaders.WorldDataManager;
import io.github.nucleuspowered.nucleus.internal.EconHelper;
import io.github.nucleuspowered.nucleus.internal.InternalServiceManager;
import io.github.nucleuspowered.nucleus.internal.PermissionRegistry;
import io.github.nucleuspowered.nucleus.internal.messages.MessageProvider;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import uk.co.drnaylor.quickstart.ModuleContainer;

public class QuickStartInjectorModule extends AbstractModule {

    private final NucleusPlugin plugin;

    public QuickStartInjectorModule(NucleusPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    protected void configure() {
        bind(NucleusPlugin.class).toProvider(() -> plugin);
        bind(Logger.class).toProvider(plugin::getLogger);
        bind(CommandsConfig.class).toProvider(plugin::getCommandsConfig);
        bind(UserDataManager.class).toProvider(plugin::getUserDataManager);
        bind(WorldDataManager.class).toProvider(plugin::getWorldDataManager);
        bind(Game.class).toProvider(Sponge::getGame);
        bind(PermissionRegistry.class).toProvider(plugin::getPermissionRegistry);
        bind(EconHelper.class).toProvider(plugin::getEconHelper);
        bind(ModuleContainer.class).toProvider(plugin::getModuleContainer);
        bind(InternalServiceManager.class).toProvider(plugin::getInternalServiceManager);
        bind(GeneralService.class).toProvider(plugin::getGeneralService);
        bind(ChatUtil.class).toProvider(plugin::getChatUtil);
        bind(MessageProvider.class).toProvider(plugin::getMessageProvider);
        bind(ItemDataService.class).toProvider(plugin::getItemDataService);
        bind(KitService.class).toProvider(plugin::getKitService);
    }
}
