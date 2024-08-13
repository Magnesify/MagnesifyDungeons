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

import static com.magnesify.magnesifydungeons.dungeon.types.trigger.commands.TriggerTypeDungeon.new_dungeon;

public class TriggerSetup {

    public void StartSetup(Player player, Location location) {
        Inventory inventory = player.getInventory();

        ItemStack start_location = new ItemStack(Material.NAME_TAG, 1);
        ItemMeta start_location_meta = start_location.getItemMeta();
        start_location_meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&aBaşlangıç Bölgesi"));
        List<String> slm = new ArrayList<>();
        slm.add(" ");
        slm.add(ChatColor.translateAlternateColorCodes('&', "&fZindana giren oyuncuların başlayacağı ilk bölgeyi seçmek için kullanılır."));
        start_location_meta.setLore(slm);
        start_location.setItemMeta(start_location_meta);
        inventory.setItem(0, start_location);

        ItemStack checkpoint = new ItemStack(Material.REPEATER, 1);
        ItemMeta checkpoint_meta = checkpoint.getItemMeta();
        checkpoint_meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&aYeni Seviyeye Giriş Bölgesi"));
        List<String> cpm = new ArrayList<>();
        cpm.add(" ");
        cpm.add(ChatColor.translateAlternateColorCodes('&', "&fCheckpoint olarak düşünülebilir, yeni bir seviyeye geçiş için kullanılır."));
        checkpoint_meta.setLore(cpm);
        checkpoint.setItemMeta(checkpoint_meta);
        inventory.setItem(1, checkpoint);

        ItemStack bp = new ItemStack(Material.IRON_SWORD, 1);
        ItemMeta bp_meta = bp.getItemMeta();
        bp_meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&aYaratık Doğum Noktası"));
        List<String> bpm = new ArrayList<>();
        bpm.add(" ");
        bpm.add(ChatColor.translateAlternateColorCodes('&', "&fYaratığının doğacağı lokasyonu seçmek için kullanırsınız."));
        bp_meta.setLore(bpm);
        bp.setItemMeta(bp_meta);
        inventory.setItem(2, bp);

        ItemStack cancel = new ItemStack(Material.ARROW, 1);
        ItemMeta cancel_meta = cancel.getItemMeta();
        cancel_meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&cKurulumu İptal Et"));
        List<String> cm = new ArrayList<>();
        cm.add(" ");
        cm.add(ChatColor.translateAlternateColorCodes('&', "&fKurulumu iptal etmek için sağ tıklayınız."));
        cancel_meta.setLore(cm);
        cancel.setItemMeta(cancel_meta);
        inventory.setItem(8, cancel);

        ItemStack kurulum = new ItemStack(Material.GREEN_DYE, 1);
        ItemMeta kurulum_meta = cancel.getItemMeta();
        kurulum_meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&eKurulumu Bitir"));
        List<String> km = new ArrayList<>();
        km.add(" ");
        km.add(ChatColor.translateAlternateColorCodes('&', "&fKurulumu tamamlamak için tıklayınız."));
        kurulum_meta.setLore(km);
        kurulum.setItemMeta(kurulum_meta);
        inventory.setItem(7, kurulum);

        DatabaseManager databaseManager = new DatabaseManager(MagnesifyDungeons.get());
        databaseManager.CreateNewTriggerTypeDungeon(new_dungeon.get("new"), "Yok", "Yok", 1, 60, 3, location, 1);

    }

}
