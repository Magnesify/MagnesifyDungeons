package com.magnesify.magnesifydungeons.dungeon.types.trigger.gui.boss.type;

import com.magnesify.magnesifydungeons.modules.managers.DatabaseManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import static com.magnesify.magnesifydungeons.MagnesifyDungeons.get;
import static com.magnesify.magnesifydungeons.dungeon.entitys.DungeonPlayer.parseHexColors;

public class BossTypeGuiLoader {

    private static Inventory inv = null;

    @Deprecated
    public static void openInventory(Player ent) {
        inv = Bukkit.createInventory(null,  54, parseHexColors(get().getConfig().getString("settings.trigger-type.types.title")));
        loadItems();
        ent.openInventory(inv);
    }

    @Deprecated
    public static void loadItems() {
        int i = 0;
        for (String a : new DatabaseManager(get()).boss().getAllBoss()) {
            ItemStack item = new ItemStack(Material.PAPER);
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setDisplayName(parseHexColors("&b" + a));
            item.setItemMeta(itemMeta);
            inv.setItem(i, item);
            i++;
        }
    }
}
