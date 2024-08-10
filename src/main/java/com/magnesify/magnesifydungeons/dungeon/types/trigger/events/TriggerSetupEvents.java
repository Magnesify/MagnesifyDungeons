package com.magnesify.magnesifydungeons.dungeon.types.trigger.events;

import com.magnesify.magnesifydungeons.MagnesifyDungeons;
import com.magnesify.magnesifydungeons.boss.MagnesifyBoss;
import com.magnesify.magnesifydungeons.dungeon.entitys.DungeonPlayer;
import com.magnesify.magnesifydungeons.modules.managers.DatabaseManager;
import com.magnesify.magnesifydungeons.storage.PlayerMethods;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;

import static com.magnesify.magnesifydungeons.MagnesifyDungeons.get;
import static com.magnesify.magnesifydungeons.dungeon.entitys.DungeonPlayer.parseHexColors;
import static com.magnesify.magnesifydungeons.dungeon.types.TriggerType.inGameHashMap;
import static com.magnesify.magnesifydungeons.dungeon.types.TriggerType.level;
import static com.magnesify.magnesifydungeons.dungeon.types.trigger.TriggerVector.isInTriggerLocation;
import static com.magnesify.magnesifydungeons.dungeon.types.trigger.commands.TriggerTypeDungeon.new_dungeon;
import static com.magnesify.magnesifydungeons.modules.Defaults.TEXT_PREFIX;

public class TriggerSetupEvents implements Listener {

    public static HashMap<String, Integer> setupDataHolder = new HashMap<>();

    public TriggerSetupEvents(MagnesifyDungeons magnesifyDungeons) {}

    @EventHandler
    public void place(PlayerDropItemEvent event) {
        ItemStack item = event.getItemDrop().getItemStack();
        if (item == null) {
            return;
        }
        if (item.hasItemMeta() && item.getItemMeta() != null) {
            ItemMeta meta = item.getItemMeta();
            if (meta.getDisplayName().startsWith(ChatColor.translateAlternateColorCodes('&', "&aBaşlangıç Bölgesi"))) {
                event.setCancelled(true);
            } else if (meta.getDisplayName().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', "&aYeni Seviyeye Giriş Bölgesi"))) {
                event.setCancelled(true);
            } else if (meta.getDisplayName().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', "&aFinal Yaratığının Doğum Noktası"))) {
                event.setCancelled(true);
            } else if (meta.getDisplayName().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', "&cKurulumu İptal Et"))) {
                event.setCancelled(true);
            } else if (meta.getDisplayName().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', "&eKurulumu Bitir"))) {
                event.setCancelled(true);
            }
        }
    }
    @EventHandler
    public void place(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();
        if (item == null) {
            return;
        }
        if (item.hasItemMeta() && item.getItemMeta() != null) {
            ItemMeta meta = item.getItemMeta();
            if (meta.getDisplayName().startsWith(ChatColor.translateAlternateColorCodes('&', "&aBaşlangıç Bölgesi"))) {
                event.setCancelled(true);
            } else if (meta.getDisplayName().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', "&aYeni Seviyeye Giriş Bölgesi"))) {
                event.setCancelled(true);
            } else if (meta.getDisplayName().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', "&aFinal Yaratığının Doğum Noktası"))) {
                event.setCancelled(true);
            } else if (meta.getDisplayName().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', "&cKurulumu İptal Et"))) {
                event.setCancelled(true);
            } else if (meta.getDisplayName().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', "&eKurulumu Bitir"))) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void place(InventoryMoveItemEvent event) {
        ItemStack item = event.getItem();
        if (item == null) {
            return;
        }
        if (item.hasItemMeta() && item.getItemMeta() != null) {
            ItemMeta meta = item.getItemMeta();
            if (meta.getDisplayName().startsWith(ChatColor.translateAlternateColorCodes('&', "&aBaşlangıç Bölgesi"))) {
                event.setCancelled(true);
            } else if (meta.getDisplayName().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', "&aYeni Seviyeye Giriş Bölgesi"))) {
                event.setCancelled(true);
            } else if (meta.getDisplayName().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', "&aFinal Yaratığının Doğum Noktası"))) {
                event.setCancelled(true);
            } else if (meta.getDisplayName().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', "&cKurulumu İptal Et"))) {
                event.setCancelled(true);
            } else if (meta.getDisplayName().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', "&eKurulumu Bitir"))) {
                event.setCancelled(true);
            }
        }
    }
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (item == null) {
            return;
        }
        if (item.hasItemMeta()) {
            ItemMeta itemMeta =  item.getItemMeta();
            DatabaseManager databaseManager = new DatabaseManager(MagnesifyDungeons.get());
            if (itemMeta.getDisplayName().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', "&aBaşlangıç Bölgesi"))) {
                if(new_dungeon.get("new") != null) {
                    if (databaseManager.TriggerTypeDungeons().isDungeonExists(new_dungeon.get("new"))) {
                        databaseManager.TriggerTypeDungeons().setSpawn(new_dungeon.get("new"), event.getPlayer().getLocation());
                        event.getPlayer().sendMessage(parseHexColors(String.format("%s %s için başlangıç bölgesi seçildi !", TEXT_PREFIX, new_dungeon.get("new"))));
                    }
                }
            } else if (itemMeta.getDisplayName().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', "&aYaratık Doğum Noktası"))) {
                if(setupDataHolder.get("level_boss") == null) {
                    setupDataHolder.put("level_boss", 1);
                    databaseManager.CreateNewBosspoints(new_dungeon.get("new"), setupDataHolder.get("level_boss"), event.getPlayer().getLocation());
                } else {
                    setupDataHolder.put("level_boss", setupDataHolder.get("level_boss") + 1);
                    databaseManager.CreateNewBosspoints(new_dungeon.get("new"), setupDataHolder.get("level_boss"), event.getPlayer().getLocation());
                }
                event.getPlayer().sendMessage(parseHexColors(String.format("%s %s. seviye için yaratığın doğum noktası seçildi ! Koordinat bilgisi: &b%s x:%s, y:%s, z:%s", TEXT_PREFIX, String.valueOf(setupDataHolder.get("level")), event.getPlayer().getLocation().getWorld().getName(), String.valueOf(event.getPlayer().getLocation().getX()), String.valueOf(event.getPlayer().getLocation().getY()), String.valueOf(event.getPlayer().getLocation().getZ()))));
            } else if (itemMeta.getDisplayName().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', "&aYeni Seviyeye Giriş Bölgesi"))) {
                if(setupDataHolder.get("level") == null) {
                    setupDataHolder.put("level", 1);
                    databaseManager.CreateNewCheckpoint(new_dungeon.get("new"), setupDataHolder.get("level"), event.getPlayer().getLocation());
                } else {
                    setupDataHolder.put("level", setupDataHolder.get("level") + 1);
                    databaseManager.CreateNewCheckpoint(new_dungeon.get("new"), setupDataHolder.get("level"), event.getPlayer().getLocation());
                }
                event.getPlayer().sendMessage(parseHexColors(String.format("%s %s. seviye seçildi ! Koordinat bilgisi: &b%s x:%s, y:%s, z:%s", TEXT_PREFIX, String.valueOf(setupDataHolder.get("level")), event.getPlayer().getLocation().getWorld().getName(), String.valueOf(event.getPlayer().getLocation().getX()), String.valueOf(event.getPlayer().getLocation().getY()), String.valueOf(event.getPlayer().getLocation().getZ()))));
            } else if (itemMeta.getDisplayName().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', "&eKurulumu Bitir"))) {
                if (!databaseManager.TriggerTypeDungeons().isEnable(new_dungeon.get("new"))) {
                    event.getPlayer().sendMessage(parseHexColors(String.format("%s %s adlı zindanın kurulumu bitirildi !", TEXT_PREFIX, new_dungeon.get("new"))));
                    event.getPlayer().sendMessage(parseHexColors(String.format("%s %s için toplam checkpoint (seviye) sayısı %s.", TEXT_PREFIX, new_dungeon.get("new"), setupDataHolder.get("level"))));
                    databaseManager.TriggerTypeDungeons().setEnable(new_dungeon.get("new"), "Evet");
                    databaseManager.TriggerTypeDungeons().setTotalCheckpoints(new_dungeon.get("new"), setupDataHolder.get("level"));
                    new_dungeon.clear();
                    event.getPlayer().getInventory().clear();
                } else {
                    event.getPlayer().sendMessage(parseHexColors(String.format("%s %s adlı zindanın kurulumu zaten bitirilmiş !", TEXT_PREFIX, new_dungeon.get("new"))));
                }
            } else if (itemMeta.getDisplayName().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', "&aFinal Yaratığının Doğum Noktası"))) {
                event.getPlayer().sendMessage(parseHexColors(String.format("%s %s adlı zindanın final yaratığı için doğum noktası seçildi ! Koordinat bilgisi: &b%s x:%s, y:%s, z:%s", TEXT_PREFIX, new_dungeon.get("new"), event.getPlayer().getLocation().getWorld().getName(), String.valueOf(event.getPlayer().getLocation().getX()), String.valueOf(event.getPlayer().getLocation().getY()), String.valueOf(event.getPlayer().getLocation().getZ()))));
                databaseManager.TriggerTypeDungeons().setBossSpawn(new_dungeon.get("new"), event.getPlayer().getLocation());
            } else if (itemMeta.getDisplayName().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', "&cKurulumu İptal Et"))) {
                event.getPlayer().sendMessage(parseHexColors(String.format("%s %s adlı zindanın kurulumu iptal edildi, zindan silindi !", TEXT_PREFIX, new_dungeon.get("new"))));
                event.getPlayer().getInventory().clear();
                new_dungeon.clear();
                if(databaseManager.TriggerTypeDungeons().isDungeonExists(new_dungeon.get("new"))) databaseManager.TriggerTypeDungeons().deleteDungeon(new_dungeon.get("new"));
            }
        }
    }

    @EventHandler
    public void move(PlayerMoveEvent event) {
        Location location = event.getPlayer().getLocation();
        PlayerMethods playerMethods = new PlayerMethods(event.getPlayer());
        if(playerMethods.inDungeon(event.getPlayer())) {
            DatabaseManager databaseManager = new DatabaseManager(MagnesifyDungeons.get());
            if (inGameHashMap.get(event.getPlayer().getUniqueId()) == null) return;
            if(inGameHashMap.get(event.getPlayer().getUniqueId())) {
                event.setCancelled(true);
            }

            if(level.get(event.getPlayer().getUniqueId()) != null) {
                DungeonPlayer dungeonPlayer = new DungeonPlayer(event.getPlayer());
                if(isInTriggerLocation(location, databaseManager.TriggerTypeDungeons().getCheckpointLocation(playerMethods.getLastDungeon(event.getPlayer()), level.get(event.getPlayer().getUniqueId())))) {
                    if(databaseManager.TriggerTypeDungeons().getTotalCheckpoints(playerMethods.getLastDungeon(event.getPlayer())) == level.get(event.getPlayer().getUniqueId())) {
                        dungeonPlayer.messageManager().title(get().getConfig().getString("settings.messages.dungeon.last-level.title"), get().getConfig().getString("settings.messages.dungeon.last-level.subtitle").replace("#level", String.valueOf(level.get(event.getPlayer().getUniqueId()))));
                    } else {
                        level.put(event.getPlayer().getUniqueId(), level.get(event.getPlayer().getUniqueId()) + 1);
                        MagnesifyBoss magnesifyBoss = new MagnesifyBoss("Magnesify");
                        magnesifyBoss.spawn(databaseManager.TriggerTypeDungeons().getBosspointsLocation(playerMethods.getLastDungeon(event.getPlayer()),level.get(event.getPlayer().getUniqueId())), event.getPlayer());
                        dungeonPlayer.messageManager().title(get().getConfig().getString("settings.messages.dungeon.new-level.title"), get().getConfig().getString("settings.messages.dungeon.new-level.subtitle").replace("#level", String.valueOf(level.get(event.getPlayer().getUniqueId()))));
                    }
                }
            }
        }
    }
}
