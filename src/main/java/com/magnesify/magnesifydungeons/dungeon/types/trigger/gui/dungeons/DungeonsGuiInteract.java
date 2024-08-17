package com.magnesify.magnesifydungeons.dungeon.types.trigger.gui.dungeons;

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

public class DungeonsGuiInteract implements Listener {
    public DungeonsGuiInteract(MagnesifyDungeons magnesifyDungeons) {}

    @Deprecated
    @EventHandler
    public void onInteract(InventoryClickEvent event) {
        String gui_title = event.getView().getTitle();
        if(gui_title.equalsIgnoreCase(parseHexColors(get().getConfig().getString("settings.trigger-type.dungeons.title"))) || gui_title.contains("IA_CUST_GUI")) {
            event.setCancelled(true);
            DatabaseManager databaseManager = new DatabaseManager(get());
            DungeonPlayer dungeonPlayer = new DungeonPlayer((Player) event.getWhoClicked());
            for(String ranks : databaseManager.TriggerTypeDungeons().getAllDungeons()) {
                if(event.getCurrentItem() == null) return;
                if(event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(parseHexColors(get().getConfig().getString("settings.challange.defaults.display").replace("#dungeon", ranks.replace("challange_", ""))))) {
                    TriggerType triggerType = new TriggerType((Player) event.getWhoClicked());
                    if(!dungeonPlayer.inDungeon()) {
                        if(triggerType.parameters(ranks).status()) {
                            MagnesifyBoss magnesifyBoss = new MagnesifyBoss(databaseManager.TriggerTypeDungeons().getCheckpointBoss(ranks, 1));
                            if(magnesifyBoss.exists()) {
                                triggerType.join(ranks);
                                triggerType.updateCurrentPlayer(ranks, ((Player) event.getWhoClicked()).getName());
                                triggerType.status(ranks, false);
                                for(String messages : get().getConfig().getStringList("settings.messages.events.joined")) {
                                    dungeonPlayer.messageManager().chat(messages.replace("#boss_name", magnesifyBoss.name()).replace("#boss_health", String.valueOf(magnesifyBoss.health())).replace("#next_level", String.valueOf(triggerType.parameters(ranks).next())).replace("#category", triggerType.parameters(ranks).category()).replace("#playtime", String.valueOf(triggerType.parameters(ranks).play())));
                                }
                                event.getWhoClicked().closeInventory();
                                return;
                            } else {
                                dungeonPlayer.messageManager().chat(new LanguageFile().getLanguage(MagnesifyDungeons.locale).getString("messages.dungeon.boss-not-exists").replace("#name", ranks));
                            }
                        } else {
                            dungeonPlayer.messageManager().chat(new LanguageFile().getLanguage(MagnesifyDungeons.locale).getString("messages.dungeon.full").replace("#countdown", "").replace("#name", ranks));
                        }
                    } else {
                        dungeonPlayer.messageManager().chat(new LanguageFile().getLanguage(MagnesifyDungeons.locale).getString("messages.dungeon.already-in-dungeon").replace("#name", ranks));
                    }
                }
            }
        }
    }

}
