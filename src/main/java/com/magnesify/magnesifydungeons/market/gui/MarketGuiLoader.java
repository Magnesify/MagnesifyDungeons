package com.magnesify.magnesifydungeons.market.gui;

import com.magnesify.magnesifydungeons.market.file.MarketFile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
import static com.magnesify.magnesifydungeons.market.format.PriceFormat.format;

public class MarketGuiLoader {

    private static Inventory inv = null;

    @Deprecated
    public static void openInventory(Player ent) {
        inv = Bukkit.createInventory(null,  54, ChatColor.translateAlternateColorCodes('&', get().getConfig().getString("settings.market.title")));
        loadItems(ent);
        ent.openInventory(inv);
    }

    @Deprecated
    public static void loadItems(Player player) {
        MarketFile marketFile = new MarketFile();
        Iterator var0 = marketFile.getMarketConfig().getConfigurationSection("market").getKeys(false).iterator();
        while (var0.hasNext()) {
            String ranks = (String) var0.next();
            boolean is_material_set = marketFile.getMarketConfig().isSet("market." + ranks + ".material");
            boolean is_display_set = marketFile.getMarketConfig().isSet("market." + ranks + ".display");
            boolean is_lore_set = marketFile.getMarketConfig().isSet("market." + ranks + ".lore");
            ItemStack itemStack;
            if(is_material_set) {
                itemStack = new ItemStack(Material.getMaterial(marketFile.getMarketConfig().getString("market." + ranks + ".material")));
            } else {itemStack = new ItemStack(Material.PAPER);}
            ItemMeta meta = itemStack.getItemMeta();
            if(is_lore_set) {
                List<String> main_lores = marketFile.getMarketConfig().getStringList("market." + ranks + ".lore");
                List<String> sub_lore = new ArrayList<>();
                for(int a = 0; a<main_lores.size();a++) {
                    sub_lore.add(parseHexColors(main_lores.get(a).replace("#price", format(ranks,marketFile.getMarketConfig().getDouble("market." + ranks + ".price.value") ))));
                }
                meta.setLore(sub_lore);
            }
            if(is_display_set) {meta.setDisplayName(parseHexColors(marketFile.getMarketConfig().getString("market." + ranks + ".display")));}
            itemStack.setItemMeta(meta);
            inv.setItem(marketFile.getMarketConfig().getInt("market." + ranks + ".slot"), itemStack);
        }

    }

}
