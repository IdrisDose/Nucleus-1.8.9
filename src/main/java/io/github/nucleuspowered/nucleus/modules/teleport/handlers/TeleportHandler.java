/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.teleport.handlers;

import com.google.common.base.Preconditions;
import io.github.nucleuspowered.nucleus.NucleusPlugin;
import io.github.nucleuspowered.nucleus.dataservices.UserService;
import io.github.nucleuspowered.nucleus.internal.PermissionRegistry;
import io.github.nucleuspowered.nucleus.internal.interfaces.CancellableTask;
import io.github.nucleuspowered.nucleus.internal.teleport.NucleusTeleportHandler;
import io.github.nucleuspowered.nucleus.modules.teleport.config.TeleportConfigAdapter;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextStyles;
import uk.co.drnaylor.quickstart.exceptions.IncorrectAdapterTypeException;
import uk.co.drnaylor.quickstart.exceptions.NoModuleException;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

public class TeleportHandler {

    private final NucleusPlugin plugin;
    private final Map<UUID, TeleportPrep> ask = new HashMap<>();

    private static final String tptoggleBypassPermission = PermissionRegistry.PERMISSIONS_PREFIX + "teleport.tptoggle.exempt";
    private Text acceptDeny;

    public TeleportHandler(NucleusPlugin plugin) {
        this.plugin = plugin;
    }

    public TeleportBuilder getBuilder() {
        return new TeleportBuilder(plugin);
    }

    public static boolean canBypassTpToggle(CommandSource from) {
        return from.hasPermission(tptoggleBypassPermission);
    }

    public void addAskQuestion(UUID target, TeleportPrep tp) {
        clearExpired();
        get(target).ifPresent(this::cancel);
        ask.put(target, tp);
    }

    public void clearExpired() {
        Instant now = Instant.now();
        ask.entrySet().stream().filter(x -> now.isAfter(x.getValue().getExpire())).map(Map.Entry::getKey).collect(Collectors.toList())
                .forEach(x -> cancel(ask.remove(x)));
    }

    public boolean getAndExecute(UUID uuid) throws Exception {
        Optional<TeleportPrep> otp = get(uuid);
        return otp.isPresent() && otp.get().tpbuilder.startTeleport();

    }

    public Optional<TeleportPrep> get(UUID uuid) {
        clearExpired();
        return Optional.ofNullable(ask.remove(uuid));
    }

    public boolean remove(UUID uuid) {
        TeleportPrep tp = ask.remove(uuid);
        cancel(tp);
        return tp != null;
    }

    public Text getAcceptDenyMessage() {
        if (acceptDeny == null) {
            acceptDeny = Text.builder()
                    .append(Text.builder().append(NucleusPlugin.getNucleus().getMessageProvider().getTextMessageWithFormat("standard.accept")).style(TextStyles.UNDERLINE)
                            .onHover(TextActions.showText(NucleusPlugin.getNucleus().getMessageProvider().getTextMessageWithFormat("teleport.accept.hover")))
                            .onClick(TextActions.runCommand("/tpaccept")).build())
                    .append(Text.of(" - "))
                    .append(Text.builder().append(NucleusPlugin.getNucleus().getMessageProvider().getTextMessageWithFormat("standard.deny")).style(TextStyles.UNDERLINE)
                            .onHover(TextActions.showText(NucleusPlugin.getNucleus().getMessageProvider().getTextMessageWithFormat("teleport.deny.hover")))
                            .onClick(TextActions.runCommand("/tpdeny")).build())
                    .build();
        }

        return acceptDeny;
    }

    private void cancel(@Nullable TeleportPrep prep) {
        if (prep == null) {
            return;
        }

        if (prep.charged != null && prep.cost > 0) {
            if (prep.charged.isOnline()) {
                prep.charged.getPlayer().ifPresent(x -> x
                        .sendMessage(NucleusPlugin.getNucleus().getMessageProvider().getTextMessageWithFormat("teleport.prep.cancel", plugin.getEconHelper().getCurrencySymbol(prep.cost))));
            }

            plugin.getEconHelper().depositInPlayer(prep.charged, prep.cost);
        }
    }

    private static class TeleportTask implements CancellableTask {

        private final Player playerToTeleport;
        private final Player playerToTeleportTo;
        private final Player charged;
        private final double cost;
        private final boolean safe;
        private final CommandSource source;
        private final NucleusPlugin plugin;
        private final boolean silentSource;
        private final boolean silentTarget;

        private TeleportTask(NucleusPlugin plugin, CommandSource source, Player playerToTeleport, Player playerToTeleportTo, boolean safe, boolean silentSource, boolean silentTarget) {
            this(plugin, source, playerToTeleport, playerToTeleportTo, null, 0, safe, silentSource, silentTarget);
        }

        private TeleportTask(NucleusPlugin plugin, CommandSource source, Player playerToTeleport, Player playerToTeleportTo, Player charged, double cost, boolean safe,
                             boolean silentSource, boolean silentTarget) {
            this.plugin = plugin;
            this.source = source;
            this.playerToTeleport = playerToTeleport;
            this.playerToTeleportTo = playerToTeleportTo;
            this.cost = cost;
            this.charged = charged;
            this.safe = safe;
            this.silentSource = silentSource;
            this.silentTarget = silentTarget;
        }

        private void run() {
            if (playerToTeleportTo.isOnline()) {
                // If safe, get the teleport mode
                NucleusTeleportHandler tpHandler = plugin.getTeleportHandler();
                NucleusTeleportHandler.TeleportMode mode = safe ? tpHandler.getTeleportModeForPlayer(playerToTeleport) :
                    NucleusTeleportHandler.TeleportMode.NO_CHECK;

                if (!tpHandler.teleportPlayer(playerToTeleport, playerToTeleportTo.getTransform(), mode)) {
                    if (!silentSource) {
                        source.sendMessage(NucleusPlugin.getNucleus().getMessageProvider().getTextMessageWithFormat("teleport.nosafe"));
                    }

                    onCancel();
                    return;
                }

                if (!source.equals(playerToTeleport) && !silentSource) {
                    source.sendMessage(NucleusPlugin.getNucleus().getMessageProvider().getTextMessageWithFormat("teleport.success.source", playerToTeleport.getName(), playerToTeleportTo.getName()));
                }

                playerToTeleport.sendMessage(NucleusPlugin.getNucleus().getMessageProvider().getTextMessageWithFormat("teleport.to.success", playerToTeleportTo.getName()));
                if (!silentTarget) {
                    playerToTeleportTo.sendMessage(NucleusPlugin.getNucleus().getMessageProvider().getTextMessageWithFormat("teleport.from.success", playerToTeleport.getName()));
                }
            } else {
                if (!silentSource) {
                    source.sendMessage(NucleusPlugin.getNucleus().getMessageProvider().getTextMessageWithFormat("teleport.fail.offline"));
                }

                onCancel();
            }
        }

        @Override
        public void accept(Task task) {
            run();
        }

        @Override
        public void onCancel() {
            if (!silentSource) {
                source.sendMessage(NucleusPlugin.getNucleus().getMessageProvider().getTextMessageWithFormat("teleport.cancelled"));
            }

            if (charged != null && cost > 0) {
                plugin.getEconHelper().depositInPlayer(charged, cost);
            }
        }
    }

    public static class TeleportBuilder {

        private CommandSource source;
        private Player from;
        private Player to;
        private Player charge;
        private double cost;
        private int warmupTime = 0;
        private boolean bypassToggle = false;
        private boolean safe;
        private boolean silentSource = false;
        private boolean silentTarget = false;

        private final NucleusPlugin plugin;

        private TeleportBuilder(NucleusPlugin plugin) {
            this.plugin = plugin;
            try {
                this.safe = plugin.getModuleContainer().getConfigAdapterForModule("teleport", TeleportConfigAdapter.class)
                    .getNodeOrDefault().isUseSafeTeleport();
            } catch (NoModuleException | IncorrectAdapterTypeException e) {
                this.safe = true;
            }
        }

        public TeleportBuilder setSafe(boolean safe) {
            this.safe = safe;
            return this;
        }

        public TeleportBuilder setSource(CommandSource source) {
            this.source = source;
            return this;
        }

        public TeleportBuilder setFrom(Player from) {
            this.from = from;
            return this;
        }

        public TeleportBuilder setTo(Player to) {
            this.to = to;
            return this;
        }

        public TeleportBuilder setCharge(Player charge) {
            this.charge = charge;
            return this;
        }

        public TeleportBuilder setCost(double cost) {
            this.cost = cost;
            return this;
        }

        public TeleportBuilder setWarmupTime(int warmupTime) {
            this.warmupTime = warmupTime;
            return this;
        }

        public TeleportBuilder setBypassToggle(boolean bypassToggle) {
            this.bypassToggle = bypassToggle;
            return this;
        }

        public TeleportBuilder setSilentSource(boolean silent) {
            this.silentSource = silent;
            return this;
        }

        public TeleportBuilder setSilentTarget(boolean silentTarget) {
            this.silentTarget = silentTarget;
            return this;
        }

        public boolean startTeleport() {
            Preconditions.checkNotNull(from);
            Preconditions.checkNotNull(to);

            if (source == null) {
                source = from;
            }

            if (from.equals(to)) {
                source.sendMessage(NucleusPlugin.getNucleus().getMessageProvider().getTextMessageWithFormat("command.teleport.self"));
                return false;
            }

            UserService toPlayer = plugin.getUserDataManager().get(to).get();
            if (!bypassToggle && !toPlayer.isTeleportToggled() && !canBypassTpToggle(source)) {
                source.sendMessage(NucleusPlugin.getNucleus().getMessageProvider().getTextMessageWithFormat("teleport.fail.targettoggle", to.getName()));
                return false;
            }

            if (plugin.getUserDataManager().get(from).get().getJailData().isPresent()) {
                // Don't teleport a jailed player.
                if (!silentSource) {
                    source.sendMessage(NucleusPlugin.getNucleus().getMessageProvider().getTextMessageWithFormat("teleport.fail.jailed", from.getName()));
                }

                return false;
            }

            TeleportTask tt;
            if (cost > 0 && charge != null) {
                tt = new TeleportTask(plugin, source, from, to, charge, cost, safe, silentSource, silentTarget);
            } else {
                tt = new TeleportTask(plugin, source, from, to, safe, silentSource, silentTarget);
            }

            if (warmupTime > 0) {
                from.sendMessage(NucleusPlugin.getNucleus().getMessageProvider().getTextMessageWithFormat("teleport.warmup", String.valueOf(warmupTime)));
                plugin.getWarmupManager().addWarmup(from.getUniqueId(), Sponge.getScheduler().createTaskBuilder().delay(warmupTime, TimeUnit.SECONDS)
                        .execute(tt).name("NucleusPlugin - Teleport Waiter").submit(plugin));
            } else {
                tt.run();
            }

            return true;
        }
    }

    public static class TeleportPrep {

        private final Instant expire;
        private final User charged;
        private final double cost;
        private final TeleportBuilder tpbuilder;

        public TeleportPrep(Instant expire, User charged, double cost, TeleportBuilder tpbuilder) {
            this.expire = expire;
            this.charged = charged;
            this.cost = cost;
            this.tpbuilder = tpbuilder;
        }

        public Instant getExpire() {
            return expire;
        }

        public User getCharged() {
            return charged;
        }

        public double getCost() {
            return cost;
        }

        public TeleportBuilder getTpbuilder() {
            return tpbuilder;
        }
    }
}
