package com.magnesify.magnesifydungeons.market.gui;

import com.magnesify.magnesifydungeons.MagnesifyDungeons;
import com.magnesify.magnesifydungeons.dungeon.entitys.DungeonPlayer;
import com.magnesify.magnesifydungeons.market.file.MarketFile;
import com.magnesify.magnesifydungeons.modules.StringFunctionReader;
import com.magnesify.magnesifydungeons.storage.PlayerMethods;
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
            for (String ranks : marketFile.getMarketConfig().getConfigurationSection("market").getKeys(false)) {
                int price = marketFile.getMarketConfig().getInt("market." + ranks + ".price.value");
                int slot = marketFile.getMarketConfig().getInt("market." + ranks + ".slot");
                for (String items : marketFile.getMarketConfig().getConfigurationSection("market").getKeys(false)) {
                    boolean commands = marketFile.getMarketConfig().isSet("market." + ranks + ".items." + items + ".commands");
                    boolean material = marketFile.getMarketConfig().isSet("market." + ranks + ".items." + items + ".material");
                    boolean enchants = marketFile.getMarketConfig().isSet("market." + ranks + ".items." + items + ".enchants");
                    boolean display = marketFile.getMarketConfig().isSet("market." + ranks + ".items." + items + ".display");
                    boolean lore = marketFile.getMarketConfig().isSet("market." + ranks + ".items." + items + ".lore");
                    if (event.getSlot() == slot) {
                        DungeonPlayer dungeonPlayer = new DungeonPlayer(player);
                        if (getProductType(ranks).equalsIgnoreCase("dungeon_point")) {
                            if (playerMethods.getPoints(player) >= price) {
                                playerMethods.removePoint(player, price);
                                dungeonPlayer.messageManager().chat(get().getConfig().getString("settings.messages.market.buy").replace("#product", parseHexColors(marketFile.getMarketConfig().getString("market." + ranks + ".display"))).replace("#price", format(ranks, price)));
                                if(commands) {
                                    for(String cmds : marketFile.getMarketConfig().getStringList("market." + ranks + ".items." + items + ".commands")) {
                                        StringFunctionReader.RunFunction(player, cmds);
                                    }
                                }
                                if (material) {
                                    System.out.println("A");
                                    if(enchants) {
                                        System.out.println("b");
                                        int amount = marketFile.getMarketConfig().getInt("market." + ranks + ".items." + items + ".amount");
                                        ItemStack itemStack = new ItemStack(Material.getMaterial(marketFile.getMarketConfig().getString("market." + ranks + ".items." + items + ".material")), amount);
                                        ItemMeta meta = itemStack.getItemMeta();
                                        if(display) meta.setDisplayName(parseHexColors(marketFile.getMarketConfig().getString("market." + ranks + ".items." + items + ".display") ));
                                        if(lore) {
                                            List<String> main_lores = marketFile.getMarketConfig().getStringList("market." + ranks + ".lore");
                                            List<String> sub_lore = new ArrayList<>();
                                            for(int a = 0; a<main_lores.size();a++) {
                                                sub_lore.add(parseHexColors(main_lores.get(a)));
                                            }
                                            meta.setLore(sub_lore);
                                        }
                                        for(String ench : marketFile.getMarketConfig().getStringList("market." + ranks + ".items." + items + ".enchants")) {
                                            String[] spl = ench.split(":");
                                            NamespacedKey namespacedKey = new NamespacedKey(get(), spl[0]);
                                            meta.addEnchant(Enchantment.getByKey(namespacedKey), Integer.parseInt(spl[1]), true);
                                        }
                                        itemStack.setItemMeta(meta);
                                        player.getInventory().addItem(itemStack);
                                        System.out.println("C");
                                    } else {
                                        int amount = marketFile.getMarketConfig().getInt("market." + ranks + ".items." + items + ".amount");
                                        System.out.println("D");
                                        ItemStack itemStack = new ItemStack(Material.getMaterial(marketFile.getMarketConfig().getString("market." + ranks + ".items." + items + ".material")), amount);
                                        ItemMeta meta = itemStack.getItemMeta();
                                        if(display) meta.setDisplayName(parseHexColors(marketFile.getMarketConfig().getString("market." + ranks + ".items." + items + ".display") ));
                                        if(lore) {
                                            List<String> main_lores = marketFile.getMarketConfig().getStringList("market." + ranks + ".lore");
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
                                dungeonPlayer.messageManager().chat(get().getConfig().getString("settings.messages.market.not-enough-balance").replace("#product", parseHexColors(marketFile.getMarketConfig().getString("market." + ranks + ".display"))).replace("#price", format(ranks, price)));
                            }
                            return;
                        }
                    }
                }
            }
        }
    }
}
