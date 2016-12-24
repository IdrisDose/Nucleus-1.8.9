/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.internal.migrators;

import com.flowpowered.math.vector.Vector3d;
import com.google.inject.Inject;
import io.github.hsyyid.essentialcmds.EssentialCmds;
import io.github.hsyyid.essentialcmds.api.util.config.Configs;
import io.github.hsyyid.essentialcmds.api.util.config.Configurable;
import io.github.hsyyid.essentialcmds.managers.config.Config;
import io.github.hsyyid.essentialcmds.managers.config.HomeConfig;
import io.github.hsyyid.essentialcmds.managers.config.JailConfig;
import io.github.hsyyid.essentialcmds.utils.Mail;
import io.github.hsyyid.essentialcmds.utils.Utils;
import io.github.nucleuspowered.nucleus.Nucleus;
import io.github.nucleuspowered.nucleus.api.data.JailData;
import io.github.nucleuspowered.nucleus.api.data.MuteData;
import io.github.nucleuspowered.nucleus.api.service.NucleusWarpService;
import io.github.nucleuspowered.nucleus.configurate.datatypes.item.BlacklistNode;
import io.github.nucleuspowered.nucleus.dataservices.ItemDataService;
import io.github.nucleuspowered.nucleus.modules.jail.handlers.JailHandler;
import io.github.nucleuspowered.nucleus.modules.mail.handlers.MailHandler;
import io.github.nucleuspowered.nucleus.modules.mute.handler.MuteHandler;
import io.github.nucleuspowered.nucleus.modules.rules.config.RulesConfig;
import io.github.nucleuspowered.nucleus.modules.rules.config.RulesConfigAdapter;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@DataMigrator.PluginDependency("io.github.hsyyid.essentialcmds")
public class EssCmdsMigrator extends DataMigrator {

    @Inject(optional = true) private JailHandler jailHandler;
    @Inject(optional = true) private MuteHandler muteHandler;
    @Inject(optional = true) private MailHandler mailHandler;
    @Inject(optional = true) private RulesConfigAdapter rca;

    @Override
    public void migrate(CommandSource src) throws Exception {
        src.sendMessage(Nucleus.getNucleus().getMessageProvider().getTextMessageWithFormat("command.nucleus.migrate.begin"));

        // Warps
        Optional<NucleusWarpService> warpService = Sponge.getServiceManager().provide(NucleusWarpService.class);

        if (warpService.isPresent()) {
            NucleusWarpService qs = warpService.get();

            for (Object warp : Utils.getWarps()) {
                String warpName = String.valueOf(warp);
                Transform<World> warpLocation = Utils.getWarp(warpName);

                if (warpLocation != null) {
                    qs.setWarp(warpName, warpLocation.getLocation(), warpLocation.getRotation());
                }
            }

            src.sendMessage(Nucleus.getNucleus().getMessageProvider().getTextMessageWithFormat("command.nucleus.migrate.warps"));
        }

        // Blacklisted items
        ItemDataService ids = this.plugin.getItemDataService();
        for (String item : Utils.getBlacklistItems()) {
            ItemType itemType = Sponge.getRegistry().getType(ItemType.class, item).orElse(ItemTypes.NONE);

            if (itemType != ItemTypes.NONE) {
                BlacklistNode bn = ids.getDataForItem(itemType.getId()).getBlacklist();
                bn.setUse(true);
                bn.setEnvironment(true);
                bn.setInventory(true);
                ids.save();
            }
        }

        src.sendMessage(Nucleus.getNucleus().getMessageProvider().getTextMessageWithFormat("command.nucleus.migrate.blacklist"));

        // Homes
        Configurable homesConfig = HomeConfig.getConfig();

        for (Object uuid : Configs.getConfig(homesConfig).getNode("home", "users").getChildrenMap().keySet()) {
            UUID uniqueId = UUID.fromString(String.valueOf(uuid));

            for (Object home : Utils.getHomes(uniqueId)) {
                String homeName = String.valueOf(home);
                Transform<World> homeLocation = Utils.getHome(uniqueId, homeName);

                if (homeLocation == null) {
                    logger.warn(Nucleus.getNucleus().getMessageProvider().getMessageWithFormat("command.nucleus.migrate.homefailiure", homeName, uuid.toString()));
                } else {
                    getUser(uniqueId).ifPresent(x -> x.setHome(homeName, homeLocation.getLocation(), homeLocation.getRotation()));
                }
            }
        }

        src.sendMessage(Nucleus.getNucleus().getMessageProvider().getTextMessageWithFormat("command.nucleus.migrate.homes"));

        // Jails
        if (jailHandler != null) {
            Configurable jailsConfig = JailConfig.getConfig();

            for (int i = 1; i <= Utils.getNumberOfJails(); i++) {
                UUID worldUuid = UUID.fromString(Configs.getConfig(jailsConfig).getNode("jails", String.valueOf(i), "world").getString());
                double x = Configs.getConfig(jailsConfig).getNode("jails", String.valueOf(i), "X").getDouble();
                double y = Configs.getConfig(jailsConfig).getNode("jails", String.valueOf(i), "Y").getDouble();
                double z = Configs.getConfig(jailsConfig).getNode("jails", String.valueOf(i), "Z").getDouble();
                jailHandler.setJail("EssCmds" + i, new Location<>(Sponge.getServer().getWorld(worldUuid).get(), x, y, z),
                        new Vector3d(0, 0, 0));
            }

            src.sendMessage(Nucleus.getNucleus().getMessageProvider().getTextMessageWithFormat("command.nucleus.migrate.jails"));

            // Jailed players
            if (!jailHandler.getJails().isEmpty()) {
                for (UUID uuid : EssentialCmds.jailedPlayers) {
                    Optional<User> user = Sponge.getServiceManager().provideUnchecked(UserStorageService.class).get(uuid);

                    if (user.isPresent()) {
                        JailData data = new JailData(uuid, (String) jailHandler.getJails().keySet().toArray()[0],
                                Nucleus.getNucleus().getMessageProvider().getMessageWithFormat("command.jail.reason"), null);
                        jailHandler.jailPlayer(user.get(), data);
                    }
                }
            }

            src.sendMessage(Nucleus.getNucleus().getMessageProvider().getTextMessageWithFormat("command.nucleus.migrate.jailed"));
        }

        // Mutes
        if (muteHandler != null) {
            for (UUID uuid : EssentialCmds.muteList) {
                Optional<User> user = Sponge.getServiceManager().provideUnchecked(UserStorageService.class).get(uuid);

                if (user.isPresent()) {
                    MuteData data = new MuteData(uuid, Nucleus.getNucleus().getMessageProvider().getMessageWithFormat("command.mute.defaultreason"));
                    muteHandler.mutePlayer(user.get(), data);
                }
            }

            src.sendMessage(Nucleus.getNucleus().getMessageProvider().getTextMessageWithFormat("command.nucleus.migrate.mutes"));
        }

        // Nicknames
        Configurable mainConfig = Config.getConfig();
        CommentedConfigurationNode node = Configs.getConfig(mainConfig).getNode("nick");

        for (Object uuid : node.getChildrenMap().keySet()) {
            String uniqueId = String.valueOf(uuid);
            String nick = node.getNode(uniqueId).getString();

            getUser(UUID.fromString(uniqueId)).ifPresent(x -> x.setNickname(nick));
        }

        src.sendMessage(Nucleus.getNucleus().getMessageProvider().getTextMessageWithFormat("command.nucleus.migrate.nicks"));

        // Mail
        if (mailHandler != null) {
            for (Mail mail : Utils.getMail()) {
                Optional<User> playerFrom = Sponge.getServiceManager().provideUnchecked(UserStorageService.class).get(mail.getSenderName());
                Optional<User> playerTo = Sponge.getServiceManager().provideUnchecked(UserStorageService.class).get(mail.getRecipientName());

                if (playerFrom.isPresent() && playerTo.isPresent()) {
                    mailHandler.sendMail(playerFrom.get(), playerTo.get(), mail.getMessage());
                }
            }

            src.sendMessage(Nucleus.getNucleus().getMessageProvider().getTextMessageWithFormat("command.nucleus.migrate.mail"));
        }

        // Locked Weather Worlds
        for (String world : Utils.getLockedWeatherWorlds()) {
            UUID uniqueId = UUID.fromString(world);
            getWorld(uniqueId).ifPresent(x -> x.setLockWeather(true));
        }

        src.sendMessage(Nucleus.getNucleus().getMessageProvider().getTextMessageWithFormat("command.nucleus.migrate.weather"));

        // Rules
        if (rca != null) {
            RulesConfig rc = rca.getNodeOrDefault();
            List<String> rules = rc.getRuleSet();
            rules.addAll(Utils.getRules());
            rc.setRuleSet(rules);
            rca.setNode(rc);
            plugin.saveSystemConfig();

            src.sendMessage(Nucleus.getNucleus().getMessageProvider().getTextMessageWithFormat("command.nucleus.migrate.rules"));
        }

        src.sendMessage(Nucleus.getNucleus().getMessageProvider().getTextMessageWithFormat("command.nucleus.migrate.success"));
    }
}
