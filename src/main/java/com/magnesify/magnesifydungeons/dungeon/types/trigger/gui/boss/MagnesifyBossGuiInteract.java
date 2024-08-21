package com.magnesify.magnesifydungeons.dungeon.types.trigger.gui.boss;

import com.magnesify.magnesifydungeons.MagnesifyDungeons;
import com.magnesify.magnesifydungeons.dungeon.types.trigger.gui.boss.type.BossTypeGuiLoader;
import com.magnesify.magnesifydungeons.modules.managers.DatabaseManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import static com.magnesify.magnesifydungeons.MagnesifyDungeons.get;
import static com.magnesify.magnesifydungeons.dungeon.entitys.DungeonPlayer.parseHexColors;
import static com.magnesify.magnesifydungeons.dungeon.types.trigger.commands.TriggerTypeDungeon.new_dungeon;

public class MagnesifyBossGuiInteract implements Listener {
    public MagnesifyBossGuiInteract(MagnesifyDungeons magnesifyDungeons) {}

    @Deprecated
    @EventHandler
    public void onInteract(InventoryClickEvent event) {
        String gui_title = event.getView().getTitle();
        if(gui_title.equalsIgnoreCase(parseHexColors(get().getConfig().getString("settings.trigger-type.boss-list.title")))) {
            event.setCancelled(true);
            if(event.getCurrentItem() == null) return;
            for (int i = 1; i<=new DatabaseManager(get()).TriggerTypeDungeons().getTotalCheckpoints(new_dungeon.get("dungeon_name"));i++) {
                if(event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(parseHexColors("&d#" + i + " &8| &bBoss: &d" + new DatabaseManager(get()).TriggerTypeDungeons().getCheckpointBoss(new_dungeon.get("dungeon_name"), i)))) {
                    new_dungeon.remove("level_dungeon_edit");
                    new_dungeon.put("level_dungeon_edit", String.valueOf(i));
                    ((Player) event.getWhoClicked()).closeInventory();
                    BossTypeGuiLoader.openInventory((Player) event.getWhoClicked());
                }
            }
        }
    }
}
