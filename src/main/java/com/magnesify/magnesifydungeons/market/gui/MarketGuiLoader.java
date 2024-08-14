package com.magnesify.magnesifydungeons.market.gui;

import com.magnesify.magnesifydungeons.market.file.MarketFile;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

import static com.magnesify.magnesifydungeons.MagnesifyDungeons.get;
import static com.magnesify.magnesifydungeons.dungeon.entitys.DungeonPlayer.parseHexColors;
import static com.magnesify.magnesifydungeons.market.format.PriceFormat.format;
import static dev.lone.itemsadder.api.ItemsAdder.getCustomItem;

public class MarketGuiLoader {

    private static Inventory inv = null;
    public static HashMap<UUID, Integer> current_page = new HashMap<>();

    @Deprecated
    public static void openInventory(Player ent, int page) {
        inv = Bukkit.createInventory(null,  54, parseHexColors(get().getConfig().getString("settings.market.title")));
        current_page.remove(ent.getUniqueId());
        current_page.put(ent.getUniqueId(), page);
        loadItems(ent);
        ent.openInventory(inv);
    }

    @Deprecated
    public static void next() {
        MarketFile marketFile = new MarketFile();
        boolean is_material_set = marketFile.getMarketConfig().isSet("market.next.material");
        boolean is_custom_material_set = marketFile.getMarketConfig().isSet("market.next.custom-material");
        boolean is_display_set = marketFile.getMarketConfig().isSet("market.next.display");
        boolean is_lore_set = marketFile.getMarketConfig().isSet("market.next.lore");
        ItemStack itemStack;
        if(is_material_set) {
            itemStack = new ItemStack(Material.getMaterial(marketFile.getMarketConfig().getString("market.next.material")));
        } else {
            itemStack = new ItemStack(Material.PAPER);
        }
        if(is_custom_material_set) {
            itemStack = getCustomItem(marketFile.getMarketConfig().getString("market.next.custom-material"));
        }
        ItemMeta meta = itemStack.getItemMeta();
        if(is_lore_set) {
            List<String> main_lores = marketFile.getMarketConfig().getStringList("market.next.lore");
            List<String> sub_lore = new ArrayList<>();
            for(int a = 0; a<main_lores.size();a++) {
                sub_lore.add(parseHexColors(main_lores.get(a)));
            }
            meta.setLore(sub_lore);
        }
        if(is_display_set) {meta.setDisplayName(parseHexColors(marketFile.getMarketConfig().getString("market.next.display")));}
        itemStack.setItemMeta(meta);
        inv.setItem(marketFile.getMarketConfig().getInt("market.next.slot"), itemStack);
    }

    @Deprecated
    public static void prev() {
        MarketFile marketFile = new MarketFile();
        boolean is_material_set = marketFile.getMarketConfig().isSet("market.prev.material");
        boolean is_custom_material_set = marketFile.getMarketConfig().isSet("market.prev.custom-material");
        boolean is_display_set = marketFile.getMarketConfig().isSet("market.prev.display");
        boolean is_lore_set = marketFile.getMarketConfig().isSet("market.prev.lore");
        ItemStack itemStack;
        if(is_material_set) {
            itemStack = new ItemStack(Material.getMaterial(marketFile.getMarketConfig().getString("market.prev.material")));
        } else {
            itemStack = new ItemStack(Material.PAPER);
        }
        if(is_custom_material_set) {
            itemStack = getCustomItem(marketFile.getMarketConfig().getString("market.prev.custom-material"));
        }
        ItemMeta meta = itemStack.getItemMeta();
        if(is_lore_set) {
            List<String> main_lores = marketFile.getMarketConfig().getStringList("market.prev.lore");
            List<String> sub_lore = new ArrayList<>();
            for(int a = 0; a<main_lores.size();a++) {
                sub_lore.add(parseHexColors(main_lores.get(a)));
            }
            meta.setLore(sub_lore);
        }
        if(is_display_set) {meta.setDisplayName(parseHexColors(marketFile.getMarketConfig().getString("market.prev.display")));}
        itemStack.setItemMeta(meta);
        inv.setItem(marketFile.getMarketConfig().getInt("market.prev.slot"), itemStack);
    }

    @Deprecated
    public static void loadItems(Player player) {
        MarketFile marketFile = new MarketFile();
        Iterator var0 = marketFile.getMarketConfig().getConfigurationSection("market."+current_page.get(player.getUniqueId())+".products").getKeys(false).iterator();
        while (var0.hasNext()) {
            String ranks = (String) var0.next();
            boolean is_material_set = marketFile.getMarketConfig().isSet("market."+current_page.get(player.getUniqueId())+".products." + ranks + ".material");
            boolean is_display_set = marketFile.getMarketConfig().isSet("market."+current_page.get(player.getUniqueId())+".products." + ranks + ".display");
            boolean is_lore_set = marketFile.getMarketConfig().isSet("market."+current_page.get(player.getUniqueId())+".products." + ranks + ".lore");
            ItemStack itemStack;
            if(is_material_set) {
                itemStack = new ItemStack(Material.getMaterial(marketFile.getMarketConfig().getString("market."+current_page.get(player.getUniqueId())+".products." + ranks + ".material")));
            } else {itemStack = new ItemStack(Material.PAPER);}
            ItemMeta meta = itemStack.getItemMeta();
            if(is_lore_set) {
                List<String> main_lores = marketFile.getMarketConfig().getStringList("market."+current_page.get(player.getUniqueId())+"products." + ranks + ".lore");
                List<String> sub_lore = new ArrayList<>();
                for(int a = 0; a<main_lores.size();a++) {
                    sub_lore.add(parseHexColors(main_lores.get(a).replace("#price", format(ranks,marketFile.getMarketConfig().getDouble("market."+current_page.get(player.getUniqueId())+".products." + ranks + ".price.value"),player))));
                }
                meta.setLore(sub_lore);
            }
            if(is_display_set) {meta.setDisplayName(parseHexColors(marketFile.getMarketConfig().getString("market."+current_page.get(player.getUniqueId())+".products." + ranks + ".display")));}
            itemStack.setItemMeta(meta);
            if(current_page.get(player.getUniqueId()) != marketFile.getMarketConfig().getConfigurationSection("market").getKeys(false).size()-2) {
                next();
            }
            if(current_page.get(player.getUniqueId()) > 1) {
                prev();
            }
            inv.setItem(marketFile.getMarketConfig().getInt("market."+current_page.get(player.getUniqueId())+".products." + ranks + ".slot"), itemStack);
        }

    }

}
