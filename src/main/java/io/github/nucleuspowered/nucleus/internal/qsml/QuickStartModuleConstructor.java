/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.internal.qsml;

import com.google.inject.Injector;
import uk.co.drnaylor.quickstart.Module;
import uk.co.drnaylor.quickstart.exceptions.QuickStartModuleLoaderException;
import uk.co.drnaylor.quickstart.loaders.ModuleConstructor;

public class QuickStartModuleConstructor implements ModuleConstructor {

    private final Injector injector;

    public QuickStartModuleConstructor(Injector injector) {
        this.injector = injector;
    }

    @Override
    public Module constructModule(Class<? extends Module> moduleClass) throws QuickStartModuleLoaderException.Construction {
        return injector.getInstance(moduleClass);
    }
}
