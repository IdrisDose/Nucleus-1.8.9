/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.mail;

import io.github.nucleuspowered.nucleus.api.service.NucleusMailService;
import io.github.nucleuspowered.nucleus.internal.qsml.module.StandardModule;
import io.github.nucleuspowered.nucleus.modules.mail.handlers.MailHandler;
import org.spongepowered.api.Game;
import uk.co.drnaylor.quickstart.annotations.ModuleData;

import javax.inject.Inject;

@ModuleData(id = "mail", name = "Mail")
public class MailModule extends StandardModule {

    @Inject private Game game;

    @Override
    protected void performPreTasks() throws Exception {
        super.performPreTasks();

        MailHandler m = new MailHandler(game, plugin);
        serviceManager.registerService(MailHandler.class, m);
        game.getServiceManager().setProvider(plugin, NucleusMailService.class, m);
    }
}
