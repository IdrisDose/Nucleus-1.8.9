/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.blacklist.config;

import com.google.common.collect.Lists;
import io.github.nucleuspowered.nucleus.internal.qsml.NucleusConfigAdapter;

import java.util.List;

public class BlacklistConfigAdapter extends NucleusConfigAdapter.StandardWithSimpleDefault<BlacklistConfig> {

    @Override protected List<Transformation> getTransformations() {
        return Lists.newArrayList(
            new Transformation(new Object[]{"environment"}, (inputPath, valueAtPath) -> {
                valueAtPath.setValue(null);
                return null;
            }),
            new Transformation(new Object[]{"inventory"}, (inputPath, valueAtPath) -> {
                valueAtPath.setValue(null);
                return null;
            }),
            new Transformation(new Object[]{"useReplacement"}, (inputPath, valueAtPath) -> {
                return new Object[] { "use-replacement" };
            })
        );
    }

    public BlacklistConfigAdapter() {
        super(BlacklistConfig.class);
    }
}
