/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.rules.commands;

import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.argumentparsers.PositiveIntegerArgument;
import io.github.nucleuspowered.nucleus.internal.annotations.*;
import io.github.nucleuspowered.nucleus.modules.rules.config.RulesConfig;
import io.github.nucleuspowered.nucleus.modules.rules.config.RulesConfigAdapter;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.List;
import java.util.Optional;

@Permissions(prefix = "rules")
@RunAsync
@RegisterCommand(value = {"add", "a", "+"}, subcommandOf = RulesCommand.class)
@NoWarmup
@NoCooldown
@NoCost
public class RulesAddCommand extends io.github.nucleuspowered.nucleus.internal.command.AbstractCommand<CommandSource> {

    @Inject private RulesConfigAdapter rca;

    private final String positionKey = "position";
    private final String ruleKey = "rule";

    @Override
    public CommandElement[] getArguments() {
        return new CommandElement[] {
            GenericArguments.optionalWeak(new PositiveIntegerArgument(Text.of(positionKey))),
            GenericArguments.remainingJoinedStrings(Text.of(ruleKey))
        };
    }

    @Override
    public CommandResult executeCommand(CommandSource src, CommandContext args) throws Exception {
        Optional<Integer> pos = args.getOne(positionKey);

        // Get the text version first.
        String rule = args.<String>getOne(ruleKey).get();
        Text r = TextSerializers.FORMATTING_CODE.deserialize(rule);

        // If that worked, show the user when done!
        RulesConfig rc = rca.getNodeOrDefault();
        List<String> rules = rc.getRuleSet();

        int position;
        if (pos.isPresent() && pos.get() <= rules.size()) {
            position = pos.get();
            rules.add(position - 1, rule);
        } else if (pos.isPresent()) {
            src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.rules.add.norule", String.valueOf(pos.get()), String.valueOf(rules.size())));
            return CommandResult.empty();
        } else {
            rules.add(rule);
            position = rules.size();
        }

        rc.setRuleSet(rules);
        rca.setNode(rc);
        plugin.saveSystemConfig();

        src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.rules.add.success", String.valueOf(position)));
        src.sendMessage(r);
        return CommandResult.success();
    }
}
