/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.internal.command;

import io.github.nucleuspowered.nucleus.Nucleus;
import io.github.nucleuspowered.nucleus.internal.annotations.NoCooldown;
import io.github.nucleuspowered.nucleus.internal.annotations.NoCost;
import io.github.nucleuspowered.nucleus.internal.annotations.NoWarmup;
import io.github.nucleuspowered.nucleus.internal.annotations.RunAsync;
import io.github.nucleuspowered.nucleus.internal.migrators.DataMigrator;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;

import java.util.Arrays;

/**
 * The {@link MigratorCommand} is common command logic for all {@link DataMigrator}s.
 *
 * <p>
 *     The constructor in the derived class must not have any paramters, the class should be defined within the constructor.
 *     This is thanks to Type Erasure...
 * </p>
 *
 * @param <M> The {@link DataMigrator} the command represents.
 */
@NoCost
@NoCooldown
@NoWarmup
@RunAsync
public abstract class MigratorCommand<M extends DataMigrator> extends AbstractCommand<CommandSource> {

    private final Class<M> migratorClass;

    public MigratorCommand(Class<M> migratorClass) {
        this.migratorClass = migratorClass;
    }

    @Override
    public CommandResult executeCommand(CommandSource src, CommandContext args) throws Exception {
        DataMigrator.PluginDependency pd = migratorClass.getAnnotation(DataMigrator.PluginDependency.class);
        boolean deps = true;
        if (pd != null && pd.value().length > 0) {
            deps = Arrays.stream(pd.value()).allMatch(x -> Sponge.getPluginManager().getPlugin(x).isPresent());
        }

        if (deps) {
            try {
                // I imagine that there may be a few questions that you may be asking here.
                //
                // 1) Why is there an injector here?
                // 2) Why not just bung the logic in this class?
                //
                // Using an injector allows us to put all our handlers into the class easily, much like with a command.
                // The code in the migrator existed in this command originally anyway, so using an injector made it easy
                // to move the code.
                //
                // Of course, that brings us to why the logic is not in this class anyway - again, due to the injector!
                // We use Guice to inject members into all our command classes, it's part of the magic that allows us
                // to have a module system. EssentialCmds uses a static class to access configuration, and the injector
                // will try to load the class. Normally, this is fine... until the class does not exist - such as when
                // EssentialCmds is NOT installed! We then get an error on the console, and confuse the user. So, moving
                // that logic into a class that loads on demand means that the nasty error no longer occurs.
                plugin.getInjector().getInstance(migratorClass).migrate(src);
                return CommandResult.success();
            } catch (Exception | NoClassDefFoundError e) {
                e.printStackTrace();
                if (pd != null && pd.value().length > 0) {
                    src.sendMessage(Nucleus.getNucleus().getMessageProvider().getTextMessageWithFormat("command.nucleus.migrate.error.plugin", String.join(", ", (CharSequence[]) pd.value())));
                } else {
                    src.sendMessage(Nucleus.getNucleus().getMessageProvider().getTextMessageWithFormat("command.nucleus.migrate.error.noplugin"));
                }
            }
        } else {
            src.sendMessage(Nucleus.getNucleus().getMessageProvider().getTextMessageWithFormat("command.nucleus.migrate.noplugin", String.join(", ", (CharSequence[]) pd.value())));
        }

        return CommandResult.empty();
    }
}
