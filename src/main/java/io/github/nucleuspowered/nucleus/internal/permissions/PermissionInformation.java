/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.internal.permissions;

import org.spongepowered.api.text.Text;

public class PermissionInformation {

    public final Text description;
    public final String plainDescription;
    public final SuggestedLevel level;

    public PermissionInformation(String description, SuggestedLevel level) {
        this.description = Text.of(description);
        this.plainDescription = description;
        this.level = level;
    }
}
