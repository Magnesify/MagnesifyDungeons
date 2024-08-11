package com.magnesify.magnesifydungeons.market.gui;

import com.magnesify.magnesifydungeons.MagnesifyDungeons;
import com.magnesify.magnesifydungeons.market.file.MarketFile;
import com.magnesify.magnesifydungeons.storage.PlayerMethods;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import static com.magnesify.magnesifydungeons.MagnesifyDungeons.get;
import static com.magnesify.magnesifydungeons.market.format.PriceFormat.getProductType;

public class MarketGuiInteract implements Listener {
    public MarketGuiInteract(MagnesifyDungeons magnesifyDungeons) {}

    @Deprecated
    @EventHandler
    public void onInteract(InventoryClickEvent event) {
        String gui_title = event.getView().getTitle();
        Player player = (Player) event.getWhoClicked();
        if(gui_title.equals(ChatColor.translateAlternateColorCodes('&',  get().getConfig().getString("settings.market.title")))) {
            MarketFile marketFile = new MarketFile();
            event.setCancelled(true);
            PlayerMethods playerMethods = new PlayerMethods();
            for (String ranks : marketFile.getMarketConfig().getConfigurationSection("market").getKeys(false)) {
                int price = marketFile.getMarketConfig().getInt("market." + ranks + ".price.value");
                int slot = marketFile.getMarketConfig().getInt("market." + ranks + ".slot");
                if (event.getSlot() == slot) {
                    if (getProductType(ranks).equalsIgnoreCase("dungeon_point")) {
                        if (playerMethods.getPoints(player) >= price) {
                            playerMethods.removePoint(player, price);
                            // burada kaldın : Marketi yapıyorsun. sadece market satışı olacak. Buradan sonra customcommands.java yı yap.
                        }
                    }
                }
            }
        }
    }
}
