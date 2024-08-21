package com.magnesify.magnesifydungeons.boss.gui.settings;

import com.magnesify.magnesifydungeons.boss.MagnesifyBoss;
import com.magnesify.magnesifydungeons.languages.LanguageFile;
import com.magnesify.magnesifydungeons.modules.managers.DatabaseManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import static com.magnesify.magnesifydungeons.MagnesifyDungeons.get;
import static com.magnesify.magnesifydungeons.dungeon.entitys.DungeonPlayer.parseHexColors;

public class SettingsGuiLoader {

    private static Inventory inv = null;

    @Deprecated
    public static void openInventory(Player ent, MagnesifyBoss magnesifyBoss) {
        inv = Bukkit.createInventory(null,  54, parseHexColors(get().getConfig().getString("settings.boss.settings.title")));
        loadItems(magnesifyBoss);
        ent.openInventory(inv);
    }


    @Deprecated
    public static void loadItems(MagnesifyBoss magnesifyBoss) {
        ItemStack item = new ItemStack(Material.RED_DYE, 1);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(parseHexColors(String.format(new LanguageFile().getLanguage().getString("plugin.boss-gui.settings-gui.health"), magnesifyBoss.health())));
        item.setItemMeta(itemMeta);
        inv.setItem(10, item);
        ItemStack health = new ItemStack(Material.REPEATER, 1);
        ItemMeta healthmeta = health.getItemMeta();
        healthmeta.setDisplayName(parseHexColors(String.format(new LanguageFile().getLanguage().getString("plugin.boss-gui.settings-gui.health-lore"), magnesifyBoss.health())));
        health.setItemMeta(healthmeta);
        inv.setItem(19, health);


        ItemStack itema = new ItemStack(Material.IRON_SWORD, 1);
        ItemMeta itemMetaa = itema.getItemMeta();
        itemMetaa.setDisplayName(parseHexColors(String.format(new LanguageFile().getLanguage().getString("plugin.boss-gui.settings-gui.knockback"), new DatabaseManager(get()).boss().getKnockback(magnesifyBoss.name()))));
        itema.setItemMeta(itemMetaa);
        inv.setItem(12, itema);
        ItemStack healtha = new ItemStack(Material.REPEATER, 1);
        ItemMeta healthmetaa = healtha.getItemMeta();
        healthmetaa.setDisplayName(parseHexColors((new LanguageFile().getLanguage().getString("plugin.boss-gui.settings-gui.knockback-lore"))));
        healtha.setItemMeta(healthmetaa);
        inv.setItem(21, healtha);

        ItemStack attacka = new ItemStack(Material.IRON_SWORD, 1);
        ItemMeta attackMetaa = attacka.getItemMeta();
        attackMetaa.setDisplayName(parseHexColors(String.format(new LanguageFile().getLanguage().getString("plugin.boss-gui.settings-gui.attack"), new DatabaseManager(get()).boss().getAttack(magnesifyBoss.name()))));
        attacka.setItemMeta(attackMetaa);
        inv.setItem(14, attacka);
        ItemStack attackma = new ItemStack(Material.REPEATER, 1);
        ItemMeta attackmmetaa = attackma.getItemMeta();
        attackmmetaa.setDisplayName(parseHexColors((new LanguageFile().getLanguage().getString("plugin.boss-gui.settings-gui.attack-lore"))));
        attackma.setItemMeta(attackmmetaa);
        inv.setItem(23, attackma);

        ItemStack mythica = new ItemStack(Material.ZOMBIE_HEAD, 1);
        ItemMeta mythicMetaa = mythica.getItemMeta();
        mythicMetaa.setDisplayName(parseHexColors(String.format(new LanguageFile().getLanguage().getString("plugin.boss-gui.settings-gui.mythic"), new DatabaseManager(get()).boss().isMythic(magnesifyBoss.name()) == true ? "Mythic Boss" : "Magnesify Boss")));
        mythica.setItemMeta(mythicMetaa);
        inv.setItem(16, mythica);
        ItemStack mythicca = new ItemStack(Material.REPEATER, 1);
        ItemMeta mythiccmetaa = mythicca.getItemMeta();
        mythiccmetaa.setDisplayName(parseHexColors((new LanguageFile().getLanguage().getString("plugin.boss-gui.settings-gui.mythic-lore"))));
        mythicca.setItemMeta(mythiccmetaa);
        inv.setItem(25, mythicca);


        ItemStack back = new ItemStack(Material.ARROW, 1);
        ItemMeta backmetaa = back.getItemMeta();
        backmetaa.setDisplayName(parseHexColors((new LanguageFile().getLanguage().getString("plugin.boss-gui.settings-gui.back"))));
        back.setItemMeta(backmetaa);
        inv.setItem(40, back);
    }
}
