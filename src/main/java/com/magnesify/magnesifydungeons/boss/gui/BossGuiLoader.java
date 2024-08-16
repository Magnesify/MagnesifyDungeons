package com.magnesify.magnesifydungeons.boss.gui;

import com.magnesify.magnesifydungeons.boss.MagnesifyBoss;
import com.magnesify.magnesifydungeons.modules.managers.DatabaseManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

import static com.magnesify.magnesifydungeons.MagnesifyDungeons.get;
import static com.magnesify.magnesifydungeons.dungeon.entitys.DungeonPlayer.parseHexColors;

public class BossGuiLoader {

    private static Inventory inv = null;

    @Deprecated
    public static void openInventory(Player ent, MagnesifyBoss magnesifyBoss) {
        inv = Bukkit.createInventory(null,  54, parseHexColors(get().getConfig().getString("settings.boss.title")));
        loadItems(magnesifyBoss);
        ent.openInventory(inv);
    }

    public static String stats() {
        DatabaseManager databaseManager = new DatabaseManager(get());
        List<DatabaseManager.Stats.Sort.Player> sortedPlayers = databaseManager.stats().sort().getSortedPlayersByKill();
        StringBuilder data = new StringBuilder();
        for (int i = 0; i < Math.min(3, sortedPlayers.size()); i++) {
            DatabaseManager.Stats.Sort.Player player = sortedPlayers.get(i);
            data.append("#").append(i + 1).append(" ").append(player.getName());
            data.append("\n");
        }
        return data.toString();
    }



    @Deprecated
    public static void loadItems(MagnesifyBoss magnesifyBoss) {
        String name = magnesifyBoss.name();
        DatabaseManager databaseManager = new DatabaseManager(get());
        ItemStack itemStack = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(parseHexColors(String.format("&fDüzenleniyor: <#4b8eff>&l%s", magnesifyBoss.name())));
        itemStack.setItemMeta(meta);
        inv.setItem(13, itemStack);


        ItemStack new_wep = new ItemStack(Material.WOODEN_SWORD);
        ItemMeta metanew_wep = new_wep.getItemMeta();
        metanew_wep.setDisplayName(parseHexColors("&7Yeni Silah İle Birlikte Buraya Tıklayınız"));
        new_wep.setItemMeta(metanew_wep);
        inv.setItem(15, new_wep);

        ItemStack new_wep_hel = new ItemStack(Material.LEATHER_HELMET);
        ItemMeta metanew_wep_hel = new_wep_hel.getItemMeta();
        metanew_wep_hel.setDisplayName(parseHexColors("&7Yeni Kask İle Birlikte Buraya Tıklayınız"));
        new_wep_hel.setItemMeta(metanew_wep_hel);
        inv.setItem(16, new_wep_hel);

        ItemStack new_wep_ch = new ItemStack(Material.LEATHER_CHESTPLATE);
        ItemMeta metanew_wep_ch = new_wep_ch.getItemMeta();
        metanew_wep_ch.setDisplayName(parseHexColors("&7Yeni Göğüslük İle Birlikte Buraya Tıklayınız"));
        new_wep_ch.setItemMeta(metanew_wep_ch);
        inv.setItem(25, new_wep_ch);

        ItemStack new_wep_ch_lg = new ItemStack(Material.LEATHER_LEGGINGS);
        ItemMeta metanew_wep_ch_lg = new_wep_ch_lg.getItemMeta();
        metanew_wep_ch_lg.setDisplayName(parseHexColors("&7Yeni Ayaklıklar İle Birlikte Buraya Tıklayınız"));
        new_wep_ch_lg.setItemMeta(metanew_wep_ch_lg);
        inv.setItem(34, new_wep_ch_lg);

        ItemStack new_wep_ch_lg_b = new ItemStack(Material.LEATHER_BOOTS);
        ItemMeta metanew_wep_ch_lg_b = new_wep_ch_lg_b.getItemMeta();
        metanew_wep_ch_lg_b.setDisplayName(parseHexColors("&7Yeni Botlar İle Birlikte Buraya Tıklayınız"));
        new_wep_ch_lg_b.setItemMeta(metanew_wep_ch_lg_b);
        inv.setItem(43, new_wep_ch_lg_b);

        ItemStack new_wep_ch_lg_b_d = new ItemStack(Material.EMERALD);
        ItemMeta metanew_wep_ch_lg_b_d = new_wep_ch_lg_b_d.getItemMeta();
        metanew_wep_ch_lg_b_d.setDisplayName(parseHexColors("&eYaratığın Düşürdüğü Eşyaları Güncelle"));
        new_wep_ch_lg_b_d.setItemMeta(metanew_wep_ch_lg_b_d);
        inv.setItem(40, new_wep_ch_lg_b_d);

        ItemStack new_wep_ch_lg_b_d_s = new ItemStack(Material.REDSTONE_TORCH);
        ItemMeta metanew_wep_ch_lg_b_d_s = new_wep_ch_lg_b_d_s.getItemMeta();
        metanew_wep_ch_lg_b_d_s.setDisplayName(parseHexColors("&eYaratık Ayarları"));
        new_wep_ch_lg_b_d_s.setItemMeta(metanew_wep_ch_lg_b_d_s);
        inv.setItem(31, new_wep_ch_lg_b_d_s);

        ItemStack helmet_p = new ItemStack(Material.getMaterial(databaseManager.boss().getHelmetItem(name)));
        ItemMeta helmet_pmeta = helmet_p.getItemMeta();
        for(String a : databaseManager.boss().getHelmetEnchants(name)) {
            String[] split = a.split(":");
            NamespacedKey key = new NamespacedKey("minecraft", split[0]);
            helmet_pmeta.addEnchant(Enchantment.getByKey(key), Integer.parseInt(split[1]), true);
        }
        helmet_p.setItemMeta(helmet_pmeta);
        inv.setItem(10, helmet_p);

        ItemStack chestplate_p = new ItemStack(Material.getMaterial(databaseManager.boss().getChestplateItem(name)));
        ItemMeta chestplate_pmeta = chestplate_p.getItemMeta();
        for(String a : databaseManager.boss().getChestplateEnchants(name)) {
            String[] split = a.split(":");
            NamespacedKey key = new NamespacedKey("minecraft", split[0]);
            chestplate_pmeta.addEnchant(Enchantment.getByKey(key), Integer.parseInt(split[1]), true);
        }
        chestplate_p.setItemMeta(chestplate_pmeta);
        inv.setItem(19, chestplate_p);

        ItemStack leggings_p = new ItemStack(Material.getMaterial(databaseManager.boss().getLeggingsItem(name)));
        ItemMeta leggings_pmeta = leggings_p.getItemMeta();
        for(String a : databaseManager.boss().getLeggingsEnchant(name)) {
            String[] split = a.split(":");
            NamespacedKey key = new NamespacedKey("minecraft", split[0]);
            leggings_pmeta.addEnchant(Enchantment.getByKey(key), Integer.parseInt(split[1]), true);
        }
        leggings_p.setItemMeta(leggings_pmeta);
        inv.setItem(28, leggings_p);

        ItemStack boots_p = new ItemStack(Material.getMaterial(databaseManager.boss().getBootsItem(name)));
        ItemMeta boots_pmeta = boots_p.getItemMeta();
        for(String a : databaseManager.boss().getBootsEnchant(name)) {
            String[] split = a.split(":");
            NamespacedKey key = new NamespacedKey("minecraft", split[0]);
            boots_pmeta.addEnchant(Enchantment.getByKey(key), Integer.parseInt(split[1]), true);
        }
        boots_p.setItemMeta(boots_pmeta);
        inv.setItem(37, boots_p);


        ItemStack weaponp = new ItemStack(Material.getMaterial(databaseManager.boss().getWeaponsItem(name)));
        ItemMeta weaponpmeta = weaponp.getItemMeta();
        for(String a : databaseManager.boss().getWeaponEnchant(name)) {
            String[] split = a.split(":");
            NamespacedKey key = new NamespacedKey("minecraft", split[0]);
            weaponpmeta.addEnchant(Enchantment.getByKey(key), Integer.parseInt(split[1]), true);
        }

        weaponp.setItemMeta(weaponpmeta);
        inv.setItem(11, weaponp);


        ItemStack info = new ItemStack(Material.ORANGE_STAINED_GLASS_PANE);
        ItemMeta infometa = info.getItemMeta();
        infometa.setDisplayName(parseHexColors("<#4b8eff>&lDetaylar"));
        List<String> sub_lore = new ArrayList<>();
        sub_lore.add(parseHexColors("&f"));
        sub_lore.add(parseHexColors("&fYaratık canı: <#4b8eff>" + databaseManager.boss().getHealth(name)));
        sub_lore.add(parseHexColors("&fYaratık adı: <#4b8eff>" + databaseManager.boss().getDisplay(name)));
        sub_lore.add(parseHexColors("&fSaldırı hasarı: <#4b8eff>" + databaseManager.boss().getAttack(name)));
        sub_lore.add(parseHexColors("&fSaldırı tepmesi: <#4b8eff>" + databaseManager.boss().getKnockback(name)));
        sub_lore.add(parseHexColors("&fYaratık tipi: <#4b8eff>" + databaseManager.boss().getType(name)));
        sub_lore.add(parseHexColors("&f"));
        sub_lore.add(parseHexColors("&fDüşürdüğü eşyalar:"));
        for (String a : magnesifyBoss.drops()) {
            String[] split = a.split(":");
            sub_lore.add(parseHexColors("&f - &e" + split[1] + "x " + split[0]));
        }
        infometa.setLore(sub_lore);
        info.setItemMeta(infometa);
        inv.setItem(22, info);
    }

}
