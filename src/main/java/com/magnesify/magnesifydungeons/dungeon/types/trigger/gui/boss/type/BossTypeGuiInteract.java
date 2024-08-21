package com.magnesify.magnesifydungeons.dungeon.types.trigger.gui.boss.type;

import com.magnesify.magnesifydungeons.MagnesifyDungeons;
import com.magnesify.magnesifydungeons.dungeon.types.trigger.gui.boss.MagnesifyBossGuiLoader;
import com.magnesify.magnesifydungeons.modules.managers.DatabaseManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import static com.magnesify.magnesifydungeons.MagnesifyDungeons.get;
import static com.magnesify.magnesifydungeons.dungeon.entitys.DungeonPlayer.parseHexColors;
import static com.magnesify.magnesifydungeons.dungeon.types.trigger.commands.TriggerTypeDungeon.new_dungeon;

public class BossTypeGuiInteract implements Listener {
    public BossTypeGuiInteract(MagnesifyDungeons magnesifyDungeons) {}

    @Deprecated
    @EventHandler
    public void onInteract(InventoryClickEvent event) {
        String gui_title = event.getView().getTitle();
        Player player = (Player) event.getWhoClicked();
        if(gui_title.equalsIgnoreCase(parseHexColors(get().getConfig().getString("settings.trigger-type.types.title")))) {
            event.setCancelled(true);
            for (String a : new DatabaseManager(get()).boss().getAllBoss()) {
                if (event.getCurrentItem() != null) {
                    if(event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(parseHexColors("&b"+a))) {
                        new DatabaseManager(get()).TriggerTypeDungeons().setCheckpointBoss(new_dungeon.get("dungeon_name"), Integer.parseInt(new_dungeon.get("level_dungeon_edit")), a);
                        player.closeInventory();
                        MagnesifyBossGuiLoader.openInventory(player,new_dungeon.get("dungeon_name"));
                        return;
                    }
                }
            }
        }
    }
}
