package com.magnesify.magnesifydungeons.genus.gui;

import com.magnesify.magnesifydungeons.files.GenusFile;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.magnesify.magnesifydungeons.MagnesifyDungeons.get;
import static com.magnesify.magnesifydungeons.dungeon.entitys.DungeonPlayer.parseHexColors;
import static dev.lone.itemsadder.api.ItemsAdder.getCustomItem;

public class GenusGuiLoader {

    private static Inventory inv = null;

    @Deprecated
    public static void openInventory(Player ent) {
        inv = Bukkit.createInventory(null,  54, parseHexColors(get().getConfig().getString("settings.genus.title")));
        loadItems(ent);
        ent.openInventory(inv);
    }

    @Deprecated
    public static void loadItems(Player player) {
        GenusFile genusFile = new GenusFile();
        Iterator var0 = genusFile.getGenusConfig().getConfigurationSection("dungeon-genus").getKeys(false).iterator();
        while (var0.hasNext()) {
            String ranks = (String) var0.next();
            boolean is_display_set = genusFile.getGenusConfig().isSet("dungeon-genus." + ranks + ".display");
            boolean is_lore_set = genusFile.getGenusConfig().isSet("dungeon-genus." + ranks + ".lore");
            boolean is_custom_material_set = get().getConfig().isSet("dungeon-genus." + ranks + ".custom-material");
            boolean is_material_set = genusFile.getGenusConfig().isSet("dungeon-genus." + ranks + ".material");
            ItemStack itemStack;
            if(is_material_set) {
                itemStack = new ItemStack(Material.getMaterial(genusFile.getGenusConfig().getString("dungeon-genus." + ranks + ".material")));
            } else if(is_custom_material_set) {
                itemStack = getCustomItem(genusFile.getGenusConfig().getString("dungeon-genus." + ranks + ".custom-material"));
            } else {
                itemStack = new ItemStack(Material.PAPER);
            }
            ItemMeta meta = itemStack.getItemMeta();
            if(is_lore_set) {
                List<String> main_lores = genusFile.getGenusConfig().getStringList("dungeon-genus." + ranks + ".lore");
                List<String> sub_lore = new ArrayList<>();
                for(int a = 0; a<main_lores.size();a++) {
                    sub_lore.add(parseHexColors(main_lores.get(a)));
                }
                meta.setLore(sub_lore);
            }
            if(is_display_set) {meta.setDisplayName(parseHexColors(genusFile.getGenusConfig().getString("dungeon-genus." + ranks + ".display")));}
            itemStack.setItemMeta(meta);
            inv.setItem(genusFile.getGenusConfig().getInt("dungeon-genus." + ranks + ".slot"), itemStack);
        }

    }

}
