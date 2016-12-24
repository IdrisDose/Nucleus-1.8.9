/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.core.commands;

import com.google.common.reflect.TypeToken;
import io.github.nucleuspowered.nucleus.internal.annotations.NoCooldown;
import io.github.nucleuspowered.nucleus.internal.annotations.NoCost;
import io.github.nucleuspowered.nucleus.internal.annotations.NoWarmup;
import io.github.nucleuspowered.nucleus.internal.annotations.Permissions;
import io.github.nucleuspowered.nucleus.internal.annotations.RegisterCommand;
import io.github.nucleuspowered.nucleus.internal.annotations.RunAsync;
import io.github.nucleuspowered.nucleus.internal.command.AbstractCommand;
import io.github.nucleuspowered.nucleus.internal.docgen.CommandDoc;
import io.github.nucleuspowered.nucleus.internal.docgen.DocGenCache;
import io.github.nucleuspowered.nucleus.internal.docgen.PermissionDoc;
import io.github.nucleuspowered.nucleus.internal.permissions.SuggestedLevel;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.SimpleConfigurationNode;
import ninja.leaping.configurate.yaml.YAMLConfigurationLoader;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.yaml.snakeyaml.DumperOptions;

import java.util.Comparator;
import java.util.List;

@RunAsync
@NoCost
@NoWarmup
@NoCooldown
@Permissions(prefix = "nucleus", suggestedLevel = SuggestedLevel.NONE)
@RegisterCommand(value = {"docgen", "gendocs"}, subcommandOf = NucleusCommand.class)
public class DocGenCommand extends AbstractCommand<CommandSource> {

    private final TypeToken<List<CommandDoc>> ttlcd = new TypeToken<List<CommandDoc>>() {};
    private final TypeToken<List<PermissionDoc>> ttlpd = new TypeToken<List<PermissionDoc>>() {};

    @Override
    public boolean canLoad() {
        // Only create the command
        return plugin.getDocGenCache().isPresent();
    }

    @Override
    public CommandResult executeCommand(CommandSource src, CommandContext args) throws Exception {
        src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.nucleus.docgen.start"));
        DocGenCache genCache = plugin.getDocGenCache().get();

        // Generate command file.
        YAMLConfigurationLoader configurationLoader = YAMLConfigurationLoader.builder().setPath(plugin.getDataPath().resolve("commands.yml"))
            .setFlowStyle(DumperOptions.FlowStyle.BLOCK).build();
        ConfigurationNode commandConfigurationNode = SimpleConfigurationNode.root().setValue(ttlcd, getAndSort(genCache.getCommandDocs(), (first, second) -> {
            int m = first.getModule().compareToIgnoreCase(second.getModule());
            if (m == 0) {
                return first.getCommandName().compareToIgnoreCase(second.getCommandName());
            }

            return m;
        }));

        configurationLoader.save(commandConfigurationNode);

        // Generate permission file.
        YAMLConfigurationLoader permissionsConfigurationLoader = YAMLConfigurationLoader.builder().setPath(plugin.getDataPath().resolve("permissions.yml"))
            .setFlowStyle(DumperOptions.FlowStyle.BLOCK).build();
        ConfigurationNode permissionConfigurationNode = SimpleConfigurationNode.root().setValue(ttlpd, getAndSort(genCache.getPermissionDocs(),  (first, second) -> {
            int m = first.getModule().compareToIgnoreCase(second.getModule());
            if (m == 0) {
                return first.getPermission().compareToIgnoreCase(second.getPermission());
            }

            return m;
        }));

        permissionsConfigurationLoader.save(permissionConfigurationNode);

        src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.nucleus.docgen.complete"));
        return CommandResult.success();
    }

    private <T> List<T> getAndSort(List<T> list, Comparator<T> comparator) {
        list.sort(comparator);
        return list;
    }
}
