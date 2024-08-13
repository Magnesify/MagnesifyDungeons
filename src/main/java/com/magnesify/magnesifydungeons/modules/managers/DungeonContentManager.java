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
import java.util.UUID;

import static com.magnesify.magnesifydungeons.MagnesifyDungeons.get;
import static com.magnesify.magnesifydungeons.dungeon.types.trigger.commands.TriggerTypeDungeon.new_dungeon;
import static com.magnesify.magnesifydungeons.dungeon.types.trigger.events.TriggerSetupEvents.setupDataHolder;
import static com.magnesify.magnesifydungeons.modules.Defaults.TEXT_PREFIX;
import static org.bukkit.Bukkit.getLogger;

public class DungeonContentManager {

    public static HashMap<UUID, Boolean> dungeonChestCreation = new HashMap<>();

    public DungeonContentManager() {}

    private void updateChestContents(Location location) {
        if (Bukkit.getWorld("world") != null) {
            Chest chest = (Chest) Bukkit.getWorld(get().getConfig().getString("settings.default.dungeon-world")).getBlockAt((int)location.getX(), (int)location.getY(), (int)location.getZ()).getState();
            if (chest != null) {
                ItemStack[] items = new ItemStack[] {
                        new ItemStack(Material.DIAMOND, 5),
                        new ItemStack(Material.IRON_INGOT, 10)
                };
                chest.getInventory().setContents(items);
                getLogger().info("Chest at (" + location.getX() + ", " + location.getY() + ", " + location.getZ() + ") has been updated!");
            } else {
                getLogger().info("Chest no found at (" + location.getX() + ", " + location.getY() + ", " + location.getZ() + ")");
            }
        } else {
            getLogger().warning("World not found!");
        }
    }

    public void SetupDungeonChests(String dungeon) {
        DatabaseManager databaseManager = new DatabaseManager(get());
        for(int i = 0; i<databaseManager.getChestSize(dungeon); i++) {
            Location location = databaseManager.getChestLocationByID(i, dungeon);
            updateChestContents(location); // burada kaldın, test etmedin.
        }
    }

    public void CreateNewDungeonChest(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        DungeonPlayer dungeonPlayer = new DungeonPlayer(player);
        if(dungeonChestCreation.get(player.getUniqueId()) != null) {
            if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                if(event.getClickedBlock().getType() == Material.CHEST) {
                    DatabaseManager databaseManager = new DatabaseManager(get());
                    if(setupDataHolder.get("chest_level") == null) {
                        setupDataHolder.put("chest_level", 1);
                        databaseManager.CreateNewChest(new_dungeon.get("new"), event.getClickedBlock().getLocation());
                    } else {
                        setupDataHolder.put("chest_level", setupDataHolder.get("chest_level") + 1);
                        databaseManager.CreateNewChest(new_dungeon.get("new"), event.getClickedBlock().getLocation());
                    }
                    dungeonPlayer.messageManager().chat(TEXT_PREFIX + " " + new_dungeon.get("new") + " zindanı için "+ setupDataHolder.get("chest_level")+". sandık oluşturuldu !");
                } else {
                    dungeonPlayer.messageManager().chat(TEXT_PREFIX + " Aktif olarak yanlızca sandık bloğuna destek verilmektedir. Lütfen sandık bloğuna sağ tıklayınız.");
                }
            } else {
                event.setCancelled(true);
            }
        }
    }


}
