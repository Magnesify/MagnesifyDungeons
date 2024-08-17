package com.magnesify.magnesifydungeons.dungeon.types.trigger.gui;

import com.magnesify.magnesifydungeons.MagnesifyDungeons;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.HashMap;

import static com.magnesify.magnesifydungeons.MagnesifyDungeons.get;
import static com.magnesify.magnesifydungeons.dungeon.entitys.DungeonPlayer.parseHexColors;

public class TriggerTypeLevelBossInteract implements Listener {
    public TriggerTypeLevelBossInteract(MagnesifyDungeons magnesifyDungeons) {}

    public static HashMap<String, String> trigger_type_edit = new HashMap<>();

    @Deprecated
    @EventHandler
    public void onInteract(InventoryClickEvent event) {
        String gui_title = event.getView().getTitle();
        if(gui_title.equalsIgnoreCase(parseHexColors(get().getConfig().getString("settings.trigger-type.title")))) {
            event.setCancelled(true);
        }
    }
}
