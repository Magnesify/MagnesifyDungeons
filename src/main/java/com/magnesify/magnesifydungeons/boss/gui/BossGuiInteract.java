package com.magnesify.magnesifydungeons.boss.gui;

import com.magnesify.magnesifydungeons.MagnesifyDungeons;
import com.magnesify.magnesifydungeons.boss.MagnesifyBoss;
import com.magnesify.magnesifydungeons.boss.gui.drops.DropsGuiLoader;
import com.magnesify.magnesifydungeons.modules.managers.DatabaseManager;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import static com.magnesify.magnesifydungeons.MagnesifyDungeons.get;
import static com.magnesify.magnesifydungeons.boss.BossManager.boss_manager;
import static com.magnesify.magnesifydungeons.dungeon.entitys.DungeonPlayer.parseHexColors;

public class BossGuiInteract implements Listener {
    public BossGuiInteract(MagnesifyDungeons magnesifyDungeons) {}

    @Deprecated
    @EventHandler
    public void onInteract(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        String gui_title = event.getView().getTitle();
        if(gui_title.equalsIgnoreCase(parseHexColors(get().getConfig().getString("settings.boss.title")))) {
            DatabaseManager databaseManager = new DatabaseManager(get());
            switch (event.getSlot()) {
                case 10:
                    event.setCancelled(true);
                    break;
                case 13:
                    event.setCancelled(true);
                    break;
                case 22:
                    event.setCancelled(true);
                    break;
                case 11:
                    event.setCancelled(true);
                    break;
                case 19:
                    event.setCancelled(true);
                    break;
                case 28:
                    event.setCancelled(true);
                    break;
                case 37:
                    event.setCancelled(true);
                    break;
                case 40:
                    event.setCancelled(true);
                    player.closeInventory();
                    DropsGuiLoader.openInventory(player, new MagnesifyBoss(boss_manager.get("boss")));
                    break;
                case 31:
                    event.setCancelled(true);
                    break;
                case 16:
                    if(boss_manager.get("boss") == null) {
                        return;
                    }
                    if(event.getCursor() != null) {
                        ItemStack itemStack = new ItemStack(event.getCursor());
                        databaseManager.boss().setHelmetItem(boss_manager.get("boss"), itemStack.getType().toString());
                        if(itemStack.getItemMeta() == null) return;
                        if(itemStack.getItemMeta().hasEnchants()) {
                            String encahnt = "";
                            for (Enchantment enchantment : itemStack.getEnchantments().keySet()) {
                                NamespacedKey key = new NamespacedKey("minecraft", enchantment.getKey().getKey());
                                encahnt += (String.format("%s:%s", Enchantment.getByKey(key).toString().replace("CraftEnchantment[", "").replace("]", "").replace("minecraft:", ""),itemStack.getEnchantmentLevel(enchantment)));
                                encahnt += ",";
                            }
                            databaseManager.boss().setHelmetEnchants(boss_manager.get("boss"), encahnt);
                        }
                        player.closeInventory();
                        MagnesifyBoss magnesifyBoss = new MagnesifyBoss(boss_manager.get("boss"));
                        BossGuiLoader.openInventory(player, magnesifyBoss);
                    }
                    break;
                case 25:
                    if(boss_manager.get("boss") == null) {
                        return;
                    }
                    if(event.getCursor() != null) {
                        ItemStack itemStack = new ItemStack(event.getCursor());
                        databaseManager.boss().setChestplateItem(boss_manager.get("boss"), itemStack.getType().toString());
                        if(itemStack.getItemMeta() == null) return;
                        if(itemStack.getItemMeta().hasEnchants()) {
                            String encahnt = "";
                            for (Enchantment enchantment : itemStack.getEnchantments().keySet()) {
                                NamespacedKey key = new NamespacedKey("minecraft", enchantment.getKey().getKey());
                                encahnt += (String.format("%s:%s", Enchantment.getByKey(key).toString().replace("CraftEnchantment[", "").replace("]", "").replace("minecraft:", ""),itemStack.getEnchantmentLevel(enchantment)));
                                encahnt += ",";
                            }
                            databaseManager.boss().setChestplateEnchants(boss_manager.get("boss"), encahnt);
                        }
                        player.closeInventory();
                        MagnesifyBoss magnesifyBoss = new MagnesifyBoss(boss_manager.get("boss"));
                        BossGuiLoader.openInventory(player, magnesifyBoss);
                    }
                    break;
                case 34:
                    if(boss_manager.get("boss") == null) {
                        return;
                    }
                    if(event.getCursor() != null) {
                        ItemStack itemStack = new ItemStack(event.getCursor());
                        databaseManager.boss().setLeggingsItem(boss_manager.get("boss"), itemStack.getType().toString());
                        if(itemStack.getItemMeta() == null) return;
                        if(itemStack.getItemMeta().hasEnchants()) {
                            String encahnt = "";
                            for (Enchantment enchantment : itemStack.getEnchantments().keySet()) {
                                NamespacedKey key = new NamespacedKey("minecraft", enchantment.getKey().getKey());
                                encahnt += (String.format("%s:%s", Enchantment.getByKey(key).toString().replace("CraftEnchantment[", "").replace("]", "").replace("minecraft:", ""),itemStack.getEnchantmentLevel(enchantment)));
                                encahnt += ",";
                            }
                            databaseManager.boss().setLeggingsEnchant(boss_manager.get("boss"), encahnt);
                        }
                        player.closeInventory();
                        MagnesifyBoss magnesifyBoss = new MagnesifyBoss(boss_manager.get("boss"));
                        BossGuiLoader.openInventory(player, magnesifyBoss);
                    }
                    break;
                case 43:
                    if(boss_manager.get("boss") == null) {
                        return;
                    }
                    if(event.getCursor() != null) {
                        ItemStack itemStack = new ItemStack(event.getCursor());
                        databaseManager.boss().setBootsItem(boss_manager.get("boss"), itemStack.getType().toString());
                        if(itemStack.getItemMeta() == null) return;
                        if(itemStack.getItemMeta().hasEnchants()) {
                            String encahnt = "";
                            for (Enchantment enchantment : itemStack.getEnchantments().keySet()) {
                                NamespacedKey key = new NamespacedKey("minecraft", enchantment.getKey().getKey());
                                encahnt += (String.format("%s:%s", Enchantment.getByKey(key).toString().replace("CraftEnchantment[", "").replace("]", "").replace("minecraft:", ""),itemStack.getEnchantmentLevel(enchantment)));
                                encahnt += ",";
                            }
                            databaseManager.boss().setBootsEnchant(boss_manager.get("boss"), encahnt);
                        }
                        player.closeInventory();
                        MagnesifyBoss magnesifyBoss = new MagnesifyBoss(boss_manager.get("boss"));
                        BossGuiLoader.openInventory(player, magnesifyBoss);
                    }
                    break;
                case 15:
                    if(boss_manager.get("boss") == null) {
                        return;
                    }
                    if(event.getCursor() != null) {
                        ItemStack itemStack = new ItemStack(event.getCursor());
                        databaseManager.boss().setWeaponItem(boss_manager.get("boss"), itemStack.getType().toString());
                        if(itemStack.getItemMeta() == null) return;
                        if(itemStack.getItemMeta().hasEnchants()) {
                            String encahnt = "";
                            for (Enchantment enchantment : itemStack.getEnchantments().keySet()) {
                                NamespacedKey key = new NamespacedKey("minecraft", enchantment.getKey().getKey());
                                encahnt += (String.format("%s:%s", Enchantment.getByKey(key).toString().replace("CraftEnchantment[", "").replace("]", "").replace("minecraft:", ""),itemStack.getEnchantmentLevel(enchantment)));
                                encahnt += ",";
                            }
                            databaseManager.boss().setWeaponEnchant(boss_manager.get("boss"), encahnt);
                        }
                        player.closeInventory();
                        MagnesifyBoss magnesifyBoss = new MagnesifyBoss(boss_manager.get("boss"));
                        BossGuiLoader.openInventory(player, magnesifyBoss);
                    }
                    break;
            }

        }
    }
}
