package com.magnesify.magnesifydungeons.languages;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

import static com.magnesify.magnesifydungeons.MagnesifyDungeons.get;

public class LanguageFile {
    private FileConfiguration kitsConfig = null;
    private File kitsFile = null;

    public void createLanguage() {
        if (kitsFile == null) {
            kitsFile = new File(get().getDataFolder(), "locale.yml");
        }
        if (!kitsFile.exists()) {
            get().saveResource("locale.yml", false);
        }
        kitsConfig = YamlConfiguration.loadConfiguration(kitsFile);
    }

    public FileConfiguration getLanguage() {
        if (kitsConfig == null) {
            createLanguage();
        }
        return kitsConfig;
    }

    public void saveLanguage() {
        if (kitsConfig == null || kitsFile == null) {
            return;
        }
        try {
            getLanguage().save(kitsFile);
        } catch (Exception e) {
            get().getLogger().severe("locale.yml kaydedilirken bir hata oluştu !");
        }
    }
}
