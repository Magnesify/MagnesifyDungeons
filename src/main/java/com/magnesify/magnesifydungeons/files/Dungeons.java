package com.magnesify.magnesifydungeons.files;

import com.magnesify.magnesifydungeons.MagnesifyDungeons;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class Dungeons {
    private static FileConfiguration localeConfig;
    private static File localeFile;

    public void reload() {
        if (localeFile == null) {
            localeFile = new File(MagnesifyDungeons.get().getDataFolder(), "datas/dungeons.yml");
        }

        if (!localeFile.exists()) {
            MagnesifyDungeons.get().saveResource("datas/dungeons.yml", false);
        }

        localeConfig = YamlConfiguration.loadConfiguration(localeFile);
    }

    public void save() {
        try {
            localeConfig.save(localeFile);
        } catch (IOException e) {
            MagnesifyDungeons.get().getLogger().warning("Could not save datas/dungeons.yml!");
        }
    }

    public FileConfiguration get() {
        if (localeConfig == null) {
            reload();
        }
        return localeConfig;
    }

    public FileConfiguration getStatic() {
        if (localeConfig == null) {
            reload();
        }
        return localeConfig;
    }
}
