package com.magnesify.magnesifydungeons.dungeon.types.trigger;

import com.magnesify.magnesifydungeons.MagnesifyDungeons;
import com.magnesify.magnesifydungeons.modules.managers.DatabaseManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class TriggerSetup {

    /*

        Setup başladığında yöneticinin eline şunlar verilecek.
        Yeşil yün - Zindana başlangıç bölgesi
        Sarı yün - Sonraki seviyeye geçiş yapılacak alan
        Kırmızı yün - Yaratığın spawn olacağı nokta
        Turuncu yün - Son seviyeye geçiş yapılacak alan
        Kızıltaş Meşale - Kurulumu iptal etme

     */


    public void StartSetup(Player player, String name, Location location) {
        Inventory inventory = player.getInventory();

        ItemStack start_location = new ItemStack(Material.GREEN_WOOL, 1);
        ItemMeta start_location_meta = start_location.getItemMeta();
        start_location_meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&aBaşlangıç Bölgesi : " + name));
        List<String> slm = new ArrayList<>();
        slm.add(" ");
        slm.add(ChatColor.translateAlternateColorCodes('&', "&fZindana giren oyuncuların başlayacağı ilk bölgeyi seçmek için kullanılır."));
        start_location_meta.setLore(slm);
        start_location.setItemMeta(start_location_meta);
        inventory.setItem(0, start_location);

        ItemStack checkpoint = new ItemStack(Material.YELLOW_WOOL, 1);
        ItemMeta checkpoint_meta = checkpoint.getItemMeta();
        checkpoint_meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&aYeni Seviyeye Giriş Bölgesi : " + name));
        List<String> cpm = new ArrayList<>();
        cpm.add(" ");
        cpm.add(ChatColor.translateAlternateColorCodes('&', "&fCheckpoint olarak düşünülebilir, yeni bir seviyeye geçiş için kullanılır."));
        checkpoint_meta.setLore(cpm);
        checkpoint.setItemMeta(checkpoint_meta);
        inventory.setItem(1, checkpoint);

        ItemStack boss = new ItemStack(Material.RED_WOOL, 1);
        ItemMeta boss_meta = boss.getItemMeta();
        boss_meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&aFinal Yaratığının Doğum Noktası : " + name));
        List<String> bm = new ArrayList<>();
        bm.add(" ");
        bm.add(ChatColor.translateAlternateColorCodes('&', "&fFinal yaratığının doğacağı lokasyonu seçmek için kullanırsınız."));
        boss_meta.setLore(bm);
        boss.setItemMeta(boss_meta);
        inventory.setItem(2, boss);

        DatabaseManager databaseManager = new DatabaseManager(MagnesifyDungeons.get());
        databaseManager.CreateNewTriggerTypeDungeon(name, "Yok", "Yok", 1, 60, 10, location, 1);

    }

}
