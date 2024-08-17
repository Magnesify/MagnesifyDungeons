package com.magnesify.magnesifydungeons.dungeon.types.trigger.gui;

import com.magnesify.magnesifydungeons.modules.managers.DatabaseManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

import static com.magnesify.magnesifydungeons.MagnesifyDungeons.get;
import static com.magnesify.magnesifydungeons.dungeon.entitys.DungeonPlayer.parseHexColors;

public class TriggerTypeLevelBossLoader {

    private static Inventory inv = null;

    @Deprecated
    public static void openInventory(Player ent) {
        inv = Bukkit.createInventory(null,  54, parseHexColors(get().getConfig().getString("settings.trigger-type.title")));
        loadItems(ent);
        ent.openInventory(inv);
    }

    @Deprecated
    public static void loadItems(Player player) {
        DatabaseManager databaseManager = new DatabaseManager(get());
        int i = 0;
        for (String ranks : databaseManager.TriggerTypeDungeons().getAllDungeons()) {
            ItemStack itemStack = new ItemStack(Material.SPAWNER);
            ItemMeta meta = itemStack.getItemMeta();
            meta.setDisplayName(parseHexColors("&a" + ranks));
            List<String> list = new ArrayList<>();
            for(int a = 1; a<=databaseManager.TriggerTypeDungeons().getTotalCheckpoints(ranks);a++) {
                list.add(parseHexColors(String.format(" &8&l* &fLevel: &a%s &7- &fBoss: &e%s",a, databaseManager.TriggerTypeDungeons().getCheckpointBoss(ranks, a))));
            }
            meta.setLore(list);
            itemStack.setItemMeta(meta);
            inv.setItem(i, itemStack);
            i++;
        }
    }

}
