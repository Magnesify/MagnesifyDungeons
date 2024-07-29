package com.magnesify.magnesifydungeons.hanapi;

import com.magnesify.magnesifydungeons.MagnesifyDungeons;
import com.magnesify.magnesifydungeons.dungeon.Dungeon;
import com.magnesify.magnesifydungeons.dungeon.entitys.DungeonPlayer;
import com.magnesify.magnesifydungeons.files.Dungeons;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import static com.magnesify.magnesifydungeons.MagnesifyDungeons.get;
import static com.magnesify.magnesifydungeons.dungeon.Dungeon.location;
import static com.magnesify.magnesifydungeons.dungeon.entitys.DungeonPlayer.parseHexColors;

public class GuiEvents implements Listener {

    public GuiEvents(MagnesifyDungeons magnesifyDungeons) {}

    @EventHandler
    public void CategoryGUI(InventoryClickEvent event) {
        Dungeons dungeons = new Dungeons();
        Player player = (Player) event.getWhoClicked();
        if ( dungeons.get().getConfigurationSection("dungeons") == null) return;
        if(event.getView().getTitle().equalsIgnoreCase(parseHexColors(get().getConfig().getString("settings.menu.title")))) {
            event.setCancelled(true);
            for (String a : dungeons.get().getConfigurationSection("dungeons").getKeys(false)) {
                String category = dungeons.get().getString("dungeons." + a + ".category");
                if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(parseHexColors("&bKategori: <#4f91fc>" + category))) {
                    Inventory inv = Bukkit.createInventory(null,  54, parseHexColors("&8Kategori: <#4f91fc>" + category));
                    GuiManager guiManager = new GuiManager(inv);
                    guiManager.openDungeons(player, category);
                    break;
                }
            }
        }

    }

    @EventHandler
    public void DungeonGUI(InventoryClickEvent event) {
        Dungeons dungeons = new Dungeons();
        Player player = (Player) event.getWhoClicked();
        if ( dungeons.get().getConfigurationSection("dungeons") == null) return;
        for (String a : dungeons.get().getConfigurationSection("dungeons").getKeys(false)) {
            String category = dungeons.get().getString("dungeons." + a + ".category");
            if (event.getView().getTitle().equalsIgnoreCase(parseHexColors("&8Kategori: <#4f91fc>" + category))) {
                event.setCancelled(true);
                String name = dungeons.get().getString("dungeons." + a + ".name");
                if (event.getCurrentItem() == null) return;
                if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(parseHexColors("&bZindan: <#4f91fc>" + name))) {
                    Dungeon dungeon = new Dungeon(name);
                    DungeonPlayer dungeonPlayer = new DungeonPlayer(player);
                    if (dungeon.exists()) {
                        if (!dungeonPlayer.inDungeon()) {
                            if (dungeon.parameters().status()) {
                                dungeonPlayer.join(dungeon);
                                dungeon.updateCurrentPlayer(player.getName());
                                dungeon.events().wait(player, dungeon);
                                dungeon.status(false);
                                for (String messages : get().getConfig().getStringList("settings.messages.events.joined")) {
                                    dungeonPlayer.messageManager().chat(messages.replace("#next_level", String.valueOf(dungeon.parameters().next())).replace("#category", dungeon.parameters().category()).replace("#playtime", String.valueOf(dungeon.parameters().play())));
                                }
                                break;
                            } else {
                                dungeonPlayer.messageManager().chat(get().getConfig().getString("settings.messages.dungeon.full").replace("#countdown", String.valueOf(dungeon.countdown())).replace("#name", name));
                            }
                        } else {
                            dungeonPlayer.messageManager().chat(get().getConfig().getString("settings.messages.dungeon.already-in-dungeon").replace("#name", name));
                        }
                    } else {
                        dungeonPlayer.messageManager().chat(get().getConfig().getString("settings.messages.dungeon.unknow-dungeon").replace("#name", name));
                    }
                }
            }
        }
    }

}
