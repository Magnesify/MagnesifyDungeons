package com.magnesify.magnesifydungeons.events;

import com.magnesify.magnesifydungeons.MagnesifyDungeons;
import com.magnesify.magnesifydungeons.dungeon.Dungeon;
import com.magnesify.magnesifydungeons.dungeon.entitys.DungeonPlayer;
import com.magnesify.magnesifydungeons.modules.DatabaseManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.*;

import static com.magnesify.magnesifydungeons.MagnesifyDungeons.get;
import static com.magnesify.magnesifydungeons.dungeon.entitys.DungeonPlayer.parseHexColors;

public class DungeonPlayerEvents implements Listener {
    public DungeonPlayerEvents(MagnesifyDungeons magnesifyDungeons) {}

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        DungeonPlayer dungeonPlayer = new DungeonPlayer(event.getPlayer());
        dungeonPlayer.createDungeonAccount(event);
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard board = manager.getNewScoreboard();
        Objective objective = board.registerNewObjective("Magnesify", "Dungeons", parseHexColors(get().getConfig().getString("settings.defaults.tag")));
        objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
        event.getPlayer().setScoreboard(board);
    }

    @EventHandler
    public void leave(PlayerQuitEvent event) {
        DungeonPlayer dungeonPlayer = new DungeonPlayer(event.getPlayer());
        if(dungeonPlayer.inDungeon()) {
            Dungeon dungeon = new Dungeon(get().getPlayers().getLastDungeon(event.getPlayer()));
            dungeon.status(true);
            dungeon.updateCurrentPlayer("None");
            get().getPlayers().setDone(event.getPlayer(), true);
            dungeon.events().stop(event.getPlayer());
            get().getPlayers().updateDungeonStatus(event.getPlayer(), false);
            get().getPlayers().updateDeath(event.getPlayer(), 1);
            killEntity(get().getPlayers().getLastBoss(event.getPlayer()));
        }
    }

    public void killEntity(String lastBoss) {
        for (Entity entity : Bukkit.getWorld(get().getConfig().getString("settings.defaults.dungeon-world")).getEntities()) {
            if (entity instanceof Zombie) {
                String key = entity.getMetadata("name").get(0).asString();
                String metadataValue = entity.getMetadata("dungeon").get(0).asString();
                Zombie zombie = (Zombie) entity;
                DatabaseManager databaseManager = new DatabaseManager(get());
                if(databaseManager.isDungeonExists(metadataValue) && databaseManager.getCurrentPlayer(metadataValue).equalsIgnoreCase("Yok")) {
                    if(metadataValue != null) {
                        if (lastBoss.equalsIgnoreCase(key)) {
                            if (entity.hasMetadata("name")) {
                                zombie.remove();
                            }
                        }
                    }
                }
            }
        }
    }

}
