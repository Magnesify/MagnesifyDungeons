package com.magnesify.magnesifydungeons.market.gui;

import com.magnesify.magnesifydungeons.MagnesifyDungeons;
import com.magnesify.magnesifydungeons.dungeon.entitys.DungeonPlayer;
import com.magnesify.magnesifydungeons.languages.LanguageFile;
import com.magnesify.magnesifydungeons.market.file.MarketFile;
import com.magnesify.magnesifydungeons.modules.StringFunctionReader;
import com.magnesify.magnesifydungeons.storage.PlayerMethods;
import com.magnesify.magnesifydungeons.support.Vault;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

import static com.magnesify.magnesifydungeons.MagnesifyDungeons.get;
import static com.magnesify.magnesifydungeons.dungeon.entitys.DungeonPlayer.parseHexColors;
import static com.magnesify.magnesifydungeons.market.format.PriceFormat.format;
import static com.magnesify.magnesifydungeons.market.format.PriceFormat.getProductType;

public class MarketGuiInteract implements Listener {
    public MarketGuiInteract(MagnesifyDungeons magnesifyDungeons) {}

    @Deprecated
    @EventHandler
    public void onInteract(InventoryClickEvent event) {
        String gui_title = event.getView().getTitle();
        Player player = (Player) event.getWhoClicked();
        if(gui_title.equals(parseHexColors(get().getConfig().getString("settings.market.title")))) {
            MarketFile marketFile = new MarketFile();
            event.setCancelled(true);
            PlayerMethods playerMethods = new PlayerMethods();
            if (event.getCurrentItem() != null) {
               if(event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(parseHexColors(marketFile.getMarketConfig().getString("market.next.display")))) {
                    player.closeInventory();
                    MarketGuiLoader.openInventory(player, MarketGuiLoader.current_page.get(player.getUniqueId())+1);
               } else if(event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(parseHexColors(marketFile.getMarketConfig().getString("market.prev.display")))) {
                   player.closeInventory();
                   MarketGuiLoader.openInventory(player, MarketGuiLoader.current_page.get(player.getUniqueId())-1);
               }
            }
            for (String ranks : marketFile.getMarketConfig().getConfigurationSection("market." + MarketGuiLoader.current_page.get(player.getUniqueId()) + ".products").getKeys(false)) {
                int price = marketFile.getMarketConfig().getInt("market." + MarketGuiLoader.current_page.get(player.getUniqueId()) + ".products." + ranks + ".price.value");
                int slot = marketFile.getMarketConfig().getInt("market." + MarketGuiLoader.current_page.get(player.getUniqueId()) + ".products." + ranks + ".slot");
                for (String items : marketFile.getMarketConfig().getConfigurationSection("market." + MarketGuiLoader.current_page.get(player.getUniqueId()) + ".products." + ranks + ".items").getKeys(false)) {
                    boolean commands = marketFile.getMarketConfig().isSet("market." + MarketGuiLoader.current_page.get(player.getUniqueId()) + ".products." + ranks + ".items." + items + ".commands");
                    boolean material = marketFile.getMarketConfig().isSet("market." + MarketGuiLoader.current_page.get(player.getUniqueId()) + ".products." + ranks + ".items." + items + ".material");
                    boolean enchants = marketFile.getMarketConfig().isSet("market." + MarketGuiLoader.current_page.get(player.getUniqueId()) + ".products." + ranks + ".items." + items + ".enchants");
                    boolean display = marketFile.getMarketConfig().isSet("market." + MarketGuiLoader.current_page.get(player.getUniqueId()) + ".products." + ranks + ".items." + items + ".display");
                    boolean lore = marketFile.getMarketConfig().isSet("market." + MarketGuiLoader.current_page.get(player.getUniqueId()) + ".products." + ranks + ".items." + items + ".lore");
                    if (event.getSlot() == slot) {
                        DungeonPlayer dungeonPlayer = new DungeonPlayer(player);
                        if (getProductType(ranks, player).equalsIgnoreCase("vault")) {
                            if(Vault.getVault()) {
                                if (Vault.getEconomy().getBalance(player) >= price) {
                                    Vault.getEconomy().withdrawPlayer(player, price);
                                    dungeonPlayer.messageManager().chat(new LanguageFile().getLanguage().getString("messages.market.buy").replace("#product", parseHexColors(marketFile.getMarketConfig().getString("market." + MarketGuiLoader.current_page.get(player.getUniqueId()) + ".products." + ranks + ".display"))).replace("#price", format(ranks, price,player)));
                                    if(commands) {
                                        for(String cmds : marketFile.getMarketConfig().getStringList("market." + MarketGuiLoader.current_page.get(player.getUniqueId()) + ".products." + ranks + ".items." + items + ".commands")) {
                                            StringFunctionReader.RunFunction(player, cmds);
                                        }
                                    }
                                    if (material) {
                                        if(enchants) {
                                            int amount = marketFile.getMarketConfig().getInt("market." + MarketGuiLoader.current_page.get(player.getUniqueId()) + ".products." + ranks + ".items." + items + ".amount");
                                            ItemStack itemStack = new ItemStack(Material.getMaterial(marketFile.getMarketConfig().getString("market." + MarketGuiLoader.current_page.get(player.getUniqueId()) + ".products." + ranks + ".items." + items + ".material")), amount);
                                            ItemMeta meta = itemStack.getItemMeta();
                                            if(display) meta.setDisplayName(parseHexColors(marketFile.getMarketConfig().getString("market." + MarketGuiLoader.current_page.get(player.getUniqueId()) + ".products." + ranks + ".items." + items + ".display") ));
                                            if(lore) {
                                                List<String> main_lores = marketFile.getMarketConfig().getStringList("market." + MarketGuiLoader.current_page.get(player.getUniqueId()) + ".products." + ranks + ".items." + items + ".lore");
                                                List<String> sub_lore = new ArrayList<>();
                                                for(int a = 0; a<main_lores.size();a++) {
                                                    sub_lore.add(parseHexColors(main_lores.get(a)));
                                                }
                                                meta.setLore(sub_lore);
                                            }
                                            for(String ench : marketFile.getMarketConfig().getStringList("market." + MarketGuiLoader.current_page.get(player.getUniqueId()) + ".products." + ranks + ".items." + items + ".enchants")) {
                                                String[] spl = ench.split(":");
                                                NamespacedKey key = new NamespacedKey("minecraft", spl[0]);
                                                meta.addEnchant(Enchantment.getByKey(key), Integer.parseInt(spl[1]), true);
                                            }
                                            // enchantta sorun var.
                                            itemStack.setItemMeta(meta);
                                            player.getInventory().addItem(itemStack);
                                        } else {
                                            int amount = marketFile.getMarketConfig().getInt("market." + MarketGuiLoader.current_page.get(player.getUniqueId()) + ".products." + ranks + ".items." + items + ".amount");
                                            ItemStack itemStack = new ItemStack(Material.getMaterial(marketFile.getMarketConfig().getString("market." + MarketGuiLoader.current_page.get(player.getUniqueId()) + ".products." + ranks + ".items." + items + ".material")), amount);
                                            ItemMeta meta = itemStack.getItemMeta();
                                            if(display) meta.setDisplayName(parseHexColors(marketFile.getMarketConfig().getString("market." + MarketGuiLoader.current_page.get(player.getUniqueId()) + ".products." + ranks + ".items." + items + ".display") ));
                                            if(lore) {
                                                List<String> main_lores = marketFile.getMarketConfig().getStringList("market." + MarketGuiLoader.current_page.get(player.getUniqueId()) + ".products." + ranks + ".items." + items + ".lore");
                                                List<String> sub_lore = new ArrayList<>();
                                                for(int a = 0; a<main_lores.size();a++) {
                                                    sub_lore.add(parseHexColors(main_lores.get(a)));
                                                }
                                                meta.setLore(sub_lore);
                                            }
                                            itemStack.setItemMeta(meta);
                                            player.getInventory().addItem(itemStack);
                                        }
                                    }
                                } else {
                                    dungeonPlayer.messageManager().chat(new LanguageFile().getLanguage().getString("messages.market.not-enough-balance").replace("#product", parseHexColors(marketFile.getMarketConfig().getString("market." + MarketGuiLoader.current_page.get(player.getUniqueId()) + ".products." + ranks + ".display"))).replace("#price", format(ranks, price,player)));
                                }
                            } else {
                                dungeonPlayer.messageManager().chat(new LanguageFile().getLanguage().getString("messages.depends.not-loaded").replace("#plugin","Vault"));
                            }
                            return;
                        }
                        if (getProductType(ranks, player).equalsIgnoreCase("dungeon_point")) {
                            if (playerMethods.getPoints(player) >= price) {
                                playerMethods.removePoint(player, price);
                                dungeonPlayer.messageManager().chat(new LanguageFile().getLanguage().getString("messages.market.buy").replace("#product", parseHexColors(marketFile.getMarketConfig().getString("market." + MarketGuiLoader.current_page.get(player.getUniqueId()) + ".products." + ranks + ".display"))).replace("#price", format(ranks, price,player)));
                                if(commands) {
                                    for(String cmds : marketFile.getMarketConfig().getStringList("market." + MarketGuiLoader.current_page.get(player.getUniqueId()) + ".products." + ranks + ".items." + items + ".commands")) {
                                        StringFunctionReader.RunFunction(player, cmds);
                                    }
                                }
                                if (material) {
                                    if(enchants) {
                                        int amount = marketFile.getMarketConfig().getInt("market." + MarketGuiLoader.current_page.get(player.getUniqueId()) + ".products." + ranks + ".items." + items + ".amount");
                                        ItemStack itemStack = new ItemStack(Material.getMaterial(marketFile.getMarketConfig().getString("market." + MarketGuiLoader.current_page.get(player.getUniqueId()) + ".products." + ranks + ".items." + items + ".material")), amount);
                                        ItemMeta meta = itemStack.getItemMeta();
                                        if(display) meta.setDisplayName(parseHexColors(marketFile.getMarketConfig().getString("market." + MarketGuiLoader.current_page.get(player.getUniqueId()) + ".products." + ranks + ".items." + items + ".display") ));
                                        if(lore) {
                                            List<String> main_lores = marketFile.getMarketConfig().getStringList("market." + MarketGuiLoader.current_page.get(player.getUniqueId()) + ".products." + ranks + ".items." + items + ".lore");
                                            List<String> sub_lore = new ArrayList<>();
                                            for(int a = 0; a<main_lores.size();a++) {
                                                sub_lore.add(parseHexColors(main_lores.get(a)));
                                            }
                                            meta.setLore(sub_lore);
                                        }
                                        for(String ench : marketFile.getMarketConfig().getStringList("market." + MarketGuiLoader.current_page.get(player.getUniqueId()) + ".products." + ranks + ".items." + items + ".enchants")) {
                                            String[] spl = ench.split(":");
                                            NamespacedKey key = new NamespacedKey("minecraft", spl[0]);
                                            meta.addEnchant(Enchantment.getByKey(key), Integer.parseInt(spl[1]), true);
                                        }
                                        // enchantta sorun var.
                                        itemStack.setItemMeta(meta);
                                        player.getInventory().addItem(itemStack);
                                    } else {
                                        int amount = marketFile.getMarketConfig().getInt("market." + MarketGuiLoader.current_page.get(player.getUniqueId()) + ".products." + ranks + ".items." + items + ".amount");
                                        ItemStack itemStack = new ItemStack(Material.getMaterial(marketFile.getMarketConfig().getString("market." + MarketGuiLoader.current_page.get(player.getUniqueId()) + ".products." + ranks + ".items." + items + ".material")), amount);
                                        ItemMeta meta = itemStack.getItemMeta();
                                        if(display) meta.setDisplayName(parseHexColors(marketFile.getMarketConfig().getString("market." + MarketGuiLoader.current_page.get(player.getUniqueId()) + ".products." + ranks + ".items." + items + ".display") ));
                                        if(lore) {
                                            List<String> main_lores = marketFile.getMarketConfig().getStringList("market." + MarketGuiLoader.current_page.get(player.getUniqueId()) + ".products." + ranks + ".items." + items + ".lore");
                                            List<String> sub_lore = new ArrayList<>();
                                            for(int a = 0; a<main_lores.size();a++) {
                                                sub_lore.add(parseHexColors(main_lores.get(a)));
                                            }
                                            meta.setLore(sub_lore);
                                        }
                                        itemStack.setItemMeta(meta);
                                        player.getInventory().addItem(itemStack);
                                    }
                                }
                            } else {
                                dungeonPlayer.messageManager().chat(new LanguageFile().getLanguage().getString("messages.market.not-enough-balance").replace("#product", parseHexColors(marketFile.getMarketConfig().getString("market." + MarketGuiLoader.current_page.get(player.getUniqueId()) + ".products." + ranks + ".display"))).replace("#price", format(ranks, price,player)));
                            }
                            return;
                        }
                    }
                }
            }
        }
    }
}
