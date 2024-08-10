package com.magnesify.magnesifydungeons.kits;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Iterator;
import java.util.List;

import static com.magnesify.magnesifydungeons.MagnesifyDungeons.get;
import static com.magnesify.magnesifydungeons.dungeon.entitys.DungeonPlayer.parseHexColors;

public class KitGuiLoader {

    private static Inventory inv = null;

    @Deprecated
    public static void openInventory(Player ent) {
        inv = Bukkit.createInventory(null,  54, ChatColor.translateAlternateColorCodes('&', get().getConfig().getString("settings.kits.title")));
        loadItems(ent);
        ent.openInventory(inv);
    }

    @Deprecated
    public static void loadItems(Player player) {
        KitsFile kitsFile = new KitsFile();
        Iterator var0 = kitsFile.getKitsConfig().getConfigurationSection("kits").getKeys(false).iterator();
        int i = 0;
        while (var0.hasNext()) {
            String ranks = (String) var0.next();
            boolean is_material_set = kitsFile.getKitsConfig().isSet("kits." + ranks + ".material");
            boolean is_display_set = kitsFile.getKitsConfig().isSet("kits." + ranks + ".display");
            boolean is_lore_set = kitsFile.getKitsConfig().isSet("kits." + ranks + ".lore");
            ItemStack itemStack;
            if(is_material_set) {
                itemStack = new ItemStack(Material.getMaterial(kitsFile.getKitsConfig().getString("kits." + ranks + ".material")));
            } else {itemStack = new ItemStack(Material.PAPER);}
            ItemMeta meta = itemStack.getItemMeta();
            if(is_lore_set) {
                List<String> deactive_lores = kitsFile.getKitsConfig().getStringList("kits." + ranks + ".lore");
                meta.setLore(deactive_lores);
            }
            if(is_display_set) {meta.setDisplayName(parseHexColors(kitsFile.getKitsConfig().getString("kits." + ranks + ".display")));}
            itemStack.setItemMeta(meta);
            inv.setItem(i, itemStack);
        }

    }

}
