/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.message;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.api.service.NucleusPrivateMessagingService;
import io.github.nucleuspowered.nucleus.dataservices.UserService;
import io.github.nucleuspowered.nucleus.internal.qsml.module.ConfigurableModule;
import io.github.nucleuspowered.nucleus.modules.message.commands.SocialSpyCommand;
import io.github.nucleuspowered.nucleus.modules.message.config.MessageConfigAdapter;
import io.github.nucleuspowered.nucleus.modules.message.handlers.MessageHandler;
import org.spongepowered.api.Game;
import uk.co.drnaylor.quickstart.annotations.ModuleData;

import java.util.Optional;

@ModuleData(id = "message", name = "message")
public class MessageModule extends ConfigurableModule<MessageConfigAdapter> {

    @Inject private Game game;

    @Override
    public MessageConfigAdapter getAdapter() {
        return new MessageConfigAdapter();
    }

    @Override
    protected void performPreTasks() throws Exception {
        super.performPreTasks();

        MessageHandler m = new MessageHandler();
        serviceManager.registerService(MessageHandler.class, m);
        game.getServiceManager().setProvider(plugin, NucleusPrivateMessagingService.class, m);
    }

    @Override public void onEnable() {
        super.onEnable();

        createSeenModule(SocialSpyCommand.class, (cs, user) -> {
            Optional<UserService> userServiceOptional = plugin.getUserDataManager().get(user);
            boolean socialSpy = userServiceOptional.isPresent() && userServiceOptional.get().isSocialSpy();
            return Lists.newArrayList(
                plugin.getMessageProvider().getTextMessageWithFormat("seen.socialspy",
                    plugin.getMessageProvider().getMessageWithFormat("standard.yesno." + Boolean.toString(socialSpy).toLowerCase())));
        });
    }
}
