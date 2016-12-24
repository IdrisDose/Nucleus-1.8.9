/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.data.seen;

import io.github.nucleuspowered.nucleus.Nucleus;
import io.github.nucleuspowered.nucleus.modules.playerinfo.PlayerInfoModule;
import io.github.nucleuspowered.nucleus.modules.playerinfo.commands.SeenCommand;
import io.github.nucleuspowered.nucleus.modules.playerinfo.config.PlayerInfoConfigAdapter;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;
import uk.co.drnaylor.quickstart.exceptions.IncorrectAdapterTypeException;
import uk.co.drnaylor.quickstart.exceptions.NoModuleException;

import java.util.Collection;
import java.util.function.BiFunction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BasicSeenInformationProvider implements SeenInformationProvider {

    private final String permission;
    private final BiFunction<CommandSource, User, Collection<Text>> getterFunction;
    private static PlayerInfoConfigAdapter adapter = null;

    public BasicSeenInformationProvider(@Nullable String permission, BiFunction<CommandSource, User, Collection<Text>> getterFunction) {
        this.permission = permission;
        this.getterFunction = getterFunction;
    }

    @Override
    public boolean hasPermission(@Nonnull CommandSource source, @Nonnull User user) {
        try {
            return source.hasPermission(SeenCommand.EXTENDED_PERMISSION) ||
                (permission != null && !getAdapter().getNodeOrDefault().getSeen().isExtendedPermRequired() && source.hasPermission(permission));
        } catch (NoModuleException | IncorrectAdapterTypeException e) {
            e.printStackTrace();

            return false;
        }
    }

    @Nonnull
    @Override
    public Collection<Text> getInformation(@Nonnull CommandSource source, @Nonnull User user) {
        return getterFunction.apply(source, user);
    }

    private synchronized static PlayerInfoConfigAdapter getAdapter() throws NoModuleException, IncorrectAdapterTypeException {
        if (adapter == null) {
            adapter = Nucleus.getNucleus().getModuleContainer().getConfigAdapterForModule(PlayerInfoModule.ID, PlayerInfoConfigAdapter.class);
        }

        return adapter;
    }
}
