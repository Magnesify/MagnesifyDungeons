package com.magnesify.magnesifydungeons.market.format;

import com.magnesify.magnesifydungeons.market.file.MarketFile;

public class PriceFormat {

    public static String getProductType(String product) {
        MarketFile marketFile = new MarketFile();
        return marketFile.getMarketConfig().getString("market." + product + ".price.type");
    }

    public static String format(String a, double price) {
        MarketFile marketFile = new MarketFile();

        String vault_prefix = marketFile.getMarketConfig().getString("format.vault.prefix");
        String vault_suffix = marketFile.getMarketConfig().getString("format.vault.suffix");


        String point_prefix = marketFile.getMarketConfig().getString("format.dungeon_point.prefix");
        String point_suffix = marketFile.getMarketConfig().getString("format.dungeon_point.suffix");

        if(getProductType(a).equalsIgnoreCase("vault")) {
            return String.format("%s %s %s", vault_suffix, price, vault_prefix);
        } else if(getProductType(a).equalsIgnoreCase("dungeon_point")) {
            return String.format("%s %s %s", point_suffix, price, point_prefix);
        } else {
            return String.format("%s %s %s", point_suffix, price, point_prefix);
        }
    }

}
