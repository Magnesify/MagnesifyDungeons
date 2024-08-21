package com.magnesify.magnesifydungeons.mythic;

import com.magnesify.magnesifydungeons.MagnesifyDungeons;
import com.magnesify.magnesifydungeons.boss.MagnesifyBoss;
import com.magnesify.magnesifydungeons.dungeon.Dungeon;
import com.magnesify.magnesifydungeons.dungeon.TriggerType;
import com.magnesify.magnesifydungeons.dungeon.entitys.DungeonConsole;
import com.magnesify.magnesifydungeons.dungeon.entitys.DungeonPlayer;
import com.magnesify.magnesifydungeons.genus.DungeonGenus;
import com.magnesify.magnesifydungeons.languages.LanguageFile;
import com.magnesify.magnesifydungeons.modules.Defaults;
import com.magnesify.magnesifydungeons.modules.managers.DatabaseManager;
import com.magnesify.magnesifydungeons.modules.managers.StatsManager;
import com.magnesify.magnesifydungeons.storage.PlayerMethods;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.magnesify.magnesifydungeons.MagnesifyDungeons.get;
import static com.magnesify.magnesifydungeons.dungeon.TriggerType.level;

public class MythicBossDeathEvent implements Listener {
    public MythicBossDeathEvent(MagnesifyDungeons magnesifyDungeons) {}

    @EventHandler
    public void onDeath(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        if(event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            if(entity.hasMetadata("name")) {
                String metadataValue = entity.getMetadata("name").get(0).asString();
                DungeonPlayer dungeonPlayer = new DungeonPlayer(player);
                DungeonGenus genus = new DungeonGenus(player);
                if(dungeonPlayer.inDungeon()) {
                    if (get().getPlayers().getLastBoss(player).equalsIgnoreCase(metadataValue)) {
                        if (get().getConfig().getBoolean("settings.minimal-options.send-damage-title")) {
                            if(genus.isGenusSet()) {
                                genus.skills().MoreDamage(event);
                            }
                            dungeonPlayer.messageManager().title("&f", "&c&l-" + String.valueOf(event.getDamage()).substring(0, 2));
                        }
                    }
                }
            }
        }

    }

    private final Map<UUID, Integer> countdowns = new HashMap<>();

    public void sendspawn(Player player) {
        countdowns.put(player.getUniqueId(), get().getConfig().getInt("settings.minimal-options.teleport-delay-for-sending-spawn"));
        new BukkitRunnable() {
            @Override
            public void run() {
                UUID playerId = player.getUniqueId();
                if (countdowns.containsKey(playerId)) {
                    int remainingTime = countdowns.get(playerId);
                    if (remainingTime > 0) {
                        countdowns.put(playerId, remainingTime - 1); // Geri sayım süresini azaltma
                    } else {
                        countdowns.remove(playerId);
                        this.cancel();
                        Defaults defaults = new Defaults();
                        DungeonConsole dungeonConsole = new DungeonConsole();
                        if(defaults.MainSpawn().world() != null) {
                            if (Bukkit.getWorld(defaults.MainSpawn().world()) != null) {
                                Location loc = new Location(Bukkit.getWorld(defaults.MainSpawn().world()), defaults.MainSpawn().x(), defaults.MainSpawn().y(), defaults.MainSpawn().z(), (float) defaults.MainSpawn().yaw(), (float) defaults.MainSpawn().pitch());
                                player.teleport(loc);
                            } else {
                                dungeonConsole.ConsoleOutputManager().write(new LanguageFile().getLanguage().getString("plugin.spawn-not-exists"));
                            }
                        } else {
                            dungeonConsole.ConsoleOutputManager().write(new LanguageFile().getLanguage().getString("plugin.spawn-not-exists"));
                        }
                    }
                } else {
                    this.cancel();
                }
            }
        }.runTaskTimer(get(), 0, 20);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (event.getDamager() instanceof LivingEntity) {
                LivingEntity damager = (LivingEntity) event.getDamager();
                player.setMetadata("last_damager", new FixedMetadataValue(get(), damager));
            }
        }
    }

    @EventHandler
    public void onKill(PlayerDeathEvent event) {
        Player entity = event.getEntity().getPlayer();
        DungeonGenus dungeonGenus = new DungeonGenus(entity);
        dungeonGenus.setGenusSkills();
        DungeonPlayer dungeonPlayer = new DungeonPlayer(entity);
        if (dungeonPlayer.inDungeon()) {
            boolean isMythicMob = MythicBukkit.inst().getMobManager().isMythicMob(event.getEntity());
            if(isMythicMob) {
                if (entity.hasMetadata("last_damager")) {
                    LivingEntity lastDamager = (LivingEntity) entity.getMetadata("last_damager").get(0).value();
                    Dungeon dungeon = new Dungeon(get().getPlayers().getLastDungeon(entity));
                    TriggerType triggerType = new TriggerType(entity);
                    DatabaseManager databaseManager = new DatabaseManager(get());
                    if (databaseManager.getType(get().getPlayers().getLastDungeon(entity)).equalsIgnoreCase("Normal")) {
                        dungeon.status(true);
                        lastDamager.remove();
                        get().getPlayers().setDone(entity, true);
                        dungeon.events().stop(entity);
                        triggerType.events().stop(entity);
                        get().getPlayers().updateDungeonStatus(entity, false);
                        get().getPlayers().updateDeath(entity, 1);
                        StatsManager statsManager = new StatsManager();
                        statsManager.updateMatch(entity.getUniqueId().toString(), 1);
                        statsManager.updateDeath(entity.getUniqueId().toString(), 1);
                        statsManager.updateLose(entity.getPlayer().getUniqueId().toString(), 1);
                    } else {
                        lastDamager.remove();
                        dungeon.events().stop(entity);
                        triggerType.events().stop(entity);
                        get().getPlayers().updateDungeonStatus(entity, false);
                        get().getPlayers().updateDeath(entity, 1);
                        StatsManager statsManager = new StatsManager();
                        statsManager.updateMatch(entity.getUniqueId().toString(), 1);
                        statsManager.updateDeath(entity.getUniqueId().toString(), 1);
                        statsManager.updateLose(entity.getPlayer().getUniqueId().toString(), 1);
                        PlayerMethods playerMethods = new PlayerMethods(entity);
                        playerMethods.updateDungeonStatus(entity, false);
                        playerMethods.setDone(entity, true);
                        triggerType.events().stop(entity);
                    }
                    dungeonPlayer.messageManager().chat(new LanguageFile().getLanguage().getString("messages.status.lose.chat"));
                    dungeonPlayer.messageManager().title(new LanguageFile().getLanguage().getString("messages.status.lose.title"), new LanguageFile().getLanguage().getString("messages.status.lose.subtitle"));
                }
            } else {
                if (entity.hasMetadata("last_damager")) {
                    LivingEntity lastDamager = (LivingEntity) entity.getMetadata("last_damager").get(0).value();
                    Dungeon dungeon = new Dungeon(get().getPlayers().getLastDungeon(entity));
                    TriggerType triggerType = new TriggerType(entity);
                    DatabaseManager databaseManager = new DatabaseManager(get());
                    if (databaseManager.getType(get().getPlayers().getLastDungeon(entity)).equalsIgnoreCase("Normal")) {
                        dungeon.status(true);
                        lastDamager.remove();
                        get().getPlayers().setDone(entity, true);
                        dungeon.events().stop(entity);
                        triggerType.events().stop(entity);
                        get().getPlayers().updateDungeonStatus(entity, false);
                        get().getPlayers().updateDeath(entity, 1);
                        StatsManager statsManager = new StatsManager();
                        statsManager.updateMatch(entity.getUniqueId().toString(), 1);
                        statsManager.updateDeath(entity.getUniqueId().toString(), 1);
                        statsManager.updateLose(entity.getPlayer().getUniqueId().toString(), 1);
                    } else {
                        lastDamager.remove();
                        dungeon.events().stop(entity);
                        triggerType.events().stop(entity);
                        get().getPlayers().updateDungeonStatus(entity, false);
                        get().getPlayers().updateDeath(entity, 1);
                        StatsManager statsManager = new StatsManager();
                        statsManager.updateMatch(entity.getUniqueId().toString(), 1);
                        statsManager.updateDeath(entity.getUniqueId().toString(), 1);
                        statsManager.updateLose(entity.getPlayer().getUniqueId().toString(), 1);
                        PlayerMethods playerMethods = new PlayerMethods(entity);
                        playerMethods.updateDungeonStatus(entity, false);
                        playerMethods.setDone(entity, true);
                        triggerType.events().stop(entity);
                    }
                    dungeonPlayer.messageManager().chat(new LanguageFile().getLanguage().getString("messages.status.lose.chat"));
                    dungeonPlayer.messageManager().title(new LanguageFile().getLanguage().getString("messages.status.lose.title"), new LanguageFile().getLanguage().getString("messages.status.lose.subtitle"));
                }
            }
        }
    }

    @EventHandler
    public void onDeathEventOther(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        Player killer = event.getEntity().getKiller();
        if (entity.hasMetadata("name")) {
            String metadataValue = entity.getMetadata("name").get(0).asString();
            String boss_name = entity.getMetadata("boss").get(0).asString();
            DungeonPlayer dungeonPlayer = new DungeonPlayer(killer);
            Entity bukkitEntity = event.getEntity();
            boolean isMythicMob = MythicBukkit.inst().getMobManager().isMythicMob(bukkitEntity);
            DatabaseManager databaseManager = new DatabaseManager(get());
            if (isMythicMob) {
                Optional<ActiveMob> optActiveMob = MythicBukkit.inst().getMobManager().getActiveMob(bukkitEntity.getUniqueId());
                optActiveMob.ifPresent(activeMob -> {
                    if (dungeonPlayer.inDungeon()) {
                        if (get().getPlayers().getLastBoss(killer).equalsIgnoreCase(metadataValue)) {
                            if (databaseManager.getType(get().getPlayers().getLastDungeon(killer)).equalsIgnoreCase("Normal")) {
                                sendspawn(killer);
                                Dungeon dungeon = new Dungeon(get().getPlayers().getLastDungeon(killer));
                                dungeon.status(true);
                                entity.remove();
                                get().getPlayers().setDone(killer, true);
                                dungeon.events().stop(killer);
                                get().getPlayers().updateDungeonStatus(killer, false);
                                get().getPlayers().updatePoint(killer, dungeon.point());
                                event.getDrops().clear();
                                MagnesifyBoss magnesifyBoss = new MagnesifyBoss(boss_name);
                                for (String a : magnesifyBoss.drops()) {
                                    String[] split = a.split(":");
                                    ItemStack item = new ItemStack(Material.getMaterial(split[0]));
                                    ItemMeta itemMeta = item.getItemMeta();
                                    item.setAmount(Integer.parseInt(split[1]));
                                    item.setItemMeta(itemMeta);
                                    killer.getInventory().addItem(item);
                                }
                                StatsManager statsManager = new StatsManager();
                                statsManager.updateMatch(killer.getUniqueId().toString(), 1);
                                statsManager.updateKill(killer.getUniqueId().toString(), 1);
                                dungeonPlayer.updateCurrentLevelForDungeon(dungeon.parameters().name(), dungeon.parameters().next());
                                dungeonPlayer.messageManager().chat(new LanguageFile().getLanguage().getString("messages.status.win.chat"));
                                dungeonPlayer.messageManager().title(new LanguageFile().getLanguage().getString("messages.status.win.title"), new LanguageFile().getLanguage().getString("messages.status.win.subtitle").replace("#point", String.valueOf(dungeon.parameters().point())));
                            } else {
                                entity.remove();
                                event.getDrops().clear();
                                MagnesifyBoss magnesifyBoss = new MagnesifyBoss(boss_name);
                                for (String a : magnesifyBoss.drops()) {
                                    String[] split = a.split(":");
                                    ItemStack item = new ItemStack(Material.getMaterial(split[0]));
                                    ItemMeta itemMeta = item.getItemMeta();
                                    item.setAmount(Integer.parseInt(split[1]));
                                    item.setItemMeta(itemMeta);
                                    killer.getInventory().addItem(item);
                                }
                                StatsManager statsManager = new StatsManager();
                                statsManager.updateKill(killer.getUniqueId().toString(), 1);
                                PlayerMethods playerMethods = new PlayerMethods();
                                if (databaseManager.TriggerTypeDungeons().getTotalCheckpoints(playerMethods.getLastDungeon(killer)) != level.get(killer.getUniqueId())) {
                                    level.put(killer.getUniqueId(), level.get(killer.getUniqueId()) + 1);
                                    MagnesifyBoss next_boss = new MagnesifyBoss(databaseManager.TriggerTypeDungeons().getCheckpointBoss(get().getPlayers().getLastDungeon(killer), level.get(killer.getUniqueId())));
                                    if (next_boss.exists()) {
                                        next_boss.spawn(databaseManager.TriggerTypeDungeons().getBosspointsLocation(get().getPlayers().getLastDungeon(killer), level.get(killer.getUniqueId())), killer);
                                    } else {
                                        DungeonConsole dungeonConsole = new DungeonConsole();
                                        dungeonConsole.ConsoleOutputManager().write("<#4f91fc>[Magnesify Dungeons] &f" + next_boss.name() + " adında bir yaratık yok, hata oluşmaması adına Magnesify, Normal yaratığı doğuruyor...");
                                        playerMethods.updateLastBoss(killer, "Magnesify");
                                        MagnesifyBoss spawnDefaultboss = new MagnesifyBoss("Magnesify");
                                        spawnDefaultboss.spawn(databaseManager.TriggerTypeDungeons().getBosspointsLocation(get().getPlayers().getLastDungeon(killer), level.get(killer.getUniqueId())), killer);
                                    }
                                    dungeonPlayer.messageManager().title(new LanguageFile().getLanguage().getString("messages.dungeon.new-level.title"), new LanguageFile().getLanguage().getString("messages.dungeon.new-level.subtitle").replace("#level", String.valueOf(level.get(killer.getUniqueId()))));
                                } else {
                                    sendspawn(killer);
                                    statsManager.updateMatch(killer.getUniqueId().toString(), 1);
                                    TriggerType triggerType = new TriggerType(killer);
                                    triggerType.leave(killer, get().getPlayers().getLastDungeon(killer));
                                    get().getPlayers().updatePoint(killer, triggerType.point(get().getPlayers().getLastDungeon(killer)));
                                    get().getPlayers().updateDungeonStatus(killer, false);
                                    dungeonPlayer.messageManager().title(new LanguageFile().getLanguage().getString("messages.status.win.title"), new LanguageFile().getLanguage().getString("messages.status.win.subtitle").replace("#point", String.valueOf(triggerType.parameters(get().getPlayers().getLastDungeon(killer)).point())));
                                }
                            }
                        }
                    }
                });
            } else {
                if (dungeonPlayer.inDungeon()) {
                    if (get().getPlayers().getLastBoss(killer).equalsIgnoreCase(metadataValue)) {
                        if (databaseManager.getType(get().getPlayers().getLastDungeon(killer)).equalsIgnoreCase("Normal")) {
                            sendspawn(killer);
                            Dungeon dungeon = new Dungeon(get().getPlayers().getLastDungeon(killer));
                            dungeon.status(true);
                            entity.remove();
                            get().getPlayers().setDone(killer, true);
                            dungeon.events().stop(killer);
                            get().getPlayers().updateDungeonStatus(killer, false);
                            get().getPlayers().updatePoint(killer, dungeon.point());
                            event.getDrops().clear();
                            MagnesifyBoss magnesifyBoss = new MagnesifyBoss(boss_name);
                            for (String a : magnesifyBoss.drops()) {
                                String[] split = a.split(":");
                                ItemStack item = new ItemStack(Material.getMaterial(split[0]));
                                ItemMeta itemMeta = item.getItemMeta();
                                item.setAmount(Integer.parseInt(split[1]));
                                item.setItemMeta(itemMeta);
                                killer.getInventory().addItem(item);
                            }
                            StatsManager statsManager = new StatsManager();
                            statsManager.updateMatch(killer.getUniqueId().toString(), 1);
                            statsManager.updateKill(killer.getUniqueId().toString(), 1);
                            dungeonPlayer.updateCurrentLevelForDungeon(dungeon.parameters().name(), dungeon.parameters().next());
                            dungeonPlayer.messageManager().chat(new LanguageFile().getLanguage().getString("messages.status.win.chat"));
                            dungeonPlayer.messageManager().title(new LanguageFile().getLanguage().getString("messages.status.win.title"), new LanguageFile().getLanguage().getString("messages.status.win.subtitle").replace("#point", String.valueOf(dungeon.parameters().point())));
                        } else {
                            entity.remove();
                            event.getDrops().clear();
                            MagnesifyBoss magnesifyBoss = new MagnesifyBoss(boss_name);
                            for (String a : magnesifyBoss.drops()) {
                                String[] split = a.split(":");
                                ItemStack item = new ItemStack(Material.getMaterial(split[0]));
                                ItemMeta itemMeta = item.getItemMeta();
                                item.setAmount(Integer.parseInt(split[1]));
                                item.setItemMeta(itemMeta);
                                killer.getInventory().addItem(item);
                            }
                            StatsManager statsManager = new StatsManager();
                            statsManager.updateKill(killer.getUniqueId().toString(), 1);
                            PlayerMethods playerMethods = new PlayerMethods();
                            if (databaseManager.TriggerTypeDungeons().getTotalCheckpoints(playerMethods.getLastDungeon(killer)) != level.get(killer.getUniqueId())) {
                                level.put(killer.getUniqueId(), level.get(killer.getUniqueId()) + 1);
                                MagnesifyBoss next_boss = new MagnesifyBoss(databaseManager.TriggerTypeDungeons().getCheckpointBoss(get().getPlayers().getLastDungeon(killer), level.get(killer.getUniqueId())));
                                if (next_boss.exists()) {
                                    next_boss.spawn(databaseManager.TriggerTypeDungeons().getBosspointsLocation(get().getPlayers().getLastDungeon(killer), level.get(killer.getUniqueId())), killer);
                                } else {
                                    DungeonConsole dungeonConsole = new DungeonConsole();
                                    dungeonConsole.ConsoleOutputManager().write("<#4f91fc>[Magnesify Dungeons] &f" + next_boss.name() + " adında bir yaratık yok, hata oluşmaması adına Magnesify, Normal yaratığı doğuruyor...");
                                    playerMethods.updateLastBoss(killer, "Magnesify");
                                    MagnesifyBoss spawnDefaultboss = new MagnesifyBoss("Magnesify");
                                    spawnDefaultboss.spawn(databaseManager.TriggerTypeDungeons().getBosspointsLocation(get().getPlayers().getLastDungeon(killer), level.get(killer.getUniqueId())), killer);
                                }
                                dungeonPlayer.messageManager().title(new LanguageFile().getLanguage().getString("messages.dungeon.new-level.title"), new LanguageFile().getLanguage().getString("messages.dungeon.new-level.subtitle").replace("#level", String.valueOf(level.get(killer.getUniqueId()))));
                            } else {
                                sendspawn(killer);
                                statsManager.updateMatch(killer.getUniqueId().toString(), 1);
                                TriggerType triggerType = new TriggerType(killer);
                                triggerType.leave(killer, get().getPlayers().getLastDungeon(killer));
                                get().getPlayers().updatePoint(killer, triggerType.point(get().getPlayers().getLastDungeon(killer)));
                                get().getPlayers().updateDungeonStatus(killer, false);
                                dungeonPlayer.messageManager().title(new LanguageFile().getLanguage().getString("messages.status.win.title"), new LanguageFile().getLanguage().getString("messages.status.win.subtitle").replace("#point", String.valueOf(triggerType.parameters(get().getPlayers().getLastDungeon(killer)).point())));
                            }
                        }
                    }
                }
            }
        }
    }
}
