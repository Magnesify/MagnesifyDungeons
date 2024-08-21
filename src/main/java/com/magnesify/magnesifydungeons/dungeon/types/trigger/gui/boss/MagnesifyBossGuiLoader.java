package com.magnesify.magnesifydungeons.dungeon.types.trigger.gui.boss;

import com.magnesify.magnesifydungeons.modules.managers.DatabaseManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import static com.magnesify.magnesifydungeons.MagnesifyDungeons.get;
import static com.magnesify.magnesifydungeons.dungeon.entitys.DungeonPlayer.parseHexColors;
import static com.magnesify.magnesifydungeons.dungeon.types.trigger.commands.TriggerTypeDungeon.new_dungeon;

public class MagnesifyBossGuiLoader {

    private static Inventory inv = null;

    @Deprecated
    public static void openInventory(Player ent, String magnesifyBoss) {
        inv = Bukkit.createInventory(null,  54, parseHexColors(get().getConfig().getString("settings.trigger-type.boss-list.title")));
        loadItems(magnesifyBoss);
        ent.openInventory(inv);
    }


    @Deprecated
    public static void loadItems(String name) {
        for (int i = 1; i<=new DatabaseManager(get()).TriggerTypeDungeons().getTotalCheckpoints(name);i++) {
            ItemStack item = new ItemStack(Material.PAPER);
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setDisplayName(parseHexColors("&d#" + i + " &8| &bBoss: &d" + new DatabaseManager(get()).TriggerTypeDungeons().getCheckpointBoss(new_dungeon.get("dungeon_name"), i)));
            item.setItemMeta(itemMeta);
            inv.setItem(i-1, item);
        }
    }
}
