/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.rules.commands;

import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.argumentparsers.PositiveIntegerArgument;
import io.github.nucleuspowered.nucleus.internal.annotations.*;
import io.github.nucleuspowered.nucleus.modules.core.config.CoreConfigAdapter;
import io.github.nucleuspowered.nucleus.modules.rules.config.RulesConfig;
import io.github.nucleuspowered.nucleus.modules.rules.config.RulesConfigAdapter;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.io.IOException;
import java.util.List;

@Permissions(prefix = "rules")
@RunAsync
@RegisterCommand(value = {"remove", "delete", "del", "-"}, subcommandOf = RulesCommand.class)
@NoWarmup
@NoCooldown
@NoCost
public class RulesDeleteCommand extends io.github.nucleuspowered.nucleus.internal.command.AbstractCommand<CommandSource> {

    @Inject private RulesConfigAdapter rca;
    @Inject private CoreConfigAdapter cca;

    private final String ruleKey = "rule";

    @Override
    public CommandElement[] getArguments() {
        return new CommandElement[] {
            new PositiveIntegerArgument(Text.of(ruleKey)),
            GenericArguments.flags().flag("f").buildWith(GenericArguments.none())
        };
    }

    @Override
    public CommandResult executeCommand(final CommandSource src, CommandContext args) throws Exception {
        RulesConfig rc = rca.getNodeOrDefault();
        List<String> rules = rc.getRuleSet();
        boolean flag = args.<Boolean>getOne("f").orElse(false);

        int index = args.<Integer>getOne(ruleKey).get() - 1;
        if (index >= rules.size()) {
            src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.rules.del.norule", String.valueOf(index + 1)));
            return CommandResult.empty();
        }

        final String ruleToDelete = rules.get(index);
        src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.rules.del.summary", ruleToDelete));
        if (!flag) {
            src.sendMessage(
                    Text.builder(plugin.getMessageProvider().getMessageWithFormat("command.rules.del.confirm")).color(TextColors.GREEN).style(TextStyles.UNDERLINE)
                            .onClick(TextActions.executeCallback(s -> actuallyRemove(index, ruleToDelete, s))).build());
        } else {
            return actuallyRemove(index, ruleToDelete, src);
        }

        return CommandResult.success();
    }

    private CommandResult actuallyRemove(int rule, String ruleToDelete, CommandSource src) {
        RulesConfig rc = rca.getNodeOrDefault();
        List<String> rules = rc.getRuleSet();
        String toDelete = rules.get(rule);
        if (toDelete.equalsIgnoreCase(ruleToDelete)) {
            rules.remove(rule);
            rc.setRuleSet(rules);
            try {
                rca.setNode(rc);
                plugin.saveSystemConfig();
            } catch (ObjectMappingException | IOException e) {
                src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.rules.del.fail"));
                if (cca.getNodeOrDefault().isDebugmode()) {
                    e.printStackTrace();
                }

                return CommandResult.empty();
            }

            src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.rules.del.success"));
            return CommandResult.success();
        } else {
            src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.rules.del.fail"));
            return CommandResult.empty();
        }
    }
}
