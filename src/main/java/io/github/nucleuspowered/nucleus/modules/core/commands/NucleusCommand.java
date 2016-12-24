/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.core.commands;

import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.internal.annotations.*;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import uk.co.drnaylor.quickstart.ModuleContainer;

import java.util.Set;

import static io.github.nucleuspowered.nucleus.PluginInfo.*;

/**
 * Gives information about Nucleus.
 *
 * Command Usage: /nucleus
 * Permission: nucleus.nucleus.base
 */
@RunAsync
@Permissions
@NoWarmup
@NoCooldown
@NoCost
@RegisterCommand({ "nucleus" })
public class NucleusCommand extends io.github.nucleuspowered.nucleus.internal.command.AbstractCommand<CommandSource> {

    @Inject private ModuleContainer container;

    private final Text version = Text.of(TextColors.GREEN, NAME + " version " + VERSION + " (built from commit " + GIT_HASH + ")");
    private Text modules = null;

    @Override
    public CommandResult executeCommand(CommandSource src, CommandContext args) throws Exception {
        if (modules == null) {
            Text.Builder tb = Text.builder("Modules: ").color(TextColors.GREEN);

            boolean addComma = false;
            Set<String> enabled = container.getModules();
            for (String module : container.getModules(ModuleContainer.ModuleStatusTristate.ALL)) {
                if (addComma) {
                    tb.append(Text.of(TextColors.GREEN, ", "));
                }

                tb.append(Text.of(enabled.contains(module) ? TextColors.GREEN : TextColors.RED, module));
                addComma = true;
            }

            modules = tb.append(Text.of(TextColors.GREEN, ".")).build();
        }

        src.sendMessage(version);
        src.sendMessage(modules);
        return CommandResult.success();
    }
}
