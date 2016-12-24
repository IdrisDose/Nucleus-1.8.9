/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.internal;

import io.github.nucleuspowered.nucleus.internal.permissions.PermissionInformation;
import org.spongepowered.api.scheduler.Task;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public abstract class TaskBase implements Consumer<Task> {

    public abstract boolean isAsync();

    public abstract TimePerRun interval();

    public Map<String, PermissionInformation> getPermissions() {
        return new HashMap<>();
    }

    public final static class TimePerRun {

        private final long time;
        private final TimeUnit unit;

        public TimePerRun(long time, TimeUnit unit) {
            this.time = time;
            this.unit = unit;
        }

        public long getTime() {
            return time;
        }

        public TimeUnit getUnit() {
            return unit;
        }
    }
}
