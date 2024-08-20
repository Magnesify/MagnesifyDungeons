package com.magnesify.magnesifydungeons.modules;

import com.magnesify.magnesifydungeons.files.Options;

import static com.magnesify.magnesifydungeons.MagnesifyDungeons.get;

public class Defaults {

    public static String TEXT_PREFIX = "<#1a88fb>M<#317dfb>a<#4772fc>g<#5e67fc>n<#745dfc>e<#8b52fc>s<#a147fd>i<#b83cfd>f<#ce31fd>y &8▸&r";

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
            Options options = new Options();
            return options.get().getString("spawn.world");
        }
        public double yaw() {
            Options options = new Options();
            return options.get().getDouble("spawn.yaw");
        }
        public double pitch() {
            Options options = new Options();
            return options.get().getDouble("spawn.pitch");
        }
        public double x() {
            Options options = new Options();
            return options.get().getDouble("spawn.x");
        }
        public double y() {
            Options options = new Options();
            return options.get().getDouble("spawn.y");
        }
        public double z() {
            Options options = new Options();
            return options.get().getDouble("spawn.z");
        }
    }

}
