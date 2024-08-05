package com.magnesify.magnesifydungeons.boss.events;

import com.magnesify.magnesifydungeons.MagnesifyDungeons;
import com.magnesify.magnesifydungeons.boss.MagnesifyBoss;
import com.magnesify.magnesifydungeons.dungeon.Dungeon;
import com.magnesify.magnesifydungeons.dungeon.entitys.DungeonConsole;
import com.magnesify.magnesifydungeons.dungeon.entitys.DungeonPlayer;
import com.magnesify.magnesifydungeons.modules.Defaults;
import com.magnesify.magnesifydungeons.modules.StatsManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
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
import java.util.UUID;

import static com.magnesify.magnesifydungeons.MagnesifyDungeons.get;

public class BossDeathEvent implements Listener {
    public BossDeathEvent(MagnesifyDungeons magnesifyDungeons) {}

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
                                if(get().getConfig().getBoolean("settings.minimal-options.send-damage-title")) {
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
                            dungeonConsole.ConsoleOutputManager().write("<#4f91fc>[Magnesify Dungeons] &fBaşlangıcın kayıtlı olduğu dünya bulunamadı, dünya silindimi ?");
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
        if(killer != null) {
            if(killer instanceof Zombie) {
                if (killer.hasMetadata("name")) {
                    String metadataValue = entity.getMetadata("name").get(0).asString();
                    DungeonPlayer dungeonPlayer = new DungeonPlayer(entity);
                    if(dungeonPlayer.inDungeon()) {
                        if(get().getPlayers().getLastBoss(entity).equalsIgnoreCase(metadataValue)) {
                            Dungeon dungeon = new Dungeon(get().getPlayers().getLastDungeon(entity));
                            dungeon.status(true);
                            entity.remove();
                            get().getPlayers().setDone(entity, true);
                            dungeon.events().stop(entity);
                            get().getPlayers().updateDungeonStatus(entity, false);
                            get().getPlayers().updateDeath(entity, 1);
                            StatsManager statsManager = new StatsManager();
                            statsManager.updateMatch(killer.getUniqueId().toString(), 1);
                            statsManager.updateLose(entity.getPlayer().getUniqueId().toString(), 1);
                            dungeonPlayer.messageManager().chat(get().getConfig().getString("settings.messages.status.lose.chat"));
                            dungeonPlayer.messageManager().title(get().getConfig().getString("settings.messages.status.lose.title"), get().getConfig().getString("settings.messages.status.lose.subtitle"));
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
                if(get().getPlayers().getLastBoss(killer).equalsIgnoreCase(metadataValue)) {
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
                    for(String a : magnesifyBoss.drops()) {
                        String[] split = a.split(":");
                        ItemStack item = new ItemStack(Material.getMaterial(split[0]));
                        ItemMeta itemMeta = item.getItemMeta();
                        item.setAmount(Integer.parseInt(split[1]));
                        item.setItemMeta(itemMeta);
                        killer.getInventory().addItem(item);
                    }
                    StatsManager statsManager = new StatsManager();
                    statsManager.updateMatch(killer.getUniqueId().toString(), 1);
                    dungeonPlayer.updateCurrentLevelForDungeon(dungeon.parameters().name(), dungeon.parameters().next());
                    dungeonPlayer.messageManager().chat(get().getConfig().getString("settings.messages.status.win.chat"));
                    dungeonPlayer.messageManager().title(get().getConfig().getString("settings.messages.status.win.title"), get().getConfig().getString("settings.messages.status.win.subtitle").replace("#point", String.valueOf(dungeon.parameters().point())));
                }
            }
        }
    }

}
