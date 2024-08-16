package com.magnesify.magnesifydungeons.boss.gui.drops;

import com.magnesify.magnesifydungeons.boss.MagnesifyBoss;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import static com.magnesify.magnesifydungeons.MagnesifyDungeons.get;
import static com.magnesify.magnesifydungeons.dungeon.entitys.DungeonPlayer.parseHexColors;

public class DropsGuiLoader {

    private static Inventory inv = null;

    @Deprecated
    public static void openInventory(Player ent, MagnesifyBoss magnesifyBoss) {
        inv = Bukkit.createInventory(null,  54, parseHexColors(get().getConfig().getString("settings.boss.drops.title")));
        loadItems(magnesifyBoss);
        ent.openInventory(inv);
    }


    @Deprecated
    public static void loadItems(MagnesifyBoss magnesifyBoss) {
        int i = 0;
        for (String a : magnesifyBoss.drops()) {
            String[] split = a.split(":");
            ItemStack item = new ItemStack(Material.getMaterial(split[0]));
            ItemMeta itemMeta = item.getItemMeta();
            item.setAmount(Integer.parseInt(split[1]));
            item.setItemMeta(itemMeta);
            inv.setItem(i, item);
            i++;
        }
    }
}
