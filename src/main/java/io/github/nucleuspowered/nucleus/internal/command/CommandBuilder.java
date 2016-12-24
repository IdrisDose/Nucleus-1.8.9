/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.internal.command;

import com.google.inject.Injector;
import io.github.nucleuspowered.nucleus.NucleusPlugin;
import io.github.nucleuspowered.nucleus.internal.annotations.SkipOnError;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.commented.SimpleCommentedConfigurationNode;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.spec.CommandSpec;

import java.util.Optional;
import java.util.Set;

public class CommandBuilder {

    private final NucleusPlugin plugin;
    private final Injector injector;
    private final Set<Class<? extends AbstractCommand<?>>> commandSet;
    private final SimpleCommentedConfigurationNode sn;
    private final String moduleID;
    private final String moduleName;

    public CommandBuilder(NucleusPlugin plugin, Injector injector, Set<Class<? extends AbstractCommand<?>>> commandSet, String moduleID, String moduleName) {
        this.plugin = plugin;
        this.injector = injector;
        this.commandSet = commandSet;
        this.sn = SimpleCommentedConfigurationNode.root();
        this.moduleID = moduleID;
        this.moduleName = moduleName;
    }

    public <T extends AbstractCommand<?>> Optional<T> buildCommand(Class<T> commandClass) {
        return buildCommand(commandClass, true);
    }

    <T extends AbstractCommand<?>> Optional<T> buildCommand(Class<T> commandClass, boolean rootCmd) {
        Optional<T> optionalCommand = getInstance(commandClass);
        if (!optionalCommand.isPresent()) {
            return Optional.empty();
        }

        T c = optionalCommand.get();
        try {
            c.setModuleName(moduleID, moduleName);
            c.setModuleCommands(commandSet);
            c.setCommandBuilder(this);
            c.postInit();
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }

        // No spec, no return. We also don't want to run it twice...
        CommandSpec spec = c.getSpec();
        if (spec == null) {
            return Optional.empty();
        }

        // If we are using DocGen, add the command information to the system.
        plugin.getDocGenCache().ifPresent(x -> x.addCommand(moduleID, c));

        if (c.mergeDefaults()) {
            sn.getNode(c.getCommandConfigAlias()).setValue(c.getDefaults());
        }

        if (plugin.getCommandsConfig().getCommandNode(c.getCommandConfigAlias()).getNode("enabled").getBoolean(true)) {
            // Register the commands.
            if (rootCmd) {
                Sponge.getCommandManager().register(plugin, spec, c.getAliases());
            }

            // Register as another full blown command.
            for (String s : c.getRootCommandAliases()) {
                if (plugin.getCommandsConfig().getCommandNode(c.getCommandConfigAlias()).getNode("aliases", s).getBoolean(true)) {
                    Sponge.getCommandManager().register(plugin, spec, s);
                }
            }

            return Optional.of(c);
        }

        return Optional.empty();
    }

    private <T extends AbstractCommand<?>> Optional<T> getInstance(Class<T> clazz) {
        try {
            T instance = injector.getInstance(clazz);
            if (instance.canLoad()) {
                return Optional.of(instance);
            }

            return Optional.empty();

            // I can't believe I have to do this...
        } catch (RuntimeException | NoClassDefFoundError e) {
            if (clazz.isAnnotationPresent(SkipOnError.class)) {
                plugin.getLogger().warn(NucleusPlugin.getNucleus().getMessageProvider().getMessageWithFormat("startup.injectablenotloaded", clazz.getName()));
                return Optional.empty();
            }

            throw e;
        }
    }

    public CommentedConfigurationNode getNodeToMerge() {
        return sn;
    }
}
