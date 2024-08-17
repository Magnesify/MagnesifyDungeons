package com.magnesify.magnesifydungeons.storage;

import com.magnesify.magnesifydungeons.modules.managers.DatabaseManager;
import com.magnesify.magnesifydungeons.modules.managers.PlayerManager;
import org.bukkit.entity.Player;

import static com.magnesify.magnesifydungeons.MagnesifyDungeons.get;


public class PlayerMethods {

    private Player player;

    public PlayerMethods(Player player){
        this.player = player;
    }

    public PlayerMethods(){}

    public void createPlayer() {
        PlayerManager playerManager = new PlayerManager(player);
        if(playerManager.CreatePlayer()) {
            DatabaseManager databaseManager = new DatabaseManager(get());
            databaseManager.CreateNewStats(player);
        }
    }

    public boolean playerExists(Player player) {
        DatabaseManager databaseManager = new DatabaseManager(get());
        return databaseManager.users().isExists(player.getUniqueId().toString());
    }
    public boolean inDungeon(Player player) {
        DatabaseManager databaseManager = new DatabaseManager(get());
        return databaseManager.users().getInDungeon(player.getUniqueId().toString());
    }

    public int getPoints(Player player) {
        DatabaseManager databaseManager = new DatabaseManager(get());
        return databaseManager.users().getPoint(player.getUniqueId().toString());
    }


    public int getKill(Player player) {
        DatabaseManager databaseManager = new DatabaseManager(get());
        return databaseManager.stats().getKill(player.getUniqueId().toString());
    }


    public int getDeath(Player player) {
        DatabaseManager databaseManager = new DatabaseManager(get());
        return databaseManager.stats().getDeath(player.getUniqueId().toString());
    }

    public void updatePoint(Player player, int dungeon){
        DatabaseManager databaseManager = new DatabaseManager(get());
        databaseManager.users().setPoint(player.getUniqueId().toString(), databaseManager.users().getPoint(player.getUniqueId().toString())+dungeon);
    }



    public void removePoint(Player player, int dungeon){
        DatabaseManager databaseManager = new DatabaseManager(get());
        databaseManager.users().setPoint(player.getUniqueId().toString(), databaseManager.users().getPoint(player.getUniqueId().toString())-dungeon);
    }


    public void setPoint(Player player, int dungeon){
        DatabaseManager databaseManager = new DatabaseManager(get());
        databaseManager.users().setPoint(player.getUniqueId().toString(), dungeon);
    }

    public void resetPoint(Player player){
        DatabaseManager databaseManager = new DatabaseManager(get());
        databaseManager.users().setPoint(player.getUniqueId().toString(), 0);
    }

    public void updateDeath(Player player, int dungeon){
        DatabaseManager databaseManager = new DatabaseManager(get());
        databaseManager.stats().setDeath(player.getUniqueId().toString(), getDeath(player)+dungeon);
    }

    public void updateKill(Player player, int dungeon){
        DatabaseManager databaseManager = new DatabaseManager(get());
        databaseManager.stats().setDeath(player.getUniqueId().toString(), getKill(player)+dungeon);
    }


    public void updateDungeonStatus(Player player, boolean dungeon){
        DatabaseManager databaseManager = new DatabaseManager(get());
        databaseManager.users().setInDungeon(player.getUniqueId().toString(), dungeon);
    }

    public boolean getDungeon(Player player)  {
        DatabaseManager databaseManager = new DatabaseManager(get());
        return databaseManager.users().getInDungeon(player.getUniqueId().toString());
    }

    public boolean getDone(Player player)  {
        DatabaseManager databaseManager = new DatabaseManager(get());
        return databaseManager.users().getDone(player.getUniqueId().toString());
    }

    public void updateLastDungeon(Player player, String dungeon){
        DatabaseManager databaseManager = new DatabaseManager(get());
        databaseManager.users().setLastDungeon(player.getUniqueId().toString(), dungeon);
    }

    public void setDone(Player player, boolean dungeon){
        DatabaseManager databaseManager = new DatabaseManager(get());
        databaseManager.users().setDone(player.getUniqueId().toString(), dungeon);
    }

    public String getLastDungeon(Player player)  {
        DatabaseManager databaseManager = new DatabaseManager(get());
        return databaseManager.users().getLastDungeon(player.getUniqueId().toString());
    }

    public void updateLastBoss(Player player, String dungeon) {
        DatabaseManager databaseManager = new DatabaseManager(get());
        databaseManager.users().setLastBoss(player.getUniqueId().toString(), dungeon);
    }

    public String getLastBoss(Player player)  {
        DatabaseManager databaseManager = new DatabaseManager(get());
        return databaseManager.users().getLastBoss(player.getUniqueId().toString());
    }
}
