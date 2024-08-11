package com.magnesify.magnesifydungeons.market.file;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

import static com.magnesify.magnesifydungeons.MagnesifyDungeons.get;

public class MarketFile {
    private FileConfiguration kitsConfig = null;
    private File kitsFile = null;

    public void createKitsConfig() {
        if (kitsFile == null) {
            kitsFile = new File(get().getDataFolder(), "market.yml");
        }
        if (!kitsFile.exists()) {
            get().saveResource("market.yml", false); // Bu, varsayılan bir market.yml varsa onu kopyalar
        }
        kitsConfig = YamlConfiguration.loadConfiguration(kitsFile);
    }

    public FileConfiguration getMarketConfig() {
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
            getMarketConfig().save(kitsFile);
        } catch (Exception e) {
            get().getLogger().severe("market.yml kaydedilirken bir hata oluştu !");
        }
    }
}
