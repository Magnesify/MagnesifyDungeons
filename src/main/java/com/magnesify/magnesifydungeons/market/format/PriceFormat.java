package com.magnesify.magnesifydungeons.market.format;

import com.magnesify.magnesifydungeons.market.file.MarketFile;
import org.bukkit.entity.Player;

import static com.magnesify.magnesifydungeons.market.gui.MarketGuiLoader.current_page;

public class PriceFormat {

    public static String getProductType(String product, Player player) {
        MarketFile marketFile = new MarketFile();
        return marketFile.getMarketConfig().getString("market." + current_page.get(player.getUniqueId()) + ".products." + product + ".price.type");
    }

    public static String format(String a, double price, Player player) {
        MarketFile marketFile = new MarketFile();

        String vault_prefix = marketFile.getMarketConfig().getString("format.vault.prefix");
        String vault_suffix = marketFile.getMarketConfig().getString("format.vault.suffix");


        String point_prefix = marketFile.getMarketConfig().getString("format.dungeon_point.prefix");
        String point_suffix = marketFile.getMarketConfig().getString("format.dungeon_point.suffix");

        if(getProductType(a, player).equalsIgnoreCase("vault")) {
            return String.format("%s %s %s", vault_suffix, price, vault_prefix);
        } else if(getProductType(a, player).equalsIgnoreCase("dungeon_point")) {
            return String.format("%s %s %s", point_suffix, price, point_prefix);
        } else {
            return String.format("%s %s %s", point_suffix, price, point_prefix);
        }
    }

}
