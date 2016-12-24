/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.freezeplayer;

import com.google.common.collect.Lists;
import io.github.nucleuspowered.nucleus.dataservices.UserService;
import io.github.nucleuspowered.nucleus.internal.qsml.module.StandardModule;
import io.github.nucleuspowered.nucleus.modules.freezeplayer.commands.FreezePlayerCommand;
import uk.co.drnaylor.quickstart.annotations.ModuleData;

import java.util.Optional;

@ModuleData(id = "freeze-player", name = "Freeze Player")
public class FreezePlayerModule extends StandardModule {

    @Override public void onEnable() {
        super.onEnable();

        createSeenModule(FreezePlayerCommand.class, (c, u) -> {
            Optional<UserService> us = plugin.getUserDataManager().get(u);
            if (us.isPresent() && us.get().isFrozen()) {
                return Lists.newArrayList(plugin.getMessageProvider().getTextMessageWithFormat("seen.frozen"));
            }

            return Lists.newArrayList(plugin.getMessageProvider().getTextMessageWithFormat("seen.notfrozen"));
        });
    }
}
