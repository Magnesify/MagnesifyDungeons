package com.magnesify.magnesifydungeons.dungeon.types.challange.gui;

import com.magnesify.magnesifydungeons.boss.MagnesifyBoss;
import com.magnesify.magnesifydungeons.modules.managers.DatabaseManager;
import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import dev.lone.itemsadder.api.FontImages.TexturedInventoryWrapper;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

import static com.magnesify.magnesifydungeons.MagnesifyDungeons.get;
import static com.magnesify.magnesifydungeons.dungeon.entitys.DungeonPlayer.parseHexColors;

public class IAChallangeGuiLoader {

    private static TexturedInventoryWrapper inv = null;

    @Deprecated
    public static void openInventory(Player ent) {
        FontImageWrapper texture = new FontImageWrapper(get().getConfig().getString("settings.challange.custom-gui-texture"));
        if(texture.exists()) {
            inv = new TexturedInventoryWrapper(null, 54, parseHexColors(get().getConfig().getString("settings.challange.title")), texture);
            loadItems(ent);
            inv.showInventory(ent);
        } else {
            Bukkit.getConsoleSender().sendMessage(parseHexColors("<#4b8eff>[Magnesify Dungeons] &f'settings.challange.custom-gui-texture' parametresinde ayarlı olan menü itemsadderda mevcut değil."));
        }
    }
    @Deprecated
    public static void loadItems(Player player) {
        DatabaseManager databaseManager = new DatabaseManager(get());
        int i = 0;
        for(String ranks : databaseManager.getChallangeNames()) {
            String boss = databaseManager.getBoss(ranks);
            MagnesifyBoss magnesifyBoss = new MagnesifyBoss(boss);
            ItemStack itemStack = new ItemStack(Material.getMaterial(get().getConfig().getString("settings.challange.defaults.material")), 1);
            ItemMeta meta = itemStack.getItemMeta();
            meta.setDisplayName(parseHexColors(get().getConfig().getString("settings.challange.defaults.display").replace("#dungeon", ranks.replace("challange_", ""))));
            List<String> main_lores = get().getConfig().getStringList("settings.challange.defaults.lore");
            List<String> sub_lore = new ArrayList<>();
            for(int a = 0; a<main_lores.size();a++) {
                if(magnesifyBoss.exists()) {
                    sub_lore.add(parseHexColors(main_lores.get(a).replace("#boss_name", magnesifyBoss.name())
                            .replace("#boss_health", String.valueOf(magnesifyBoss.health()))
                            .replace("#play_time", String.valueOf(databaseManager.getPlayTime(ranks)))));
                }
            }
            meta.setLore(sub_lore);
            itemStack.setItemMeta(meta);
            inv.getInternal().setItem(i, itemStack);
            i++;
        }

    }

}