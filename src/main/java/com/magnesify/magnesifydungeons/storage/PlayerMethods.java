package com.magnesify.magnesifydungeons.storage;

import com.magnesify.magnesifydungeons.files.Players;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;


public class PlayerMethods {

    private Player player;

    public PlayerMethods(Player player){
        this.player = player;
    }

    public PlayerMethods(){}

    public void createPlayer() {
        Players players = new Players();
        if(players.get().getString("players." + player.getUniqueId().toString()) == null) {
            players.get().set("players." + player.getUniqueId().toString() + ".name", player.getName());
            players.get().set("players." + player.getUniqueId().toString() + ".point", 0);
            players.get().set("players." + player.getUniqueId().toString() + ".kill", 0);
            players.get().set("players." + player.getUniqueId().toString() + ".death", 0);
            players.get().set("players." + player.getUniqueId().toString() + ".last-dungeon", "None");
            players.get().set("players." + player.getUniqueId().toString() + ".last-boss", "None");
            players.get().set("players." + player.getUniqueId().toString() + ".dungeons", "None");
            players.get().set("players." + player.getUniqueId().toString() + ".in-dungeon", false);
            players.get().set("players." + player.getUniqueId().toString() + ".done", false);
            players.save();
        }
    }

    public boolean playerExists(Player player) {
        Players players = new Players();
        return players.get().getString("players." + player.getUniqueId().toString()) == null;
    }

    public int getPoints(Player player) {
        Players players = new Players();
        return players.get().getInt("players." + player.getUniqueId().toString() + ".point");
    }


    public int getKill(Player player) {
        Players players = new Players();
        return players.get().getInt("players." + player.getUniqueId().toString() + ".point");
    }


    public int getDeath(Player player) {
        Players players = new Players();
        return players.get().getInt("players." + player.getUniqueId().toString() + ".death");
    }

    public void updatePoint(Player player, int dungeon){
        if (playerExists(player)){
            createPlayer();
        }
        Players players = new Players();
        players.get().set("players." + player.getUniqueId().toString() + ".point", dungeon+getPoints(player));
        players.save();
    }

    public void updateDeath(Player player, int dungeon){
        if (playerExists(player)){
            createPlayer();
        }
        Players players = new Players();
        players.get().set("players." + player.getUniqueId().toString() + ".death", dungeon+getDeath(player));
        players.save();
    }

    public void updateKill(Player player, int dungeon){
        if (playerExists(player)){
            createPlayer();
        }
        Players players = new Players();
        players.get().set("players." + player.getUniqueId().toString() + ".kill", dungeon+getKill(player));
        players.save();
    }


    public void updateDungeonStatus(Player player, boolean dungeon){
        if (playerExists(player)){
            createPlayer();
        }
        Players players = new Players();
        players.get().set("players." + player.getUniqueId().toString() + ".in-dungeon", dungeon);
        players.save();
    }

    public boolean getDungeon(Player player)  {
        Players players = new Players();
        return players.get().getBoolean("players." + player.getUniqueId().toString() + ".in-dungeon");
    }

    public boolean getDone(Player player)  {
        Players players = new Players();
        return players.get().getBoolean("players." + player.getUniqueId().toString() + ".done");
    }

    public void updateLastDungeon(Player player, String dungeon){
        if (playerExists(player)){
            createPlayer();
        }
        Players players = new Players();
        players.get().set("players." + player.getUniqueId().toString() + ".last-dungeon", dungeon);
        players.save();
    }

    public void setDone(Player player, boolean dungeon){
        if (playerExists(player)){
            createPlayer();
        }
        Players players = new Players();
        players.get().set("players." + player.getUniqueId().toString() + ".done", dungeon);
        players.save();
    }

    public String getLastDungeon(Player player)  {
        Players players = new Players();
        return players.get().getString("players." + player.getUniqueId().toString() + ".last-dungeon");
    }

    public void updateLastBoss(Player player, String dungeon) {
        if (playerExists(player)){
            createPlayer();
        }
        Players players = new Players();
        players.get().set("players." + player.getUniqueId().toString() + ".last-boss", dungeon);
        players.save();
    }

    public String getLastBoss(Player player)  {
        Players players = new Players();
        return players.get().getString("players." + player.getUniqueId().toString() + ".last-boss");
    }
}
