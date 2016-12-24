/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.enums;

import io.github.nucleuspowered.nucleus.api.events.NucleusModuleEvent;

public enum ModuleEnableState {
    /**
     * Indicates that the module has been disabled and will not be/has not been loaded.
     */
    DISABLED,

    /**
     * Indicates that the module is currently enabled and will be, or has been, loaded.
     *
     * <p>
     *    Modules in this state can be disabled any time before {@link NucleusModuleEvent.AboutToEnable} completes, using
     *    {@link io.github.nucleuspowered.nucleus.api.events.NucleusModuleEvent.AboutToConstruct#disableModule(String, Object)}.
     * </p>
     */
    ENABLED,

    /**
     * Indicates that the module is currently enabled and cannot be disabled. It will be, or has been, loaded.
     */
    FORCE_ENABLED
}