/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.core.config;

import io.github.nucleuspowered.nucleus.internal.qsml.NucleusConfigAdapter;

import java.util.List;

public class CoreConfigAdapter extends NucleusConfigAdapter.StandardWithSimpleDefault<CoreConfig> {

    public CoreConfigAdapter() {
        super(CoreConfig.class);
    }

    @Override
    protected List<Transformation> getTransformations() {
        List<Transformation> lt = super.getTransformations();

        // Delete the "permission-command" node as we do not need it any more.
        lt.add(new Transformation(new Object[] { "permission-command" }, (inputPath, valueAtPath) -> {
            valueAtPath.setValue(null);
            return null;
        }));

        return lt;
    }
}
