package com.magnesify.magnesifydungeons.commands.player.profile;

import com.magnesify.magnesifydungeons.modules.managers.DatabaseManager;
import com.magnesify.magnesifydungeons.modules.managers.StatsManager;
import com.magnesify.magnesifydungeons.storage.PlayerMethods;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static com.magnesify.magnesifydungeons.MagnesifyDungeons.get;
import static com.magnesify.magnesifydungeons.dungeon.entitys.DungeonPlayer.parseHexColors;

public class ProfileGuiLoader {

    private static Inventory inv = null;

    @Deprecated
    public static void openInventory(Player ent) {
        inv = Bukkit.createInventory(null,  54, parseHexColors(get().getConfig().getString("settings.profile.title")));
        loadItems(ent);
        ent.openInventory(inv);
    }

    public static String rank(String name) {
        DatabaseManager databaseManager = new DatabaseManager(get());
        List<DatabaseManager.Stats.Sort.Player> sortedPlayers = databaseManager.stats().sort().getSortedPlayersByKill();
        AtomicReference<String> data = null;
        sortedPlayers.stream().filter(player1 -> {
            for (int i = 0; i < Math.min(3, sortedPlayers.size()); i++) {
                if (player1.getName().equalsIgnoreCase(name)) {
                    data.set("#" + (i + 1));
                }
            }
            return false;
        });
        return String.valueOf(data);
    }

    public static String stats() {
        DatabaseManager databaseManager = new DatabaseManager(get());
        List<DatabaseManager.Stats.Sort.Player> sortedPlayers = databaseManager.stats().sort().getSortedPlayersByKill();
        StringBuilder data = new StringBuilder();
        for (int i = 0; i < Math.min(3, sortedPlayers.size()); i++) {
            DatabaseManager.Stats.Sort.Player player = sortedPlayers.get(i);
            data.append("#").append(i + 1).append(" ").append(player.getName());
            data.append("\n");
        }
        return data.toString();
    }

    @Deprecated
    public static void loadItems(Player player) {
        Iterator var0 = get().getConfig().getConfigurationSection("settings.profile.items").getKeys(false).iterator();
        while (var0.hasNext()) {
            String ranks = (String) var0.next();
            boolean is_material_set = get().getConfig().isSet("settings.profile.items." + ranks + ".material");
            boolean is_display_set = get().getConfig().isSet("settings.profile.items." + ranks + ".display");
            boolean is_lore_set = get().getConfig().isSet("settings.profile.items." + ranks + ".lore");
            ItemStack itemStack;
            if(is_material_set) {
                itemStack = new ItemStack(Material.getMaterial(get().getConfig().getString("settings.profile.items." + ranks + ".material")));
            } else {itemStack = new ItemStack(Material.PAPER);}
            ItemMeta meta = itemStack.getItemMeta();
            if(is_lore_set) {
                List<String> main_lores = get().getConfig().getStringList("settings.profile.items." + ranks + ".lore");
                List<String> sub_lore = new ArrayList<>();
                PlayerMethods playerMethods = new PlayerMethods();
                StatsManager statsManager = new StatsManager();
                for(int a = 0; a<main_lores.size();a++) {
                    sub_lore.add(parseHexColors(main_lores.get(a)
                            .replace("#kill", String.valueOf(statsManager.getKill(player.getUniqueId().toString())))
                            .replace("#ranks", stats())
                            .replace("#point", String.valueOf(playerMethods.getPoints(player)))
                            .replace("#last_dungeon", String.valueOf(playerMethods.getLastDungeon(player)))
                            .replace("#death", String.valueOf(statsManager.getDeath(player.getUniqueId().toString())))
                                    .replace("#total", String.valueOf(statsManager.getMatches(player.getUniqueId().toString())))
                                    .replace("#win", String.valueOf(statsManager.getWin(player.getUniqueId().toString())))
                                    .replace("#lose", String.valueOf(statsManager.getLose(player.getUniqueId().toString())))

                            ));
                }
                meta.setLore(sub_lore);
            }
            if(is_display_set) {meta.setDisplayName(parseHexColors(get().getConfig().getString("settings.profile.items." + ranks + ".display")));}
            itemStack.setItemMeta(meta);
            inv.setItem(get().getConfig().getInt("settings.profile.items." + ranks + ".slot"), itemStack);
        }

    }

}
