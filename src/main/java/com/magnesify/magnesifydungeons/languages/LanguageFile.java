package com.magnesify.magnesifydungeons.languages;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

import static com.magnesify.magnesifydungeons.MagnesifyDungeons.get;

public class LanguageFile {
    private FileConfiguration kitsConfig = null;
    private File kitsFile = null;

    public void createLanguage(String lcale) {
        if (kitsFile == null) {
            kitsFile = new File(get().getDataFolder(), "locale.yml");
        }
        if (!kitsFile.exists()) {
            get().saveResource("locale.yml", false);
        }
        kitsConfig = YamlConfiguration.loadConfiguration(kitsFile);
    }

    public FileConfiguration getLanguage(String locale) {
        if (kitsConfig == null) {
            createLanguage(locale);
        }
        return kitsConfig;
    }

    public void saveLanguage(String locale) {
        if (kitsConfig == null || kitsFile == null) {
            return;
        }
        try {
            getLanguage(locale).save(kitsFile);
        } catch (Exception e) {
            get().getLogger().severe("locale.yml kaydedilirken bir hata oluştu !");
        }
    }
}
