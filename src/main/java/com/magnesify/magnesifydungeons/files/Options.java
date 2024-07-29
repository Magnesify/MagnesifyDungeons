package com.magnesify.magnesifydungeons.files;

import com.magnesify.magnesifydungeons.MagnesifyDungeons;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class Options {
    private FileConfiguration localeConfig;
    private File localeFile;

    public void reload() {
        if (localeFile == null) {
            localeFile = new File(MagnesifyDungeons.get().getDataFolder(), "options.yml");
        }

        if (!localeFile.exists()) {
            MagnesifyDungeons.get().saveResource("options.yml", false);
        }

        localeConfig = YamlConfiguration.loadConfiguration(localeFile);
    }

    public void save() {
        try {
            localeConfig.save(localeFile);
        } catch (IOException e) {
            MagnesifyDungeons.get().getLogger().warning("Could not save options.yml!");
        }
    }

    public FileConfiguration get() {
        if (localeConfig == null) {
            reload();
        }
        return localeConfig;
    }
}
