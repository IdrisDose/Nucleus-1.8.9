/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.configurate.datatypes.item;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class BlacklistNode {

    @Setting(value = "block-environment", comment = "loc:config.itemdatanode.blacklist.environment")
    private boolean environment = false;

    @Setting(value = "block-possesion", comment = "loc:config.itemdatanode.blacklist.possesion")
    private boolean inventory = false;

    @Setting(value = "block-use", comment = "loc:config.itemdatanode.blacklist.block-use")
    private boolean use = false;

    public boolean isEnvironment() {
        return environment;
    }

    public boolean isInventory() {
        return inventory;
    }

    public boolean isUse() {
        return use;
    }

    public void setEnvironment(boolean environment) {
        this.environment = environment;
    }

    public void setInventory(boolean inventory) {
        this.inventory = inventory;
    }

    public void setUse(boolean use) {
        this.use = use;
    }
}
