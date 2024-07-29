package com.magnesify.magnesifydungeons.hanapi;

import com.magnesify.magnesifydungeons.MagnesifyDungeons;
import com.magnesify.magnesifydungeons.dungeon.entitys.DungeonConsole;
import com.magnesify.magnesifydungeons.files.Dungeons;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;

import static com.magnesify.magnesifydungeons.MagnesifyDungeons.get;
import static com.magnesify.magnesifydungeons.dungeon.entitys.DungeonPlayer.parseHexColors;

public class GuiManager implements Listener {
    private Inventory inv;
    private int page = 1;
    private int currentPage;

    public GuiManager(MagnesifyDungeons magnesifyDungeons){}

    public GuiManager(Inventory inventory) {
        this.inv = inventory;
        this.currentPage = page;
        setPagingItems();
    }

    public void openCategory(Player player) {
        loadCategory();
        player.openInventory(inv);
    }

    public void openDungeons(Player player, String category) {
        loadDungeons(category);
        player.closeInventory();
        player.openInventory(inv);
    }

    public int getPage() {
        return page;
    }

    public boolean nextPageAvailable() {
        return currentPage + 1 <= page;
    }

    public boolean previousPageAvailable() {
        if(currentPage - 1 > 0) {
            if(currentPage - 1 < page) {
                return true;
            }
            return false;
        }
        return false;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int changePagingSlotsBySize() {
        // 0 -> 9
        // 1 -> 18
        // 2 -> 27
        // 3 -> 36
        // 4 -> 45
        // 5 -> 65
        switch (inv.getSize()) {
            case 9:
                return 0;
            case 18:
                return 1;
            case 27:
                return 2;
            case 36:
                return 3;
            case 45:
                return 4;
            case 54:
                return 5;
        }
        return 0;
    }

    public void setPagingItems() {
        String info = get().getConfig().getString("settings.menu.paging.info.display").replace("#current", String.valueOf(getCurrentPage())).replace("#max", String.valueOf(getPage()));
        String info_material = get().getConfig().getString("settings.menu.paging.info.material");
        ItemStack info_item = new ItemStack(Material.getMaterial(info_material), 1);
        ItemMeta info_meta = info_item.getItemMeta();
        info_meta.setDisplayName(parseHexColors(info));
        info_item.setItemMeta(info_meta);

        String previous = get().getConfig().getString("settings.menu.paging.previous.display");
        String previous_material = get().getConfig().getString("settings.menu.paging.previous.material");
        ItemStack prev_item = new ItemStack(Material.getMaterial(previous_material), 1);
        ItemMeta prev_meta = prev_item.getItemMeta();
        prev_meta.setDisplayName(parseHexColors(previous));
        prev_item.setItemMeta(prev_meta);

        String next = get().getConfig().getString("settings.menu.paging.next.display");
        String next_material = get().getConfig().getString("settings.menu.paging.next.material");
        ItemStack next_item = new ItemStack(Material.getMaterial(next_material), 1);
        ItemMeta next_meta = next_item.getItemMeta();
        next_meta.setDisplayName(parseHexColors(next));
        next_item.setItemMeta(next_meta);
        if(getPage() == 1) {
            switch (changePagingSlotsBySize()) {
                case 1:
                    if(nextPageAvailable()) inv.setItem(17, next_item);
                    inv.setItem(13, info_item);
                case 2:
                    if(nextPageAvailable()) inv.setItem(26, next_item);
                    inv.setItem(22, info_item);
                case 3:
                    if(nextPageAvailable()) inv.setItem(35, next_item);
                    inv.setItem(21, info_item);
                case 4:
                    if(nextPageAvailable()) inv.setItem(44, next_item);
                    inv.setItem(40, info_item);
                case 5:
                    if(nextPageAvailable()) inv.setItem(53, next_item);
                    inv.setItem(49, info_item);
            }
        } else if (getPage() > 1) {
            switch (changePagingSlotsBySize()) {
                case 1:
                    inv.setItem(17, next_item);
                    inv.setItem(13, info_item);
                    inv.setItem(9, prev_item);
                case 2:
                    inv.setItem(26, next_item);
                    inv.setItem(22, info_item);
                    inv.setItem(18, prev_item);
                case 3:
                    inv.setItem(35, next_item);
                    inv.setItem(21, info_item);
                    inv.setItem(27, prev_item);
                case 4:
                    inv.setItem(44, next_item);
                    inv.setItem(36, prev_item);
                    inv.setItem(40, info_item);
                case 5:
                    inv.setItem(53, next_item);
                    inv.setItem(45, prev_item);
                    inv.setItem(49, info_item);
            }
        }
    }

    @Deprecated
    public void loadCategory() {
        Dungeons dungeons = new Dungeons();
        int i = 0;
        if(!dungeons.get().getConfigurationSection("dungeons").getKeys(false).isEmpty() && dungeons.get().getConfigurationSection("dungeons") != null) {
            for (String a : dungeons.get().getConfigurationSection("dungeons").getKeys(false)) {
                String category = dungeons.get().getString("dungeons." + a + ".category");
                ItemStack item = new ItemStack(Material.BOOK, 1);
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(parseHexColors("&bKategori: <#4f91fc>" + category));
                item.setItemMeta(meta);
                item.setItemMeta(meta);
                inv.setItem(i, item);
                i++;
                if(i >= inv.getSize()) {
                    page++;
                    i=0;
                }

            }
        } else {
            DungeonConsole dungeonConsole = new DungeonConsole();
            dungeonConsole.ConsoleOutputManager().write("<#4f91fc>[Magnesify Dungeons] &fKategori menüsü boş, işlem iptal edildi.");
        }

    }


    @Deprecated
    public void loadDungeons(String input_category) {
        Dungeons dungeons = new Dungeons();

        int i = 0;

        String previous = get().getConfig().getString("settings.menu.paging.previous.display");
        String previous_material = get().getConfig().getString("settings.menu.paging.previous.material");
        ItemStack prev_item = new ItemStack(Material.getMaterial(previous_material), 1);
        ItemMeta prev_meta = prev_item.getItemMeta();
        prev_meta.setDisplayName(parseHexColors(previous));
        prev_item.setItemMeta(prev_meta);

        switch (changePagingSlotsBySize()) {
            case 1:
                inv.setItem(9, prev_item);
            case 2:
                inv.setItem(18, prev_item);
            case 3:
                inv.setItem(27, prev_item);
            case 4:
                inv.setItem(36, prev_item);
            case 5:
                inv.setItem(45, prev_item);
        }

        if(!dungeons.get().getConfigurationSection("dungeons").getKeys(false).isEmpty()) {
            for (String a : dungeons.get().getConfigurationSection("dungeons").getKeys(false)) {
                String category = dungeons.get().getString("dungeons." + a + ".category");
                if(category.equalsIgnoreCase(input_category)) {
                    String name = dungeons.get().getString("dungeons." + a + ".name");
                    ItemStack item = new ItemStack(Material.BOOK, 1);
                    ItemMeta meta = item.getItemMeta();
                    meta.setDisplayName(parseHexColors("&bZindan: <#4f91fc>" + name));
                    item.setItemMeta(meta);
                    item.setItemMeta(meta);
                    inv.setItem(i, item);
                    i++;
                    if(i >= inv.getSize()) {
                        page++;
                        i=0;
                    }
                }

            }
        } else {
            DungeonConsole dungeonConsole = new DungeonConsole();
            dungeonConsole.ConsoleOutputManager().write("<#4f91fc>[Magnesify Dungeons] &fKategori menüsü boş, işlem iptal edildi.");
        }

    }


}
