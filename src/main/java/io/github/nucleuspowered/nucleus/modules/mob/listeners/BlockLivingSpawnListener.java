/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.mob.listeners;

import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.Nucleus;
import io.github.nucleuspowered.nucleus.internal.ListenerBase;
import io.github.nucleuspowered.nucleus.internal.annotations.ConditionalListener;
import io.github.nucleuspowered.nucleus.modules.mob.MobModule;
import io.github.nucleuspowered.nucleus.modules.mob.config.BlockSpawnsConfig;
import io.github.nucleuspowered.nucleus.modules.mob.config.MobConfig;
import io.github.nucleuspowered.nucleus.modules.mob.config.MobConfigAdapter;
import org.spongepowered.api.GameState;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.ConstructEntityEvent;
import org.spongepowered.api.event.entity.SpawnEntityEvent;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.world.World;
import uk.co.drnaylor.quickstart.exceptions.IncorrectAdapterTypeException;
import uk.co.drnaylor.quickstart.exceptions.NoModuleException;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

@ConditionalListener(BlockLivingSpawnListener.Condition.class)
public class BlockLivingSpawnListener extends ListenerBase.Reloadable {

    @Inject private MobConfigAdapter configAdapter;
    private MobConfig config = null;

    @Listener
    public void onConstruct(ConstructEntityEvent.Pre event, @Getter("getTransform") Transform<World> worldTransform, @Getter("getTargetType") EntityType type) {
        // No, let's not prevent players from spawning...
        if (Player.class.isAssignableFrom(type.getEntityClass())) {
            return;
        }

        if (!isSpawnable(type.getEntityClass(), type, worldTransform.getExtent())) {
            event.setCancelled(true);
        }
    }

    // Most will be caught by the attempt above, but just in case, this catches them.
    @Listener
    public void onSpawn(SpawnEntityEvent event) {
        event.filterEntities(x -> isSpawnable(x.getClass(), x.getType(), event.getTargetWorld()));
    }

    private boolean isSpawnable(Class<? extends Entity> classType, EntityType type, World world) {
        if (config == null) {
            config = configAdapter.getNodeOrDefault();
        }

        if (!Living.class.isAssignableFrom(classType)) {
            return true;
        }

        Optional<BlockSpawnsConfig> bsco = config.getBlockSpawnsConfigForWorld(world);
        if (!bsco.isPresent()) {
            return true;
        }

        String id = type.getId().toLowerCase();
        return !(bsco.get().isBlockVanillaMobs() && id.startsWith("minecraft:") || bsco.get().getIdsToBlock().contains(id));
    }

    @Override public void onReload() throws Exception {
        config = null;
    }

    public static class Condition implements Predicate<Nucleus> {

        @Override public boolean test(Nucleus nucleus) {
            if (Sponge.getGame().getState().ordinal() < GameState.SERVER_STARTING.ordinal()) {
                return true;
            }

            try {
                Map<String, BlockSpawnsConfig> config = nucleus.getModuleContainer().getConfigAdapterForModule(MobModule.ID, MobConfigAdapter.class)
                    .getNodeOrDefault().getBlockSpawnsConfig();
                if (config.entrySet().stream().anyMatch(x -> Sponge.getServer().getWorldProperties(x.getKey()).isPresent())) {
                    for (BlockSpawnsConfig s : config.values()) {
                        List<String> idsToBlock = s.getIdsToBlock();
                        if (s.isBlockVanillaMobs() || Sponge.getRegistry().getAllOf(EntityType.class).stream()
                            .anyMatch(x -> idsToBlock.contains(x.getId()))) {
                            return true;
                        }
                    }
                }
            } catch (NoModuleException | IncorrectAdapterTypeException e) {
                if (nucleus.isDebugMode()) {
                    e.printStackTrace();
                }
            }

            return false;
        }
    }
}
