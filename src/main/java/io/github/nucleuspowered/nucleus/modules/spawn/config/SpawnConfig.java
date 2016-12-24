/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.spawn.config;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class SpawnConfig {

    @Setting(value = "spawn-on-login", comment = "loc:config.spawn.onlogin")
    private boolean spawnOnLogin = false;

    @Setting(value = "use-safe-spawn", comment = "loc:config.spawn.safe")
    private boolean safeTeleport = true;

    @Setting(value = "global-spawn", comment = "loc:config.spawn.global.base")
    private GlobalSpawnConfig globalSpawn = new GlobalSpawnConfig();

    public boolean isSpawnOnLogin() {
        return spawnOnLogin;
    }

    public boolean isSafeTeleport() {
        return safeTeleport;
    }

    public GlobalSpawnConfig getGlobalSpawn() {
        return globalSpawn;
    }
}
