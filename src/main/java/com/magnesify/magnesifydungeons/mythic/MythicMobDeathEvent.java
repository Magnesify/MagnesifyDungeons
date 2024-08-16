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
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.magnesify.magnesifydungeons.MagnesifyDungeons.get;
import static com.magnesify.magnesifydungeons.MagnesifyDungeons.locale;
import static com.magnesify.magnesifydungeons.dungeon.TriggerType.level;

public class MythicMobDeathEvent implements Listener {
    public MythicMobDeathEvent(MagnesifyDungeons magnesifyDungeons) {}

    @EventHandler
    public void onDeath(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        if(event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            if(entity.hasMetadata("name")) {
                String metadataValue = entity.getMetadata("name").get(0).asString();
                DungeonPlayer dungeonPlayer = new DungeonPlayer(player);
                if(metadataValue != null) {
                    if(dungeonPlayer.inDungeon()) {
                        if (get().getPlayers().getLastBoss(player).equalsIgnoreCase(metadataValue)) {
                            if (entity.hasMetadata("name")) {
                                if (get().getConfig().getBoolean("settings.minimal-options.send-damage-title")) {
                                    dungeonPlayer.messageManager().title("&f", "&c&l-" + String.valueOf(event.getDamage()).substring(0, 2));
                                }
                            }
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
                        if (Bukkit.getWorld(defaults.MainSpawn().world()) != null) {
                            Location loc = new Location(Bukkit.getWorld(defaults.MainSpawn().world()), defaults.MainSpawn().x(), defaults.MainSpawn().y(), defaults.MainSpawn().z(), (float) defaults.MainSpawn().yaw(), (float) defaults.MainSpawn().pitch());
                            player.teleport(loc);
                        } else {
                            DungeonConsole dungeonConsole = new DungeonConsole();
                            dungeonConsole.ConsoleOutputManager().write( new LanguageFile().getLanguage(locale).getString("plugin.spawn-not-exists"));
                        }
                    }
                } else {
                    this.cancel();
                }
            }
        }.runTaskTimer(get(), 0, 20);
    }

    @EventHandler
    public void onKill(PlayerDeathEvent event) {
        Player entity = event.getEntity().getPlayer();
        Entity killer = event.getEntity().getKiller();
        DungeonGenus dungeonGenus = new DungeonGenus(entity);
        dungeonGenus.setGenusSkills();
        if(killer != null) {
            MythicAdapter mythicAdapter = new MythicAdapter();
            if(Bukkit.getPluginManager().getPlugin("MythicMobs") != null) {
                if (mythicAdapter.isMythicMob(killer)) {
                    DungeonPlayer dungeonPlayer = new DungeonPlayer(entity);
                    Optional<ActiveMob> optActiveMob = MythicBukkit.inst().getMobManager().getActiveMob(killer.getUniqueId());
                    optActiveMob.ifPresent(activeMob -> {
                        if (activeMob.getEntity().hasMetadata("name")) {
                            if (dungeonPlayer.inDungeon()) {
                                String metadataValue = entity.getMetadata("name").get(0).asString();
                                Dungeon dungeon = new Dungeon(get().getPlayers().getLastDungeon(entity));
                                DatabaseManager databaseManager = new DatabaseManager(get());
                                if (databaseManager.getType(get().getPlayers().getLastDungeon(entity)).equalsIgnoreCase("Normal")) {
                                    if (get().getPlayers().getLastBoss(entity).equalsIgnoreCase(metadataValue)) {
                                        dungeon.status(true);
                                        activeMob.setDead();
                                        get().getPlayers().setDone(entity, true);
                                        dungeon.events().stop(entity);
                                        get().getPlayers().updateDungeonStatus(entity, false);
                                        get().getPlayers().updateDeath(entity, 1);
                                        StatsManager statsManager = new StatsManager();
                                        statsManager.updateMatch(entity.getUniqueId().toString(), 1);
                                        statsManager.updateDeath(entity.getUniqueId().toString(), 1);
                                        statsManager.updateLose(entity.getPlayer().getUniqueId().toString(), 1);
                                        dungeonPlayer.messageManager().chat(get().getConfig().getString("settings.messages.status.lose.chat"));
                                        dungeonPlayer.messageManager().title(get().getConfig().getString("settings.messages.status.lose.title"), get().getConfig().getString("settings.messages.status.lose.subtitle"));
                                    }
                                } else {
                                    if (get().getPlayers().getLastBoss(entity).equalsIgnoreCase(metadataValue)) {
                                        activeMob.setDead();
                                        get().getPlayers().updateDungeonStatus(entity, false);
                                        get().getPlayers().updateDeath(entity, 1);
                                        StatsManager statsManager = new StatsManager();
                                        statsManager.updateMatch(entity.getUniqueId().toString(), 1);
                                        statsManager.updateDeath(entity.getUniqueId().toString(), 1);
                                        statsManager.updateLose(entity.getPlayer().getUniqueId().toString(), 1);
                                        dungeonPlayer.messageManager().chat(get().getConfig().getString("settings.messages.status.lose.chat"));
                                        dungeonPlayer.messageManager().title(get().getConfig().getString("settings.messages.status.lose.title"), get().getConfig().getString("settings.messages.status.lose.subtitle"));
                                    }
                                }
                            }
                        }
                    });

                } else {
                    if (killer instanceof Zombie && killer instanceof Skeleton) {
                        if (killer.hasMetadata("name")) {
                            String metadataValue = entity.getMetadata("name").get(0).asString();
                            DungeonPlayer dungeonPlayer = new DungeonPlayer(entity);
                            if (dungeonPlayer.inDungeon()) {
                                Dungeon dungeon = new Dungeon(get().getPlayers().getLastDungeon(entity));
                                DatabaseManager databaseManager = new DatabaseManager(get());
                                if (databaseManager.getType(get().getPlayers().getLastDungeon(entity)).equalsIgnoreCase("Normal")) {
                                    if (get().getPlayers().getLastBoss(entity).equalsIgnoreCase(metadataValue)) {
                                        dungeon.status(true);
                                        entity.remove();
                                        get().getPlayers().setDone(entity, true);
                                        dungeon.events().stop(entity);
                                        get().getPlayers().updateDungeonStatus(entity, false);
                                        get().getPlayers().updateDeath(entity, 1);
                                        StatsManager statsManager = new StatsManager();
                                        statsManager.updateMatch(entity.getUniqueId().toString(), 1);
                                        statsManager.updateDeath(entity.getUniqueId().toString(), 1);
                                        statsManager.updateLose(entity.getPlayer().getUniqueId().toString(), 1);
                                        dungeonPlayer.messageManager().chat(get().getConfig().getString("settings.messages.status.lose.chat"));
                                        dungeonPlayer.messageManager().title(get().getConfig().getString("settings.messages.status.lose.title"), get().getConfig().getString("settings.messages.status.lose.subtitle"));
                                    }
                                } else {
                                    if (get().getPlayers().getLastBoss(entity).equalsIgnoreCase(metadataValue)) {
                                        entity.remove();
                                        get().getPlayers().updateDungeonStatus(entity, false);
                                        get().getPlayers().updateDeath(entity, 1);
                                        StatsManager statsManager = new StatsManager();
                                        statsManager.updateMatch(entity.getUniqueId().toString(), 1);
                                        statsManager.updateDeath(entity.getUniqueId().toString(), 1);
                                        statsManager.updateLose(entity.getPlayer().getUniqueId().toString(), 1);
                                        dungeonPlayer.messageManager().chat(get().getConfig().getString("settings.messages.status.lose.chat"));
                                        dungeonPlayer.messageManager().title(get().getConfig().getString("settings.messages.status.lose.title"), get().getConfig().getString("settings.messages.status.lose.subtitle"));
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                if (killer instanceof Zombie && killer instanceof Skeleton) {
                    if (killer.hasMetadata("name")) {
                        String metadataValue = entity.getMetadata("name").get(0).asString();
                        DungeonPlayer dungeonPlayer = new DungeonPlayer(entity);
                        if (dungeonPlayer.inDungeon()) {
                            Dungeon dungeon = new Dungeon(get().getPlayers().getLastDungeon(entity));
                            DatabaseManager databaseManager = new DatabaseManager(get());
                            if (databaseManager.getType(get().getPlayers().getLastDungeon(entity)).equalsIgnoreCase("Normal")) {
                                if (get().getPlayers().getLastBoss(entity).equalsIgnoreCase(metadataValue)) {
                                    dungeon.status(true);
                                    entity.remove();
                                    get().getPlayers().setDone(entity, true);
                                    dungeon.events().stop(entity);
                                    get().getPlayers().updateDungeonStatus(entity, false);
                                    get().getPlayers().updateDeath(entity, 1);
                                    StatsManager statsManager = new StatsManager();
                                    statsManager.updateMatch(entity.getUniqueId().toString(), 1);
                                    statsManager.updateDeath(entity.getUniqueId().toString(), 1);
                                    statsManager.updateLose(entity.getPlayer().getUniqueId().toString(), 1);
                                    dungeonPlayer.messageManager().chat(get().getConfig().getString("settings.messages.status.lose.chat"));
                                    dungeonPlayer.messageManager().title(get().getConfig().getString("settings.messages.status.lose.title"), get().getConfig().getString("settings.messages.status.lose.subtitle"));
                                }
                            } else {
                                if (get().getPlayers().getLastBoss(entity).equalsIgnoreCase(metadataValue)) {
                                    entity.remove();
                                    get().getPlayers().updateDungeonStatus(entity, false);
                                    get().getPlayers().updateDeath(entity, 1);
                                    StatsManager statsManager = new StatsManager();
                                    statsManager.updateMatch(entity.getUniqueId().toString(), 1);
                                    statsManager.updateDeath(entity.getUniqueId().toString(), 1);
                                    statsManager.updateLose(entity.getPlayer().getUniqueId().toString(), 1);
                                    dungeonPlayer.messageManager().chat(get().getConfig().getString("settings.messages.status.lose.chat"));
                                    dungeonPlayer.messageManager().title(get().getConfig().getString("settings.messages.status.lose.title"), get().getConfig().getString("settings.messages.status.lose.subtitle"));
                                }
                            }
                        }
                    }
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
            if(dungeonPlayer.inDungeon()) {
                MythicAdapter mythicAdapter = new MythicAdapter();
                DatabaseManager databaseManager = new DatabaseManager(get());
                if(Bukkit.getPluginManager().getPlugin("MythicMobs") != null) {
                    if (mythicAdapter.isMythicMob(entity)) {
                        if (get().getPlayers().getLastBoss(killer).equalsIgnoreCase(metadataValue)) {
                            Optional<ActiveMob> optActiveMob = MythicBukkit.inst().getMobManager().getActiveMob(killer.getUniqueId());
                            optActiveMob.ifPresent(activeMob -> {
                                if (activeMob.getEntity().hasMetadata("name")) {
                                    if (databaseManager.getType(get().getPlayers().getLastDungeon(killer)).equalsIgnoreCase("Normal")) {
                                        sendspawn(killer);
                                        Dungeon dungeon = new Dungeon(get().getPlayers().getLastDungeon(killer));
                                        dungeon.status(true);
                                        activeMob.setDead();
                                        get().getPlayers().setDone(killer, true);
                                        dungeon.events().stop(killer);
                                        get().getPlayers().updateDungeonStatus(killer, false);
                                        get().getPlayers().updatePoint(killer, dungeon.point());
                                        get().getPlayers().updateKill(killer, 1);
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
                                        dungeonPlayer.messageManager().chat(get().getConfig().getString("settings.messages.status.win.chat"));
                                        dungeonPlayer.messageManager().title(get().getConfig().getString("settings.messages.status.win.title"), get().getConfig().getString("settings.messages.status.win.subtitle").replace("#point", String.valueOf(dungeon.parameters().point())));
                                    } else {
                                        activeMob.setDead();
                                        get().getPlayers().updateKill(killer, 1);
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
                                                dungeonConsole.ConsoleOutputManager().write(String.format(new LanguageFile().getLanguage("tr").getString("plugin.unknow-boss"),next_boss.name()));
                                                MagnesifyBoss spawnDefaultboss = new MagnesifyBoss("Magnesify");
                                                spawnDefaultboss.spawn(databaseManager.TriggerTypeDungeons().getBosspointsLocation(get().getPlayers().getLastDungeon(killer), level.get(killer.getUniqueId())), killer);
                                            }
                                            dungeonPlayer.messageManager().title(get().getConfig().getString("settings.messages.dungeon.new-level.title"), get().getConfig().getString("settings.messages.dungeon.new-level.subtitle").replace("#level", String.valueOf(level.get(killer.getUniqueId()))));
                                        } else {
                                            sendspawn(killer);
                                            statsManager.updateMatch(killer.getUniqueId().toString(), 1);
                                            TriggerType triggerType = new TriggerType(killer);
                                            triggerType.leave(killer, get().getPlayers().getLastDungeon(killer));
                                            get().getPlayers().updateDungeonStatus(killer, false);
                                            dungeonPlayer.messageManager().title(get().getConfig().getString("settings.messages.status.win.title"), get().getConfig().getString("settings.messages.status.win.subtitle").replace("#point", String.valueOf(triggerType.parameters(get().getPlayers().getLastDungeon(killer)).point())));
                                        }
                                    }
                                }
                            });
                        }
                    } else {
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
                                get().getPlayers().updateKill(killer, 1);
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
                                dungeonPlayer.messageManager().chat(get().getConfig().getString("settings.messages.status.win.chat"));
                                dungeonPlayer.messageManager().title(get().getConfig().getString("settings.messages.status.win.title"), get().getConfig().getString("settings.messages.status.win.subtitle").replace("#point", String.valueOf(dungeon.parameters().point())));
                            } else {
                                entity.remove();
                                get().getPlayers().updateKill(killer, 1);
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
                                        dungeonConsole.ConsoleOutputManager().write(String.format(new LanguageFile().getLanguage("tr").getString("plugin.unknow-boss"),next_boss.name()));
                                        MagnesifyBoss spawnDefaultboss = new MagnesifyBoss("Magnesify");
                                        spawnDefaultboss.spawn(databaseManager.TriggerTypeDungeons().getBosspointsLocation(get().getPlayers().getLastDungeon(killer), level.get(killer.getUniqueId())), killer);
                                    }
                                    dungeonPlayer.messageManager().title(get().getConfig().getString("settings.messages.dungeon.new-level.title"), get().getConfig().getString("settings.messages.dungeon.new-level.subtitle").replace("#level", String.valueOf(level.get(killer.getUniqueId()))));
                                } else {
                                    sendspawn(killer);
                                    statsManager.updateMatch(killer.getUniqueId().toString(), 1);
                                    TriggerType triggerType = new TriggerType(killer);
                                    triggerType.leave(killer, get().getPlayers().getLastDungeon(killer));
                                    get().getPlayers().updateDungeonStatus(killer, false);
                                    dungeonPlayer.messageManager().title(get().getConfig().getString("settings.messages.status.win.title"), get().getConfig().getString("settings.messages.status.win.subtitle").replace("#point", String.valueOf(triggerType.parameters(get().getPlayers().getLastDungeon(killer)).point())));
                                }
                            }
                        }
                    }
                } else {
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
                            get().getPlayers().updateKill(killer, 1);
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
                            dungeonPlayer.messageManager().chat(get().getConfig().getString("settings.messages.status.win.chat"));
                            dungeonPlayer.messageManager().title(get().getConfig().getString("settings.messages.status.win.title"), get().getConfig().getString("settings.messages.status.win.subtitle").replace("#point", String.valueOf(dungeon.parameters().point())));
                        } else {
                            entity.remove();
                            get().getPlayers().updateKill(killer, 1);
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
                                    dungeonConsole.ConsoleOutputManager().write(String.format(new LanguageFile().getLanguage("tr").getString("plugin.unknow-boss"),next_boss.name()));
                                    MagnesifyBoss spawnDefaultboss = new MagnesifyBoss("Magnesify");
                                    spawnDefaultboss.spawn(databaseManager.TriggerTypeDungeons().getBosspointsLocation(get().getPlayers().getLastDungeon(killer), level.get(killer.getUniqueId())), killer);
                                }
                                dungeonPlayer.messageManager().title(get().getConfig().getString("settings.messages.dungeon.new-level.title"), get().getConfig().getString("settings.messages.dungeon.new-level.subtitle").replace("#level", String.valueOf(level.get(killer.getUniqueId()))));
                            } else {
                                sendspawn(killer);
                                statsManager.updateMatch(killer.getUniqueId().toString(), 1);
                                TriggerType triggerType = new TriggerType(killer);
                                triggerType.leave(killer, get().getPlayers().getLastDungeon(killer));
                                get().getPlayers().updateDungeonStatus(killer, false);
                                dungeonPlayer.messageManager().title(get().getConfig().getString("settings.messages.status.win.title"), get().getConfig().getString("settings.messages.status.win.subtitle").replace("#point", String.valueOf(triggerType.parameters(get().getPlayers().getLastDungeon(killer)).point())));
                            }
                        }
                    }
                }
            }
        }
    }

}
