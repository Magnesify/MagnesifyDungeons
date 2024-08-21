package com.magnesify.magnesifydungeons.dungeon.types.trigger.commands.gui;

import com.magnesify.magnesifydungeons.MagnesifyDungeons;
import com.magnesify.magnesifydungeons.boss.MagnesifyBoss;
import com.magnesify.magnesifydungeons.dungeon.TriggerType;
import com.magnesify.magnesifydungeons.dungeon.entitys.DungeonPlayer;
import com.magnesify.magnesifydungeons.languages.LanguageFile;
import com.magnesify.magnesifydungeons.modules.managers.DatabaseManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import static com.magnesify.magnesifydungeons.MagnesifyDungeons.get;
import static com.magnesify.magnesifydungeons.dungeon.entitys.DungeonPlayer.parseHexColors;
import static com.magnesify.magnesifydungeons.modules.Defaults.TEXT_PREFIX;

public class TriggerGuiInteract implements Listener {
    public TriggerGuiInteract(MagnesifyDungeons magnesifyDungeons) {}

    @Deprecated
    @EventHandler
    public void onInteract(InventoryClickEvent event) {
        String gui_title = event.getView().getTitle();
        if(gui_title.equalsIgnoreCase(parseHexColors(get().getConfig().getString("settings.trigger-type.dungeon-list.title"))) || gui_title.contains("IA_CUST_GUI")) {
            event.setCancelled(true);
            DatabaseManager databaseManager = new DatabaseManager(get());
            DungeonPlayer dungeonPlayer = new DungeonPlayer((Player) event.getWhoClicked());
            for(String ranks : databaseManager.getChallangeNames()) {
                if(event.getCurrentItem() == null) return;
                if(event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(parseHexColors(get().getConfig().getString("settings.trigger-type.dungeon-list.defaults.display").replace("#dungeon", ranks.replace("challange_", ""))))) {
                    TriggerType triggerType = new TriggerType((Player) event.getWhoClicked());
                    if(!dungeonPlayer.inDungeon()) {
                        if (databaseManager.TriggerTypeDungeons().isDungeonExists(ranks)) {
                            if (databaseManager.TriggerTypeDungeons().getAvailable(ranks)) {
                                MagnesifyBoss magnesifyBoss = new MagnesifyBoss(triggerType.parameters(ranks).boss());
                                if (magnesifyBoss.exists()) {
                                    Player player = ((Player) event.getWhoClicked()).getPlayer();
                                    databaseManager.TriggerTypeDungeons().setAvailable(ranks, false);
                                    triggerType.join(ranks);
                                    dungeonPlayer.messageManager().chat(TEXT_PREFIX + " &fZindana giriş yaptın !");
                                } else {
                                    dungeonPlayer.messageManager().chat(new LanguageFile().getLanguage().getString("messages.dungeon.boss-not-exists").replace("#name", ranks));
                                }
                            } else {
                                dungeonPlayer.messageManager().chat(new LanguageFile().getLanguage().getString("messages.dungeon.full").replace("#name", ranks).replace("#countdown", ""));
                            }
                        } else {
                            dungeonPlayer.messageManager().chat(new LanguageFile().getLanguage().getString("messages.dungeon.unknow-dungeon").replace("#name", ranks));
                        }
                    } else {
                        dungeonPlayer.messageManager().chat(new LanguageFile().getLanguage().getString("messages.dungeon.already-in-dungeon").replace("#name", ranks));
                    }
                }
            }
        }
    }

}
