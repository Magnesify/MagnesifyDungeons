package com.magnesify.magnesifydungeons.storage;

import com.magnesify.magnesifydungeons.files.JsonStorage;
import com.magnesify.magnesifydungeons.modules.managers.DatabaseManager;
import org.bukkit.entity.Player;
import org.json.JSONObject;

import static com.magnesify.magnesifydungeons.MagnesifyDungeons.get;


public class PlayerMethods {

    private Player player;

    public PlayerMethods(Player player){
        this.player = player;
    }

    public PlayerMethods(){}

    public void createPlayer() {
        JsonStorage players = new JsonStorage(get().getDataFolder() + "/datas/players.json");
        if(players.getValue("players." + player.getUniqueId().toString()) == null) {
            DatabaseManager databaseManager = new DatabaseManager(get());
            databaseManager.CreateNewStats(player);
            JSONObject players_config = new JSONObject();
            players_config.put("players." + player.getUniqueId().toString() + ".name", player.getName());
            players_config.put("players." + player.getUniqueId().toString() + ".point", 0);
            players_config.put("players." + player.getUniqueId().toString() + ".kill", 0);
            players_config.put("players." + player.getUniqueId().toString() + ".death", 0);
            players_config.put("players." + player.getUniqueId().toString() + ".last_dungeon", "Yok");
            players_config.put("players." + player.getUniqueId().toString() + ".last_boss", "Yok");
            players_config.put("players." + player.getUniqueId().toString() + ".in_dungeon", false);
            players_config.put("players." + player.getUniqueId().toString() + ".done", false);
            players.writeData(players_config);
        }
    }

    public boolean playerExists(Player player) {
        JsonStorage players = new JsonStorage(get().getDataFolder() + "/datas/players.json");
        return players.getValue("players." + player.getUniqueId().toString()) != null;
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
