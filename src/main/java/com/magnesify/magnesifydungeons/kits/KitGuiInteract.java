package com.magnesify.magnesifydungeons.kits;

import com.magnesify.magnesifydungeons.MagnesifyDungeons;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import static com.magnesify.magnesifydungeons.MagnesifyDungeons.get;

public class KitGuiInteract implements Listener {
    public KitGuiInteract(MagnesifyDungeons magnesifyDungeons) {}

    @Deprecated
    @EventHandler
    public void onInteract(InventoryClickEvent event) {
        String gui_title = event.getView().getTitle();
        if(gui_title.equals(ChatColor.translateAlternateColorCodes('&',  get().getConfig().getString("settings.kits.title")))) {
            event.setCancelled(true);
        }
    }
}
