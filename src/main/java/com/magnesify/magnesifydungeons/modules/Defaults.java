package com.magnesify.magnesifydungeons.modules;

import com.magnesify.magnesifydungeons.files.JsonStorage;

import static com.magnesify.magnesifydungeons.MagnesifyDungeons.get;

public class Defaults {

    public static String name = get().getConfig().getString("settings.holders.name");
    public static String category = get().getConfig().getString("settings.holders.category");
    public static String boss_id = get().getConfig().getString("settings.holders.boss_id");
    public static String none = get().getConfig().getString("settings.holders.none");
    public static String playtime = get().getConfig().getString("settings.holders.play-time");
    public static String starttime = get().getConfig().getString("settings.holders.start-time");
    public static String level = get().getConfig().getString("settings.holders.level");

    public DungeonMainSpawn MainSpawn() {
        return new DungeonMainSpawn();
    }

    public static class DungeonMainSpawn {
        JsonStorage jsonStorage = new JsonStorage(get().getDataFolder()+"/datas/plugin_datas.json");
        public String world() {
            return (String) jsonStorage.getValue("spawn.world");
        }
        public double yaw() {
            return jsonStorage.getDoubleValue("spawn.yaw");
        }
        public double pitch() {
            return jsonStorage.getDoubleValue("spawn.pitch");
        }
        public double x() {
            return jsonStorage.getDoubleValue("spawn.x");
        }
        public double y() {
            return jsonStorage.getDoubleValue("spawn.y");
        }
        public double z() {
            return jsonStorage.getDoubleValue("spawn.z");
        }
    }

}
