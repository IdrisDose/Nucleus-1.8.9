/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.jail.runnables;

import io.github.nucleuspowered.nucleus.NucleusPlugin;
import io.github.nucleuspowered.nucleus.Util;
import io.github.nucleuspowered.nucleus.dataservices.loaders.UserDataManager;
import io.github.nucleuspowered.nucleus.internal.TaskBase;
import io.github.nucleuspowered.nucleus.modules.jail.handlers.JailHandler;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;

import javax.inject.Inject;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

public class JailTask extends TaskBase {
    @Inject private NucleusPlugin plugin;
    @Inject private JailHandler jailHandler;
    @Inject private UserDataManager userDataManager;

    @Override
    public void accept(Task task) {
        Collection<Player> pl = Sponge.getServer().getOnlinePlayers();
        pl.stream().map(x -> userDataManager.getUser(x).orElse(null)).filter(x -> x == null || x.getJailData().isPresent()).forEach(x -> Util.testForEndTimestamp(x.getJailData(), () -> jailHandler.unjailPlayer(x.getUser())));
    }

    @Override
    public boolean isAsync() {
        return true;
    }

    @Override
    public TimePerRun interval() {
        return new TimePerRun(2, TimeUnit.SECONDS);
    }

}
