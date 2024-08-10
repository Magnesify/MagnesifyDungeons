package com.magnesify.magnesifydungeons.kits;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

import static com.magnesify.magnesifydungeons.MagnesifyDungeons.get;

public class KitsFile {
    private FileConfiguration kitsConfig = null;
    private File kitsFile = null;

    public void createKitsConfig() {
        if (kitsFile == null) {
            kitsFile = new File(get().getDataFolder(), "kits.yml");
        }
        if (!kitsFile.exists()) {
            get().saveResource("kits.yml", false); // Bu, varsayılan bir kits.yml varsa onu kopyalar
        }
        kitsConfig = YamlConfiguration.loadConfiguration(kitsFile);
        get().getLogger().severe("'kits.yml' yükleniyor...");
    }

    public FileConfiguration getKitsConfig() {
        if (kitsConfig == null) {
            createKitsConfig();
        }
        return kitsConfig;
    }

    public void saveKitsConfig() {
        if (kitsConfig == null || kitsFile == null) {
            return;
        }
        try {
            getKitsConfig().save(kitsFile);
        } catch (Exception e) {
            get().getLogger().severe("kits.yml kaydedilirken bir hata oluştu !");
        }
    }
}
