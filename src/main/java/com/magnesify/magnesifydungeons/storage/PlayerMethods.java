package com.magnesify.magnesifydungeons.storage;

import com.magnesify.magnesifydungeons.files.JsonStorage;
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

    // Burada kaldın, Oyuncuları SQLite a taşıyorsun.

    public boolean playerExists(Player player) {
        DatabaseManager databaseManager = new DatabaseManager(get());
        return databaseManager.users().isExists(player.getUniqueId().toString());
    }
    public boolean inDungeon(Player player) {
        JsonStorage players = new JsonStorage(get().getDataFolder() + "/datas/players.json");
        return (boolean) players.getValue("players." + player.getUniqueId().toString() + ".in_dungeon");
    }

    public int getPoints(Player player) {
        JsonStorage players = new JsonStorage(get().getDataFolder() + "/datas/players.json");
        return (int) players.getValue("players." + player.getUniqueId().toString() + ".point");
    }


    public int getKill(Player player) {
        JsonStorage players = new JsonStorage(get().getDataFolder() + "/datas/players.json");
        return (int) players.getValue("players." + player.getUniqueId().toString() + ".kill");
    }


    public int getDeath(Player player) {
        JsonStorage players = new JsonStorage(get().getDataFolder() + "/datas/players.json");
        return (int) players.getValue("players." + player.getUniqueId().toString() + ".death");
    }

    public void updatePoint(Player player, int dungeon){
        JsonStorage players = new JsonStorage(get().getDataFolder() + "/datas/players.json");
        if (playerExists(player)){
            createPlayer();
        }
        players.updateData("players." + player.getUniqueId().toString() + ".point", dungeon+getPoints(player));
    }



    public void removePoint(Player player, int dungeon){
        JsonStorage players = new JsonStorage(get().getDataFolder() + "/datas/players.json");
        if (playerExists(player)){
            createPlayer();
        }
        players.updateData("players." + player.getUniqueId().toString() + ".point", getPoints(player)-dungeon);
    }


    public void setPoint(Player player, int dungeon){
        JsonStorage players = new JsonStorage(get().getDataFolder() + "/datas/players.json");
        if (playerExists(player)){
            createPlayer();
        }
        players.updateData("players." + player.getUniqueId().toString() + ".point", dungeon);
    }

    public void resetPoint(Player player){
        JsonStorage players = new JsonStorage(get().getDataFolder() + "/datas/players.json");
        if (playerExists(player)){
            createPlayer();
        }
        players.updateData("players." + player.getUniqueId().toString() + ".point", 0);
    }

    public void updateDeath(Player player, int dungeon){
        JsonStorage players = new JsonStorage(get().getDataFolder() + "/datas/players.json");
        if (playerExists(player)){
            createPlayer();
        }
        players.updateData("players." + player.getUniqueId().toString() + ".death", dungeon+getDeath(player));
    }

    public void updateKill(Player player, int dungeon){
        JsonStorage players = new JsonStorage(get().getDataFolder() + "/datas/players.json");
        if (playerExists(player)){
            createPlayer();
        }
        players.updateData("players." + player.getUniqueId().toString() + ".kill", dungeon+getKill(player));
    }


    public void updateDungeonStatus(Player player, boolean dungeon){
        JsonStorage players = new JsonStorage(get().getDataFolder() + "/datas/players.json");
        if (playerExists(player)){
            createPlayer();
        }
        players.updateData("players." + player.getUniqueId().toString() + ".in_dungeon", dungeon);
    }

    public boolean getDungeon(Player player)  {
        JsonStorage players = new JsonStorage(get().getDataFolder() + "/datas/players.json");
        return (boolean) players.getValue("players." + player.getUniqueId().toString() + ".in_dungeon");
    }

    public boolean getDone(Player player)  {
        JsonStorage players = new JsonStorage(get().getDataFolder() + "/datas/players.json");
        return (boolean) players.getValue("players." + player.getUniqueId().toString() + ".done");
    }

    public void updateLastDungeon(Player player, String dungeon){
        JsonStorage players = new JsonStorage(get().getDataFolder() + "/datas/players.json");
        if (playerExists(player)){
            createPlayer();
        }
        players.updateData("players." + player.getUniqueId().toString() + ".last_dungeon", dungeon);
    }

    public void setDone(Player player, boolean dungeon){
        JsonStorage players = new JsonStorage(get().getDataFolder() + "/datas/players.json");
        if (playerExists(player)){
            createPlayer();
        }
        players.updateData("players." + player.getUniqueId().toString() + ".done", dungeon);
    }

    public String getLastDungeon(Player player)  {
        JsonStorage players = new JsonStorage(get().getDataFolder() + "/datas/players.json");
        return (String) players.getValue("players." + player.getUniqueId().toString() + ".last_dungeon");
    }

    public void updateLastBoss(Player player, String dungeon) {
        JsonStorage players = new JsonStorage(get().getDataFolder() + "/datas/players.json");
        if (playerExists(player)){
            createPlayer();
        }
        players.updateData("players." + player.getUniqueId().toString() + ".last_boss", dungeon);
    }

    public String getLastBoss(Player player)  {
        JsonStorage players = new JsonStorage(get().getDataFolder() + "/datas/players.json");
        return (String) players.getValue("players." + player.getUniqueId().toString() + ".last_boss");
    }
}
