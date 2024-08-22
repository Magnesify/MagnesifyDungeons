package com.magnesify.magnesifydungeons.dungeon.types.trigger.events;

import com.magnesify.magnesifydungeons.MagnesifyDungeons;
import com.magnesify.magnesifydungeons.genus.DungeonGenus;
import com.magnesify.magnesifydungeons.languages.LanguageFile;
import com.magnesify.magnesifydungeons.modules.managers.DatabaseManager;
import com.magnesify.magnesifydungeons.modules.managers.DungeonContentManager;
import com.magnesify.magnesifydungeons.storage.PlayerMethods;
import org.bukkit.ChatColor;
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
import static com.magnesify.magnesifydungeons.dungeon.TriggerType.inGameHashMap;
import static com.magnesify.magnesifydungeons.dungeon.entitys.DungeonPlayer.parseHexColors;
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
            if (meta.getDisplayName().startsWith(ChatColor.translateAlternateColorCodes('&', new LanguageFile().getLanguage().getString("plugin.setup.spawn")))) {
                event.setCancelled(true);
            } else if (meta.getDisplayName().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', new LanguageFile().getLanguage().getString("plugin.setup.new-level-entry-point")))) {
                event.setCancelled(true);
            } else if (meta.getDisplayName().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', new LanguageFile().getLanguage().getString("plugin.setup.cancel")))) {
                event.setCancelled(true);
            } else if (meta.getDisplayName().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', new LanguageFile().getLanguage().getString("plugin.setup.done")))) {
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
            if (meta.getDisplayName().startsWith(ChatColor.translateAlternateColorCodes('&', new LanguageFile().getLanguage().getString("plugin.setup.spawn")))) {
                event.setCancelled(true);
            } else if (meta.getDisplayName().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', new LanguageFile().getLanguage().getString("plugin.setup.new-level-entry-point")))) {
                event.setCancelled(true);
            } else if (meta.getDisplayName().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', new LanguageFile().getLanguage().getString("plugin.setup.cancel")))) {
                event.setCancelled(true);
            } else if (meta.getDisplayName().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', new LanguageFile().getLanguage().getString("plugin.setup.done")))) {
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
            if (meta.getDisplayName().startsWith(ChatColor.translateAlternateColorCodes('&', new LanguageFile().getLanguage().getString("plugin.setup.spawn")))) {
                event.setCancelled(true);
            } else if (meta.getDisplayName().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', new LanguageFile().getLanguage().getString("plugin.setup.new-level-entry-point")))) {
                event.setCancelled(true);
            } else if (meta.getDisplayName().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', new LanguageFile().getLanguage().getString("plugin.setup.cancel")))) {
                event.setCancelled(true);
            } else if (meta.getDisplayName().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', new LanguageFile().getLanguage().getString("plugin.setup.done")))) {
                event.setCancelled(true);
            }
        }
    }
    @Deprecated
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        DungeonContentManager dungeonContentManager = new DungeonContentManager();
        dungeonContentManager.CreateNewDungeonChest(event);
        if(item == null) return;
        ItemMeta itemMeta = item.getItemMeta();
        if(itemMeta == null) return;
        if (itemMeta.getDisplayName().equalsIgnoreCase(parseHexColors(get().getConfig().getString("settings.skill-tools.GHOST.display")))) {
            DungeonGenus dungeonGenus = new DungeonGenus(event.getPlayer());
            if(dungeonGenus.isGenusSet()) {
                dungeonGenus.skills().Ghost(event.getPlayer());
            }
        }
        if(new_dungeon.get("new") != null) {
            if (item == null) {
                return;
            }
            if (item.hasItemMeta()) {
                DatabaseManager databaseManager = new DatabaseManager(get());
                if (itemMeta.getDisplayName().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&',  new LanguageFile().getLanguage().getString("plugin.setup.spawn")))) {
                    if (new_dungeon.get("new") != null) {
                        if (databaseManager.TriggerTypeDungeons().isDungeonExists(new_dungeon.get("new"))) {
                            databaseManager.TriggerTypeDungeons().setSpawn(new_dungeon.get("new"), event.getPlayer().getLocation());
                            event.getPlayer().sendMessage(parseHexColors(String.format(new LanguageFile().getLanguage().getString("plugin.setup.messages.spawn-selected"), TEXT_PREFIX, new_dungeon.get("new"))));
                        }
                    }
                } else if (itemMeta.getDisplayName().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', new LanguageFile().getLanguage().getString("plugin.setup.boss-spawn-point")))) {
                    if (setupDataHolder.get("level_boss") == null) {
                        setupDataHolder.put("level_boss", 1);
                        databaseManager.CreateNewBosspoints(new_dungeon.get("new"), setupDataHolder.get("level_boss"), event.getPlayer().getLocation());
                    } else {
                        setupDataHolder.put("level_boss", setupDataHolder.get("level_boss") + 1);
                        databaseManager.CreateNewBosspoints(new_dungeon.get("new"), setupDataHolder.get("level_boss"), event.getPlayer().getLocation());
                    }
                    event.getPlayer().sendMessage(parseHexColors(String.format(new LanguageFile().getLanguage().getString("plugin.setup.messages.new-boss-point-selected"), TEXT_PREFIX, String.valueOf(setupDataHolder.get("level_boss")), event.getPlayer().getLocation().getWorld().getName(), String.valueOf(event.getPlayer().getLocation().getX()), String.valueOf(event.getPlayer().getLocation().getY()), String.valueOf(event.getPlayer().getLocation().getZ()))));
                } else if (itemMeta.getDisplayName().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', new LanguageFile().getLanguage().getString("plugin.setup.new-level-entry-point")))) {
                    if (setupDataHolder.get("level") == null) {
                        setupDataHolder.put("level", 1);
                        databaseManager.CreateNewCheckpoint(new_dungeon.get("new"), setupDataHolder.get("level"), event.getPlayer().getLocation(), "Magnesify");
                    } else {
                        setupDataHolder.put("level", setupDataHolder.get("level") + 1);
                        databaseManager.CreateNewCheckpoint(new_dungeon.get("new"), setupDataHolder.get("level"), event.getPlayer().getLocation(), "Magnesify");
                    }
                    event.getPlayer().sendMessage(parseHexColors(String.format(new LanguageFile().getLanguage().getString("plugin.setup.messages.new-level-point-selected"), TEXT_PREFIX, String.valueOf(setupDataHolder.get("level")), event.getPlayer().getLocation().getWorld().getName(), String.valueOf(event.getPlayer().getLocation().getX()), String.valueOf(event.getPlayer().getLocation().getY()), String.valueOf(event.getPlayer().getLocation().getZ()))));
                } else if (itemMeta.getDisplayName().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', new LanguageFile().getLanguage().getString("plugin.setup.done")))) {
                    if (!databaseManager.TriggerTypeDungeons().isEnable(new_dungeon.get("new"))) {
                        event.getPlayer().sendMessage(parseHexColors(String.format(new LanguageFile().getLanguage().getString("plugin.setup.messages.done"), TEXT_PREFIX, new_dungeon.get("new"))));
                        event.getPlayer().sendMessage(parseHexColors(String.format(new LanguageFile().getLanguage().getString("plugin.setup.messages.info"), TEXT_PREFIX, new_dungeon.get("new"), setupDataHolder.get("level"))));
                        databaseManager.TriggerTypeDungeons().setEnable(new_dungeon.get("new"), "Evet");
                        databaseManager.TriggerTypeDungeons().setTotalCheckpoints(new_dungeon.get("new"), setupDataHolder.get("level"));
                        new_dungeon.remove("new");
                        setupDataHolder.remove("level");
                        setupDataHolder.remove("level_boss");
                        setupDataHolder.clear();
                        new_dungeon.clear();
                        event.getPlayer().getInventory().clear();
                    } else {
                        event.getPlayer().sendMessage(parseHexColors(String.format(new LanguageFile().getLanguage().getString("plugin.setup.messages.already-done"), TEXT_PREFIX, new_dungeon.get("new"))));
                    }
                } else if (itemMeta.getDisplayName().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', new LanguageFile().getLanguage().getString("plugin.setup.cancel")))) {
                    event.getPlayer().sendMessage(parseHexColors(String.format(new LanguageFile().getLanguage().getString("plugin.setup.messages.cancelled"), TEXT_PREFIX, new_dungeon.get("new"))));
                    event.getPlayer().getInventory().clear();
                    new_dungeon.remove("new");
                    setupDataHolder.remove("level");
                    setupDataHolder.clear();
                    new_dungeon.clear();
                    setupDataHolder.remove("level_boss");
                    if (databaseManager.TriggerTypeDungeons().isDungeonExists(new_dungeon.get("new")))
                        databaseManager.TriggerTypeDungeons().deleteDungeon(new_dungeon.get("new"));
                }
            }
        }
    }

    @EventHandler
    public void move(PlayerMoveEvent event) {
        PlayerMethods playerMethods = new PlayerMethods(event.getPlayer());
        if(playerMethods.inDungeon(event.getPlayer())) {
            if (inGameHashMap.get(event.getPlayer().getUniqueId()) == null) return;
            if(inGameHashMap.get(event.getPlayer().getUniqueId())) {
                event.setCancelled(true);
            }
        }
    }
}
