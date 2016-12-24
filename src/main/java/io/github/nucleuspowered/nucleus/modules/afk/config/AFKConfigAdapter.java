/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.afk.config;

import io.github.nucleuspowered.nucleus.internal.qsml.NucleusConfigAdapter;

public class AFKConfigAdapter extends NucleusConfigAdapter.StandardWithSimpleDefault<AFKConfig> {

    public AFKConfigAdapter() {
        super(AFKConfig.class);
    }
}
