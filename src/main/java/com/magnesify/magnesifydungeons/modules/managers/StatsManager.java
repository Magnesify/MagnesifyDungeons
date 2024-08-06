package com.magnesify.magnesifydungeons.modules.managers;

import java.util.HashMap;
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
        DatabaseManager databaseManager = new DatabaseManager(get());
        databaseManager.stats().setKill(name, i);
    }

    public void updateDeath(String name, int i) {
        DatabaseManager databaseManager = new DatabaseManager(get());
        databaseManager.stats().setDeath(name, i);
    }

    public void updateLose(String name, int i) {
        DatabaseManager databaseManager = new DatabaseManager(get());
        databaseManager.stats().setLose(name, i);
    }

    public void updateWin(String name, int i) {
        DatabaseManager databaseManager = new DatabaseManager(get());
        databaseManager.stats().setWin(name, i);
    }

    public void updateMatch(String name, int i) {
        DatabaseManager databaseManager = new DatabaseManager(get());
        databaseManager.stats().setMatch(name, i);
    }

    public int getKill(String name) {
        DatabaseManager databaseManager = new DatabaseManager(get());
        return databaseManager.stats().getKill(name);
    }

    public int getDeath(String name) {
        DatabaseManager databaseManager = new DatabaseManager(get());
        return databaseManager.stats().getDeath(name);
    }

    public int getWin(String name) {
        DatabaseManager databaseManager = new DatabaseManager(get());
        return databaseManager.stats().getWin(name);
    }

    public int getLose(String name) {
        DatabaseManager databaseManager = new DatabaseManager(get());
        return databaseManager.stats().getLose(name);
    }

    public String getPlayer(String name) {
        DatabaseManager databaseManager = new DatabaseManager(get());
        return databaseManager.stats().getName(name);
    }


    public int getMatches(String name) {
        DatabaseManager databaseManager = new DatabaseManager(get());
        return databaseManager.stats().getMatch(name);
    }

    public boolean remove(String name) {
        if (scores.containsKey(name)) {
            scores.remove(name);
            return true;
        }
        return false;
    }


}
