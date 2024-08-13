package com.magnesify.magnesifydungeons.files;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

import static com.magnesify.magnesifydungeons.MagnesifyDungeons.get;

public class GenusFile {
    private FileConfiguration kitsConfig = null;
    private File kitsFile = null;

    public void createGenusConfig() {
        if (kitsFile == null) {
            kitsFile = new File(get().getDataFolder(), "genus.yml");
        }
        if (!kitsFile.exists()) {
            get().saveResource("genus.yml", false);
        }
        kitsConfig = YamlConfiguration.loadConfiguration(kitsFile);
    }

    public FileConfiguration getGenusConfig() {
        if (kitsConfig == null) {
            createGenusConfig();
        }
        return kitsConfig;
    }

    public void saveGenusConfig() {
        if (kitsConfig == null || kitsFile == null) {
            return;
        }
        try {
            getGenusConfig().save(kitsFile);
        } catch (Exception e) {
            get().getLogger().severe("genus.yml kaydedilirken bir hata oluştu !");
        }
    }
}
