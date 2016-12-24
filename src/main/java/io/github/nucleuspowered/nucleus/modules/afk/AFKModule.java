/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.afk;

import io.github.nucleuspowered.nucleus.internal.qsml.module.ConfigurableModule;
import io.github.nucleuspowered.nucleus.modules.afk.config.AFKConfigAdapter;
import io.github.nucleuspowered.nucleus.modules.afk.handlers.AFKHandler;
import uk.co.drnaylor.quickstart.annotations.ModuleData;

@ModuleData(id = "afk", name = "AFK")
public class AFKModule extends ConfigurableModule<AFKConfigAdapter> {

    @Override
    public AFKConfigAdapter getAdapter() {
        return new AFKConfigAdapter();
    }

    @Override
    protected void performPreTasks() throws Exception {
        super.performPreTasks();

        AFKHandler handler = new AFKHandler();
        plugin.getInjector().injectMembers(handler);
        serviceManager.registerService(AFKHandler.class, handler);
    }
}
