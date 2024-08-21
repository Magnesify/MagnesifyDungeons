package com.magnesify.magnesifydungeons.boss.gui.settings;

import com.magnesify.magnesifydungeons.MagnesifyDungeons;
import com.magnesify.magnesifydungeons.boss.MagnesifyBoss;
import com.magnesify.magnesifydungeons.boss.gui.BossGuiLoader;
import com.magnesify.magnesifydungeons.modules.managers.DatabaseManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import static com.magnesify.magnesifydungeons.MagnesifyDungeons.get;
import static com.magnesify.magnesifydungeons.boss.BossManager.boss_manager;
import static com.magnesify.magnesifydungeons.dungeon.entitys.DungeonPlayer.parseHexColors;

public class SettingsGuiInteract implements Listener {
    public SettingsGuiInteract(MagnesifyDungeons magnesifyDungeons) {}

    @Deprecated
    @EventHandler
    public void onInteract(InventoryClickEvent event) {
        String gui_title = event.getView().getTitle();
        if(gui_title.equalsIgnoreCase(parseHexColors(get().getConfig().getString("settings.boss.settings.title")))) {
            Player player = (Player) event.getWhoClicked();
            event.setCancelled(true);
            switch (event.getSlot()) {
                case 19:
                    if(event.getClick().isLeftClick()) {
                        DatabaseManager databaseManager = new DatabaseManager(get());
                        databaseManager.boss().removeHealth(boss_manager.get("boss"), 1);
                    } else if (event.getClick().isRightClick()) {
                        DatabaseManager databaseManager = new DatabaseManager(get());
                        databaseManager.boss().addHealth(boss_manager.get("boss"), 1);
                    }
                    player.closeInventory();
                    SettingsGuiLoader.openInventory(player, new MagnesifyBoss(boss_manager.get("boss")));
                    break;
                case 21:
                    if(event.getClick().isLeftClick()) {
                        DatabaseManager databaseManager = new DatabaseManager(get());
                        databaseManager.boss().removeKnockback(boss_manager.get("boss"), 1);
                    } else if (event.getClick().isRightClick()) {
                        DatabaseManager databaseManager = new DatabaseManager(get());
                        databaseManager.boss().addKnockback(boss_manager.get("boss"), 1);
                    }
                    player.closeInventory();
                    SettingsGuiLoader.openInventory(player, new MagnesifyBoss(boss_manager.get("boss")));
                    break;
                case 23:
                    if(event.getClick().isLeftClick()) {
                        DatabaseManager databaseManager = new DatabaseManager(get());
                        databaseManager.boss().removeDamage(boss_manager.get("boss"), 1);
                    } else if (event.getClick().isRightClick()) {
                        DatabaseManager databaseManager = new DatabaseManager(get());
                        databaseManager.boss().addDamage(boss_manager.get("boss"), 1);
                    }
                    player.closeInventory();
                    SettingsGuiLoader.openInventory(player, new MagnesifyBoss(boss_manager.get("boss")));
                    break;
                case 25:
                    if(event.getClick().isLeftClick()) {
                        DatabaseManager databaseManager = new DatabaseManager(get());
                        databaseManager.boss().setMythic(boss_manager.get("boss"), true);
                    } else if (event.getClick().isRightClick()) {
                        DatabaseManager databaseManager = new DatabaseManager(get());
                        databaseManager.boss().setMythic(boss_manager.get("boss"), false);
                    }
                    player.closeInventory();
                    SettingsGuiLoader.openInventory(player, new MagnesifyBoss(boss_manager.get("boss")));
                    break;
                case 40:
                    player.closeInventory();
                    BossGuiLoader.openInventory(player, new MagnesifyBoss(boss_manager.get("boss")));
                    break;
            }
        }
    }
}
