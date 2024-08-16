package com.magnesify.magnesifydungeons.boss.gui.drops;

import com.magnesify.magnesifydungeons.MagnesifyDungeons;
import com.magnesify.magnesifydungeons.modules.managers.DatabaseManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import static com.magnesify.magnesifydungeons.MagnesifyDungeons.get;
import static com.magnesify.magnesifydungeons.boss.BossManager.boss_manager;
import static com.magnesify.magnesifydungeons.dungeon.entitys.DungeonPlayer.parseHexColors;

public class DropsGuiInteract implements Listener {
    public DropsGuiInteract(MagnesifyDungeons magnesifyDungeons) {}

    @Deprecated
    @EventHandler
    public void onInteract(InventoryCloseEvent event) {
        String gui_title = event.getView().getTitle();
        if(gui_title.equalsIgnoreCase(parseHexColors(get().getConfig().getString("settings.boss.drops.title")))) {
            String drops = "";
            DatabaseManager databaseManager = new DatabaseManager(get());
            for(ItemStack itemStack : event.getInventory().getContents()) {
                if(itemStack != null) {
                    drops += itemStack.getType().toString()+":"+itemStack.getAmount();
                    drops += "/";
                }
            }
            databaseManager.boss().setDrops(boss_manager.get("boss"), drops);
        }
    }
}
