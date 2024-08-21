package com.magnesify.magnesifydungeons.dungeon.types.trigger.commands.gui;

import com.magnesify.magnesifydungeons.boss.MagnesifyBoss;
import com.magnesify.magnesifydungeons.modules.managers.DatabaseManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

import static com.magnesify.magnesifydungeons.MagnesifyDungeons.get;
import static com.magnesify.magnesifydungeons.dungeon.entitys.DungeonPlayer.parseHexColors;
import static dev.lone.itemsadder.api.ItemsAdder.getCustomItem;

public class TriggerGuiLoader {

    private static Inventory inv = null;

    @Deprecated
    public static void openInventory(Player ent) {
        inv = Bukkit.createInventory(null,  54, parseHexColors(get().getConfig().getString("settings.trigger-type.dungeon-list.title")));
        loadItems(ent);
        ent.openInventory(inv);
    }

    @Deprecated
    public static void loadItems(Player player) {
        DatabaseManager databaseManager = new DatabaseManager(get());
        int i = 0;
        for(String ranks : databaseManager.TriggerTypeDungeons().getAllDungeons()) {
            String boss = databaseManager.getBoss(databaseManager.TriggerTypeDungeons().getBoss(ranks));
            MagnesifyBoss magnesifyBoss = new MagnesifyBoss(boss);
            boolean is_material_set = get().getConfig().isSet("settings.trigger-type.dungeon-list.defaults.material");
            boolean is_custom_material_set = get().getConfig().isSet("settings.trigger-type.dungeon-list.defaults.custom-material");
            ItemStack itemStack = null;
            if(is_material_set) {
                itemStack = new ItemStack(Material.getMaterial(get().getConfig().getString("settings.trigger-type.dungeon-list.defaults.material")));
            } else if(is_custom_material_set) {
                itemStack = getCustomItem(get().getConfig().getString("settings.trigger-type.dungeon-list.defaults.custom-material"));
            } else {
                itemStack = new ItemStack(Material.PAPER);
            }
            ItemMeta meta = itemStack.getItemMeta();
            meta.setDisplayName(parseHexColors(get().getConfig().getString("settings.trigger-type.dungeon-list.defaults.display").replace("#dungeon", ranks.replace("challange_", ""))));
            List<String> main_lores = get().getConfig().getStringList("settings.trigger-type.dungeon-list.defaults.lore");
            List<String> sub_lore = new ArrayList<>();
            boolean stats= databaseManager.TriggerTypeDungeons().getStatus(ranks);
            for(int a = 0; a<main_lores.size();a++) {
                if(magnesifyBoss.exists()) {
                    sub_lore.add(parseHexColors(main_lores.get(a).replace("#boss_name", magnesifyBoss.name())
                            .replace("#boss_health", String.valueOf(magnesifyBoss.health()))
                                    .replace("#play_time", String.valueOf(databaseManager.getPlayTime(ranks)))
                            .replace("#status",stats == false ? get().getConfig().getString("settings.holders.full") : get().getConfig().getString("settings.holders.empty"))));
                }
            }
            meta.setLore(sub_lore);
            itemStack.setItemMeta(meta);
            inv.setItem(i, itemStack);
            i++;
        }

    }

}
