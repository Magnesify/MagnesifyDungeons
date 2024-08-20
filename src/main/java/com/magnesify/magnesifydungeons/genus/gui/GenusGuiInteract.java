package com.magnesify.magnesifydungeons.genus.gui;

import com.magnesify.magnesifydungeons.MagnesifyDungeons;
import com.magnesify.magnesifydungeons.dungeon.entitys.DungeonPlayer;
import com.magnesify.magnesifydungeons.files.GenusFile;
import com.magnesify.magnesifydungeons.genus.DungeonGenus;
import com.magnesify.magnesifydungeons.languages.LanguageFile;
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
        if(gui_title.equalsIgnoreCase(parseHexColors(get().getConfig().getString("settings.genus.title"))) || gui_title.contains("IA_CUST_GUI")) {
            event.setCancelled(true);
            GenusFile genusFile = new GenusFile();
            DungeonPlayer dungeonPlayer = new DungeonPlayer((Player) event.getWhoClicked());
            DungeonGenus dungeonGenus = new DungeonGenus((Player) event.getWhoClicked());
            for(String genus : genusFile.getGenusConfig().getConfigurationSection("dungeon-genus").getKeys(false)) {
                String display = genusFile.getGenusConfig().getString("dungeon-genus." + genus + ".display");
                if (event.getCurrentItem() == null) return;
                if(event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(parseHexColors(display))) {
                    dungeonGenus.setGenus(genus);
                    event.getWhoClicked().closeInventory();
                    dungeonPlayer.messageManager().title(new LanguageFile().getLanguage().getString("messages.genus.selected.title"),new LanguageFile().getLanguage().getString("messages.genus.selected.subtitle").replace("#genus", genus));
                    return;
                }
            }
        }
    }
}
