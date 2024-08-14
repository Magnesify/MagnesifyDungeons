package com.magnesify.magnesifydungeons.events;

import com.magnesify.magnesifydungeons.MagnesifyDungeons;
import com.magnesify.magnesifydungeons.dungeon.Dungeon;
import com.magnesify.magnesifydungeons.dungeon.entitys.DungeonPlayer;
import com.magnesify.magnesifydungeons.dungeon.types.challange.Challange;
import com.magnesify.magnesifydungeons.modules.Defaults;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;
import java.util.UUID;

import static com.magnesify.magnesifydungeons.MagnesifyDungeons.get;
import static com.magnesify.magnesifydungeons.commands.Administrator.challange;

public class DungeonCreateEvent implements Listener {

    public static HashMap<UUID, Integer> creationSystemLevel = new HashMap<>();
    public DungeonCreateEvent(MagnesifyDungeons magnesifyDungeons) {}
    public static HashMap<String, String> data = new HashMap<>();

    public boolean isNumeric(String str) {
        if (str == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    @EventHandler
    public void onDungeonCreateEvent(AsyncPlayerChatEvent event) {
        if(creationSystemLevel.get(event.getPlayer().getUniqueId()) != null) {

            if (challange.get(event.getPlayer().getUniqueId()) != null) {
                DungeonPlayer dungeonPlayer = new DungeonPlayer(event.getPlayer());
                int level = creationSystemLevel.get(event.getPlayer().getUniqueId());
                switch (level) {
                    case 1:
                        data.put("Name", event.getMessage());
                        data.put("lvl", "*");
                        event.setCancelled(true);
                        dungeonPlayer.messageManager().chat(get().getConfig().getString("settings.messages.dungeon.creation").replace("#done", Defaults.name).replace("#next", Defaults.boss_id));
                        creationSystemLevel.put(event.getPlayer().getUniqueId(), level + 1);
                        data.put("Category", "Challange");
                        break;
                    case 2:
                        data.put("BossID", event.getMessage());
                        event.setCancelled(true);
                        dungeonPlayer.messageManager().chat(get().getConfig().getString("settings.messages.dungeon.creation").replace("#done", Defaults.boss_id).replace("#next", Defaults.playtime));
                        creationSystemLevel.put(event.getPlayer().getUniqueId(), level + 1);
                        break;
                    case 3:
                        if (isNumeric(event.getMessage())) {
                            data.put("PT", event.getMessage());
                            event.setCancelled(true);
                            dungeonPlayer.messageManager().chat(get().getConfig().getString("settings.messages.dungeon.creation").replace("#done", Defaults.playtime).replace("#next", Defaults.starttime));
                            creationSystemLevel.put(event.getPlayer().getUniqueId(), level + 1);
                        } else {
                            dungeonPlayer.messageManager().chat(get().getConfig().getString("settings.messages.dungeon.canceled.must-be-number"));
                            event.setCancelled(true);
                        }
                        break;
                    case 4:
                        if (isNumeric(event.getMessage())) {
                            data.put("ST", event.getMessage());
                            event.setCancelled(true);
                            creationSystemLevel.remove(event.getPlayer().getUniqueId());
                            Challange dungeon = new Challange(data.get("Name"), "Challange", 1, data.get("BossID"), Integer.parseInt(data.get("ST")), Integer.parseInt(data.get("PT")), event.getPlayer().getLocation());
                            if (dungeon.create()) {
                                dungeonPlayer.messageManager().chat(get().getConfig().getString("settings.messages.dungeon.created").replace("#category", "Challange").replace("#name", data.get("Name")));
                            } else {
                                dungeonPlayer.messageManager().chat(get().getConfig().getString("settings.messages.error.cannot-created").replace("#category", "Challange").replace("#name", data.get("Name")));
                            }
                            data.clear();
                            challange.clear();
                            creationSystemLevel.clear();
                        } else {
                            dungeonPlayer.messageManager().chat(get().getConfig().getString("settings.messages.dungeon.canceled.must-be-number"));
                            event.setCancelled(true);
                        }
                        break;
                }
            } else {

                DungeonPlayer dungeonPlayer = new DungeonPlayer(event.getPlayer());
                int level = creationSystemLevel.get(event.getPlayer().getUniqueId());
                switch (level) {
                    case 1:
                        data.put("Name", event.getMessage());
                        event.setCancelled(true);
                        dungeonPlayer.messageManager().chat(get().getConfig().getString("settings.messages.dungeon.creation").replace("#done", Defaults.name).replace("#next", Defaults.category));
                        creationSystemLevel.put(event.getPlayer().getUniqueId(), level + 1);
                        break;
                    case 2:
                        data.put("Category", event.getMessage());
                        event.setCancelled(true);
                        dungeonPlayer.messageManager().chat(get().getConfig().getString("settings.messages.dungeon.creation").replace("#done", Defaults.category).replace("#next", Defaults.boss_id));
                        creationSystemLevel.put(event.getPlayer().getUniqueId(), level + 1);
                        break;
                    case 3:
                        data.put("BossID", event.getMessage());
                        event.setCancelled(true);
                        dungeonPlayer.messageManager().chat(get().getConfig().getString("settings.messages.dungeon.creation").replace("#done", Defaults.boss_id).replace("#next", Defaults.playtime));
                        creationSystemLevel.put(event.getPlayer().getUniqueId(), level + 1);
                        break;
                    case 4:
                        if (isNumeric(event.getMessage())) {
                            data.put("PT", event.getMessage());
                            event.setCancelled(true);
                            dungeonPlayer.messageManager().chat(get().getConfig().getString("settings.messages.dungeon.creation").replace("#done", Defaults.playtime).replace("#next", Defaults.starttime));
                            creationSystemLevel.put(event.getPlayer().getUniqueId(), level + 1);
                        } else {
                            dungeonPlayer.messageManager().chat(get().getConfig().getString("settings.messages.dungeon.canceled.must-be-number"));
                            event.setCancelled(true);
                        }
                        break;
                    case 5:
                        if (isNumeric(event.getMessage())) {
                            data.put("ST", event.getMessage());
                            event.setCancelled(true);
                            dungeonPlayer.messageManager().chat(get().getConfig().getString("settings.messages.dungeon.creation").replace("#done", Defaults.starttime).replace("#next", Defaults.level));
                            creationSystemLevel.put(event.getPlayer().getUniqueId(), level + 1);
                        } else {
                            dungeonPlayer.messageManager().chat(get().getConfig().getString("settings.messages.dungeon.canceled.must-be-number"));
                            event.setCancelled(true);
                        }
                        break;
                    case 6:
                        if (isNumeric(event.getMessage())) {
                            data.put("lvl", event.getMessage());
                            event.setCancelled(true);
                            creationSystemLevel.remove(event.getPlayer().getUniqueId());
                            Dungeon dungeon = new Dungeon(data.get("Name"), data.get("Category"), Integer.parseInt(data.get("lvl")), data.get("BossID"), Integer.parseInt(data.get("ST")), Integer.parseInt(data.get("PT")), event.getPlayer().getLocation());
                            if (dungeon.create()) {
                                dungeonPlayer.messageManager().chat(get().getConfig().getString("settings.messages.dungeon.created").replace("#category", data.get("Category")).replace("#name", data.get("Name")));
                            } else {
                                dungeonPlayer.messageManager().chat(get().getConfig().getString("settings.messages.error.cannot-created").replace("#category", data.get("Category")).replace("#name", data.get("Name")));
                            }
                            data.clear();
                        } else {
                            dungeonPlayer.messageManager().chat(get().getConfig().getString("settings.messages.dungeon.canceled.must-be-number"));
                            event.setCancelled(true);
                        }
                        break;
                }
            }
        }
    }
}
