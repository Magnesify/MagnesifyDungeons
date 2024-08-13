package com.magnesify.magnesifydungeons.modules.managers;

import com.magnesify.magnesifydungeons.dungeon.entitys.DungeonPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import static com.magnesify.magnesifydungeons.MagnesifyDungeons.get;
import static com.magnesify.magnesifydungeons.dungeon.types.trigger.commands.TriggerTypeDungeon.new_dungeon;
import static com.magnesify.magnesifydungeons.dungeon.types.trigger.events.TriggerSetupEvents.setupDataHolder;
import static com.magnesify.magnesifydungeons.modules.Defaults.TEXT_PREFIX;
import static org.bukkit.Bukkit.getLogger;

public class DungeonContentManager {

    public static HashMap<UUID, Boolean> dungeonChestCreation = new HashMap<>();

    public DungeonContentManager() {}

    public void updateChestContents(Location location) {
        Chest chest = (Chest) Bukkit.getWorld(location.getWorld().getName()).getBlockAt((int)location.getX(), (int)location.getY(), (int)location.getZ()).getState();
        if (chest != null) {
            chest.getInventory().clear();
            for(String item : get().getConfig().getStringList("settings.chest-contents")) {
                String[] spl = item.split(":");
                ItemStack a = new ItemStack(Material.getMaterial(spl[0]), Integer.parseInt(spl[1]));
                Random random = new Random();
                double randomNumber = random.nextDouble() * 100;
                if (Integer.parseInt(spl[2]) > randomNumber) {
                    chest.getInventory().addItem(a);
                }
            }
            getLogger().info("Chest at (" + location.getX() + ", " + location.getY() + ", " + location.getZ() + ") has been updated!");
        } else {
            getLogger().info("Chest no found at (" + location.getX() + ", " + location.getY() + ", " + location.getZ() + ")");
        }
    }

    public void SetupDungeonChests(String dungeon) {
        DatabaseManager databaseManager = new DatabaseManager(get());
        if(databaseManager.getChestSize(dungeon) > 0) {
            for(int i = 1; i<=databaseManager.getChestSize(dungeon); i++) {
                Location location = databaseManager.getChestLocationByID(i, dungeon);
                updateChestContents(location);
            }
        }
    }

    @Deprecated
    public void CreateNewDungeonChest(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        DungeonPlayer dungeonPlayer = new DungeonPlayer(player);
        if(dungeonChestCreation.get(player.getUniqueId()) != null) {
            if(event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
                DatabaseManager databaseManager = new DatabaseManager(get());
                event.getClickedBlock().setType(Material.CHEST);
                event.setCancelled(true);
                if (setupDataHolder.get("chest_level") == null) {
                    setupDataHolder.put("chest_level", 1);
                    databaseManager.CreateNewChest(new_dungeon.get("chestdata"), event.getClickedBlock().getLocation(), setupDataHolder.get("chest_level"));
                } else {
                    setupDataHolder.put("chest_level", setupDataHolder.get("chest_level") + 1);
                    databaseManager.CreateNewChest(new_dungeon.get("chestdata"), event.getClickedBlock().getLocation(), setupDataHolder.get("chest_level"));
                }
                dungeonPlayer.messageManager().chat(TEXT_PREFIX + " " + new_dungeon.get("chestdata") + " zindanı için " + setupDataHolder.get("chest_level") + ". sandık oluşturuldu !");
            } else {
                event.setCancelled(true);
            }
        }
    }


}
