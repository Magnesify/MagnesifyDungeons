package com.magnesify.magnesifydungeons.modules;

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
        public String world() {
            return get().getConfig().getString("settings.main-spawn.world");
        }
        public double yaw() {
            return get().getConfig().getDouble("settings.main-spawn.yaw");
        }
        public double pitch() {
            return get().getConfig().getDouble("settings.main-spawn.pitch");
        }
        public int x() {
            return get().getConfig().getInt("settings.main-spawn.x");
        }
        public int y() {
            return get().getConfig().getInt("settings.main-spawn.y");
        }
        public int z() {
            return get().getConfig().getInt("settings.main-spawn.z");
        }
    }

}
