package com.magnesify.magnesifydungeons.modules;

import com.magnesify.magnesifydungeons.files.JsonStorage;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.magnesify.magnesifydungeons.MagnesifyDungeons.get;

public class StatsManager {

    Map<String, Integer> scores = new HashMap<>();
    public StatsManager() {}

    public boolean add(String name, int i) {
        if (!scores.containsKey(name)) {
            scores.put(name, i);
            return true;
        }
        return false;
    }

    public void updateKill(String name, int i) {
        JsonStorage stats = new JsonStorage(get().getDataFolder() + "/caches/statistics.json");
        stats.updateData(name + ".kill", i+getKill(name));
    }

    public void updateDeath(String name, int i) {
        JsonStorage stats = new JsonStorage(get().getDataFolder() + "/caches/statistics.json");
        stats.updateData(name + ".death", i+getDeath(name));
    }

    public void updateLose(String name, int i) {
        JsonStorage stats = new JsonStorage(get().getDataFolder() + "/caches/statistics.json");
        stats.updateData(name + ".lose", i+getLose(name));
    }

    public void updateWin(String name, int i) {
        JsonStorage stats = new JsonStorage(get().getDataFolder() + "/caches/statistics.json");
        stats.updateData(name + ".win", i+getWin(name));
    }

    public void updateMatch(String name, int i) {
        JsonStorage stats = new JsonStorage(get().getDataFolder() + "/caches/statistics.json");
        stats.updateData(name + ".played_match", i+getMatches(name));
    }

    public int getKill(String name) {
        JsonStorage stats = new JsonStorage(get().getDataFolder() + "/caches/statistics.json");
        return (int) stats.getValue(name + ".kill");
    }

    public int getDeath(String name) {
        JsonStorage stats = new JsonStorage(get().getDataFolder() + "/caches/statistics.json");
        return (int) stats.getValue(name + ".death");
    }

    public int getWin(String name) {
        JsonStorage stats = new JsonStorage(get().getDataFolder() + "/caches/statistics.json");
        return (int) stats.getValue(name + ".win");
    }

    public int getLose(String name) {
        JsonStorage stats = new JsonStorage(get().getDataFolder() + "/caches/statistics.json");
        return (int) stats.getValue(name + ".lose");
    }

    public int getPlayer(String name) {
        JsonStorage stats = new JsonStorage(get().getDataFolder() + "/caches/statistics.json");
        return (int) stats.getValue(name + ".name");
    }


    public int getMatches(String name) {
        JsonStorage stats = new JsonStorage(get().getDataFolder() + "/caches/statistics.json");
        return (int) stats.getValue(name + ".played_match");
    }

    public boolean remove(String name) {
        if (scores.containsKey(name)) {
            scores.remove(name);
            return true;
        }
        return false;
    }

    public void create(String name) {
        JsonStorage stats = new JsonStorage(get().getDataFolder() + "/caches/statistics.json");

        if(stats.getValue(name) == null) {
            JSONObject players_config = new JSONObject();
            players_config.put(stats.readData().length()+1 + ".name", name);
            players_config.put(stats.readData().length()+1 + ".kill", 0);
            players_config.put(stats.readData().length()+1 + ".death", 0);
            players_config.put(stats.readData().length()+1 + ".played_match", 0);
            players_config.put(stats.readData().length()+1 + ".win", 0);
            players_config.put(stats.readData().length()+1 + ".lose", 0);
            stats.writeData(players_config);
        }
    }

    public Map.Entry<String, Integer> sortKill() {
        JsonStorage stats = new JsonStorage(get().getDataFolder() + "/caches/statistics.json");
        for(int i = 0; i<stats.readData().length();i++) {
            add((String) stats.getValue(i+".name"), (Integer) stats.getValue(i+".kill"));
            List<Map.Entry<String, Integer>> entryList = new ArrayList<>(scores.entrySet());
            entryList.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));
            for (int a = 0; a < 3 && a < entryList.size(); a++) {
                Map.Entry<String, Integer> entry = entryList.get(a);
                // System.out.println((i + 1) + ". " + entry.getKey() + " - " + entry.getValue());
                return entry;
            }
        }
        return null;
    }

}
