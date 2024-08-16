package com.magnesify.magnesifydungeons.dungeon.types.challange.gui;

import com.magnesify.magnesifydungeons.MagnesifyDungeons;
import com.magnesify.magnesifydungeons.boss.MagnesifyBoss;
import com.magnesify.magnesifydungeons.dungeon.entitys.DungeonPlayer;
import com.magnesify.magnesifydungeons.dungeon.types.challange.Challange;
import com.magnesify.magnesifydungeons.languages.LanguageFile;
import com.magnesify.magnesifydungeons.modules.managers.DatabaseManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import static com.magnesify.magnesifydungeons.MagnesifyDungeons.get;
import static com.magnesify.magnesifydungeons.dungeon.entitys.DungeonPlayer.parseHexColors;

public class ChallangeGuiInteract implements Listener {
    public ChallangeGuiInteract(MagnesifyDungeons magnesifyDungeons) {}

    @Deprecated
    @EventHandler
    public void onInteract(InventoryClickEvent event) {
        String gui_title = event.getView().getTitle();
        if(gui_title.equalsIgnoreCase(parseHexColors(get().getConfig().getString("settings.challange.title"))) || gui_title.contains("IA_CUST_GUI")) {
            event.setCancelled(true);
            DatabaseManager databaseManager = new DatabaseManager(get());
            DungeonPlayer dungeonPlayer = new DungeonPlayer((Player) event.getWhoClicked());
            for(String ranks : databaseManager.getChallangeNames()) {
                if(event.getCurrentItem() == null) return;
                if(event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(parseHexColors(get().getConfig().getString("settings.challange.defaults.display").replace("#dungeon", ranks.replace("challange_", ""))))) {
                    Challange dungeon = new Challange(ranks.replace("challange_", ""));
                    if(!dungeonPlayer.inDungeon()) {
                        if(dungeon.exists()) {
                            if(dungeon.parameters().status()) {
                                MagnesifyBoss magnesifyBoss = new MagnesifyBoss(dungeon.parameters().boss());
                                if(magnesifyBoss.exists()) {
                                    dungeonPlayer.challange(dungeon);
                                    dungeon.events().wait((Player) event.getWhoClicked(), dungeon);
                                    dungeon.updateCurrentPlayer(((Player) event.getWhoClicked()).getName());
                                    dungeon.status(false);
                                    for(String messages : get().getConfig().getStringList("settings.messages.events.joined")) {
                                        dungeonPlayer.messageManager().chat(messages.replace("#boss_name", magnesifyBoss.name()).replace("#boss_health", String.valueOf(magnesifyBoss.health())).replace("#next_level", String.valueOf(dungeon.parameters().next())).replace("#category", dungeon.parameters().category()).replace("#playtime", String.valueOf(dungeon.parameters().play())));
                                    }
                                    return;
                                } else {
                                    dungeonPlayer.messageManager().chat(new LanguageFile().getLanguage(MagnesifyDungeons.locale).getString("messages.dungeon.boss-not-exists").replace("#name", ranks));
                                }
                            } else {
                                dungeonPlayer.messageManager().chat(new LanguageFile().getLanguage(MagnesifyDungeons.locale).getString("messages.dungeon.full").replace("#countdown", String.valueOf(dungeon.countdown())).replace("#name", ranks));
                            }
                        } else {
                            dungeonPlayer.messageManager().chat(new LanguageFile().getLanguage(MagnesifyDungeons.locale).getString("messages.dungeon.unknow-dungeon").replace("#name", ranks));
                        }
                    } else {
                        dungeonPlayer.messageManager().chat(new LanguageFile().getLanguage(MagnesifyDungeons.locale).getString("messages.dungeon.already-in-dungeon").replace("#name", ranks));
                    }
                }
            }
        }
    }

}
