package com.magnesify.magnesifydungeons.dungeon.types.trigger.events;

import com.magnesify.magnesifydungeons.MagnesifyDungeons;
import com.magnesify.magnesifydungeons.modules.managers.DatabaseManager;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class TriggerSetupBlockPlaces implements Listener {

    public TriggerSetupBlockPlaces(MagnesifyDungeons magnesifyDungeons) {}

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
            }
        }
    }

    // çalışmıyor
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (item == null) {
            return;
        }
        if (item.hasItemMeta() && item.getItemMeta() instanceof LeatherArmorMeta) {
            ItemMeta itemMeta =  item.getItemMeta();
            String[] splt = itemMeta.getDisplayName().split(" : ");
            if (itemMeta.getDisplayName().startsWith(ChatColor.translateAlternateColorCodes('&', "&aBaşlangıç Bölgesi"))) {
                event.getPlayer().sendMessage("Merhaba " + splt[1]);
                DatabaseManager databaseManager = new DatabaseManager(MagnesifyDungeons.get());
                if(databaseManager.TriggerTypeDungeons().isDungeonExists(splt[1])) {
                    databaseManager.TriggerTypeDungeons().setSpawn(splt[1], event.getPlayer().getLocation());
                    event.getPlayer().sendMessage("Başlangıç " + splt[1] + " için ayarlandı.");
                }
            }
        }
    }

}
