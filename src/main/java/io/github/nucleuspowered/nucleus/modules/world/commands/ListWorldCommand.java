/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.world.commands;

import com.google.common.collect.Lists;
import io.github.nucleuspowered.nucleus.internal.annotations.Permissions;
import io.github.nucleuspowered.nucleus.internal.annotations.RegisterCommand;
import io.github.nucleuspowered.nucleus.internal.command.AbstractCommand;
import io.github.nucleuspowered.nucleus.internal.permissions.PermissionInformation;
import io.github.nucleuspowered.nucleus.internal.permissions.SuggestedLevel;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.storage.WorldProperties;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Permissions(prefix = "world", suggestedLevel = SuggestedLevel.ADMIN)
@RegisterCommand(value = {"list", "ls"}, subcommandOf = WorldCommand.class, rootAliasRegister = "worlds")
public class ListWorldCommand extends AbstractCommand<CommandSource> {

    // Use a space over EMPTY so pagination doesn't mess up.
    private final Text SPACE = Text.of(" ");

    @Override protected Map<String, PermissionInformation> permissionSuffixesToRegister() {
        Map<String, PermissionInformation> m = super.permissionSuffixesToRegister();
        m.put("seed", new PermissionInformation(plugin.getMessageProvider().getMessageWithFormat("permission.world.seed"), SuggestedLevel.ADMIN));
        return m;
    }

    @Override
    public CommandResult executeCommand(CommandSource src, CommandContext args) throws Exception {
        // Get all the worlds
        Collection<WorldProperties> cwp = Sponge.getServer().getAllWorldProperties();
        final List<Text> listContent = Lists.newArrayList();

        final boolean canSeeSeeds = permissions.testSuffix(src, "seed");
        cwp.stream().sorted(Comparator.comparing(WorldProperties::getWorldName)).forEach(x -> {
            // Name of world
            if (!listContent.isEmpty()) {
                listContent.add(SPACE);
            }

            listContent.add(plugin.getMessageProvider().getTextMessageWithFormat("command.world.list.worlditem", x.getWorldName()));

            if (x.isEnabled()) {
                boolean worldLoaded = Sponge.getServer().getWorld(x.getUniqueId()).isPresent();
                String message =
                    (worldLoaded ? "&a" : "&c") + plugin.getMessageProvider().getMessageWithFormat(worldLoaded ? "standard.true" : "standard.false");
                listContent.add(plugin.getMessageProvider().getTextMessageWithFormat("command.world.list.enabled", message));
            } else {
                listContent.add(plugin.getMessageProvider().getTextMessageWithFormat("command.world.list.disabled"));
            }

            if (canSeeSeeds) {
                listContent.add(plugin.getMessageProvider().getTextMessageWithFormat("command.world.list.seed", String.valueOf(x.getSeed())));
            }

            listContent.add(plugin.getMessageProvider().getTextMessageWithFormat("command.world.list.params",
                x.getDimensionType().getName(),
                x.getGeneratorType().getName(),
                CreateWorldCommand.modifierString(x.getGeneratorModifiers()),
                x.getGameMode().getName(),
                x.getDifficulty().getName()));
        });

        PaginationList.Builder plb = Sponge.getServiceManager().provideUnchecked(PaginationService.class).builder()
            .contents(listContent).title(plugin.getMessageProvider().getTextMessageWithFormat("command.world.list.title"));
        if (!(src instanceof Player)) {
            plb.linesPerPage(-1);
        }

        plb.sendTo(src);
        return CommandResult.success();
    }
}
