package com.magnesify.magnesifydungeons.genus.gui;

import com.magnesify.magnesifydungeons.MagnesifyDungeons;
import com.magnesify.magnesifydungeons.dungeon.entitys.DungeonPlayer;
import com.magnesify.magnesifydungeons.files.GenusFile;
import com.magnesify.magnesifydungeons.genus.DungeonGenus;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import static com.magnesify.magnesifydungeons.MagnesifyDungeons.get;
import static com.magnesify.magnesifydungeons.dungeon.entitys.DungeonPlayer.parseHexColors;

public class GenusGuiInteract implements Listener {
    public GenusGuiInteract(MagnesifyDungeons magnesifyDungeons) {}

    @Deprecated
    @EventHandler
    public void onInteract(InventoryClickEvent event) {
        String gui_title = event.getView().getTitle();
        if(gui_title.equals(parseHexColors(get().getConfig().getString("settings.genus.title")))) {
            event.setCancelled(true);
            GenusFile genusFile = new GenusFile();
            DungeonPlayer dungeonPlayer = new DungeonPlayer((Player) event.getWhoClicked());
            DungeonGenus dungeonGenus = new DungeonGenus((Player) event.getWhoClicked());
            for(String genus : genusFile.getGenusConfig().getConfigurationSection("dungeon-genus").getKeys(false)) {
                int slot = genusFile.getGenusConfig().getInt("dungeon-genus." + genus + ".slot");
                if(event.getSlot() == slot) {
                    dungeonGenus.setGenus(genus);
                    event.getWhoClicked().closeInventory();
                    dungeonPlayer.messageManager().title(get().getConfig().getString("settings.messages.genus.selected.title"),get().getConfig().getString("settings.messages.genus.selected.subtitle").replace("#genus", genus));
                    return;
                }
            }
        }
    }
}
