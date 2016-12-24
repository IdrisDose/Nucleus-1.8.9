/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.fly.config;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class FlyConfig {

    @Setting(value = "save-all-flystate-on-quit", comment = "loc:config.fly.stateonquit")
    private boolean saveOnQuit = true;

    @Setting(value = "find-safe-location-on-login", comment = "loc:config.fly.onlogin")
    private boolean findSafeOnLogin = true;

    @Setting(value = "require-fly-permission-on-login", comment = "loc:config.fly.permissiononlogin")
    private boolean permissionOnLogin = false;

    public boolean isSaveOnQuit() {
        return saveOnQuit;
    }

    public boolean isPermissionOnLogin() {
        return permissionOnLogin;
    }

    public boolean isFindSafeOnLogin() {
        return findSafeOnLogin;
    }
}
