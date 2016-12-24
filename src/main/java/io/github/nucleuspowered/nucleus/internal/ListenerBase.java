/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.internal;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.NucleusPlugin;
import io.github.nucleuspowered.nucleus.internal.permissions.PermissionInformation;
import io.github.nucleuspowered.nucleus.util.ThrowableAction;

import java.util.Map;

public abstract class ListenerBase {
    @Inject
    protected NucleusPlugin plugin;

    public Map<String, PermissionInformation> getPermissions() {
        return Maps.newHashMap();
    }

    public abstract static class Reloadable extends ListenerBase implements ThrowableAction<Exception> {

        public abstract void onReload() throws Exception;

        @Override public final void action() throws Exception {
            onReload();
        }
    }
}
