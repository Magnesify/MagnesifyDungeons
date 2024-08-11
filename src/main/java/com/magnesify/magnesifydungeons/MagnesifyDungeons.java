package com.magnesify.magnesifydungeons;

import com.magnesify.magnesifydungeons.boss.BossManager;
import com.magnesify.magnesifydungeons.boss.MagnesifyBoss;
import com.magnesify.magnesifydungeons.boss.events.BossCreateEvent;
import com.magnesify.magnesifydungeons.boss.events.BossDeathEvent;
import com.magnesify.magnesifydungeons.commands.Administrator;
import com.magnesify.magnesifydungeons.commands.player.Stats;
import com.magnesify.magnesifydungeons.commands.player.Status;
import com.magnesify.magnesifydungeons.commands.player.events.JoinDungeon;
import com.magnesify.magnesifydungeons.commands.player.events.LeaveDungeon;
import com.magnesify.magnesifydungeons.dungeon.entitys.DungeonPlayer;
import com.magnesify.magnesifydungeons.dungeon.types.trigger.commands.TriggerTypeDungeon;
import com.magnesify.magnesifydungeons.dungeon.types.trigger.events.TriggerSetupEvents;
import com.magnesify.magnesifydungeons.events.DungeonCreateEvent;
import com.magnesify.magnesifydungeons.events.DungeonPlayerEvents;
import com.magnesify.magnesifydungeons.files.JsonStorage;
import com.magnesify.magnesifydungeons.files.Options;
import com.magnesify.magnesifydungeons.market.gui.MarketGuiInteract;
import com.magnesify.magnesifydungeons.market.MarketManager;
import com.magnesify.magnesifydungeons.market.file.MarketFile;
import com.magnesify.magnesifydungeons.modules.managers.DatabaseManager;
import com.magnesify.magnesifydungeons.storage.PlayerMethods;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.JSONObject;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.magnesify.magnesifydungeons.dungeon.entitys.DungeonPlayer.parseHexColors;
import static com.magnesify.magnesifydungeons.events.DungeonCreateEvent.creationSystemLevel;
import static com.magnesify.magnesifydungeons.events.DungeonCreateEvent.data;

public final class MagnesifyDungeons extends JavaPlugin {
    private DatabaseManager dbManager;
    private static MagnesifyDungeons instance;
    public synchronized static MagnesifyDungeons get() {return instance;}
    public void setInstance(MagnesifyDungeons magnesifyDungeons) {instance = magnesifyDungeons;}


    @Override
    public void onEnable() {
        setInstance(this);

        Logger.getLogger("com.zaxxer.hikari.pool.PoolBase").setLevel(Level.OFF);
        Logger.getLogger("com.zaxxer.hikari.pool.HikariPool").setLevel(Level.OFF);
        Logger.getLogger("com.zaxxer.hikari.HikariDataSource").setLevel(Level.OFF);
        Logger.getLogger("com.zaxxer.hikari.HikariConfig").setLevel(Level.OFF);
        Logger.getLogger("com.zaxxer.hikari.util.DriverDataSource").setLevel(Level.OFF);

        Options options = new Options(); options.reload();
        MarketFile marketFile = new MarketFile();
        marketFile.createKitsConfig();
        saveDefaultConfig();

        if(!options.get().getBoolean("options.clean-start"))
            Bukkit.getConsoleSender().sendMessage(parseHexColors("<#4f91fc>\n" +
                    "                                          _ ____     \n" +
                    "   ____ ___  ____ _____ _____  ___  _____(_) __/_  __\n" +
                    "  / __ `__ \\/ __ `/ __ `/ __ \\/ _ \\/ ___/ / /_/ / / /\n" +
                    " / / / / / / /_/ / /_/ / / / /  __(__  ) / __/ /_/ / \n" +
                    "/_/ /_/ /_/\\__,_/\\__, /_/ /_/\\___/____/_/_/  \\__, /  \n" +
                    "                /____/                      /____/   \n" +
                    "\n\n<#4b8eff>Magnesify Dungeons&f, Hacı Mert Gökhan tarafından geliştirildi."));

        File dataFolder = getDataFolder();
        File datasFolder = new File(dataFolder, "datas");
        if (!datasFolder.exists()) {
            if (datasFolder.mkdirs()) {
                Bukkit.getConsoleSender().sendMessage(parseHexColors("<#4b8eff>[Magnesify Dungeons] &f'datas' klasörü başarıyla oluşturuldu."));
            } else {
                Bukkit.getConsoleSender().sendMessage(parseHexColors("<#4b8eff>[Magnesify Dungeons] &f'datas' klasörü oluşturulurken bir sorun oluştu."));
            }
        } else {
            Bukkit.getConsoleSender().sendMessage(parseHexColors("<#4b8eff>[Magnesify Dungeons] &f'datas' klasörü zaten mevcut."));
        }

        File cachesFolder = new File(dataFolder, "caches");
        if (!cachesFolder.exists()) {
            if (cachesFolder.mkdirs()) {
                Bukkit.getConsoleSender().sendMessage(parseHexColors("<#4b8eff>[Magnesify Dungeons] &f'caches' klasörü başarıyla oluşturuldu."));
            } else {
                Bukkit.getConsoleSender().sendMessage(parseHexColors("<#4b8eff>[Magnesify Dungeons] &f'caches' klasörü oluşturulurken bir sorun oluştu."));
            }
        } else {
            Bukkit.getConsoleSender().sendMessage(parseHexColors("<#4b8eff>[Magnesify Dungeons] &f'caches' klasörü zaten mevcut."));
        }


        JsonStorage jsonStorage = new JsonStorage(this.getDataFolder() + "/datas/plugin_datas.json");
        JsonStorage cache = new JsonStorage(this.getDataFolder() + "/caches/player_dungeon_cache.json");
        JsonStorage players = new JsonStorage(this.getDataFolder() + "/datas/players.json");

        JsonStorage stats = new JsonStorage(this.getDataFolder() + "/caches/statistics.json");

        JSONObject players_config = new JSONObject();
        players_config.put("json_config_version", "1");
        players.createJsonFile(players_config);
        stats.createJsonFile(players_config);
        cache.createJsonFile(players_config);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("spawn.world", "world");
        jsonObject.put("spawn.x", 0);
        jsonObject.put("spawn.y", 0);
        jsonObject.put("spawn.z", 0);
        jsonObject.put("spawn.pitch", 0);
        jsonObject.put("spawn.yaw", 0);
        jsonStorage.createJsonFile(jsonObject);

        dbManager = new DatabaseManager(this);
        dbManager.initialize();

        getCommand("MagnesifyDungeons").setExecutor(new Administrator(this));
        getCommand("MagnesifyDungeons").setTabCompleter(new Administrator(this));
        getCommand("MagnesifyDungeonsTrigger").setExecutor(new TriggerTypeDungeon(this));
        getCommand("MagnesifyDungeonsTrigger").setTabCompleter(new TriggerTypeDungeon(this));
        getCommand("MagnesifyDungeonsBoss").setExecutor(new BossManager(this));
        getCommand("MagnesifyDungeonsBoss").setTabCompleter(new BossManager(this));
        getCommand("MagnesifyDungeonsMarket").setExecutor(new MarketManager(this));
        getCommand("DungeonProfile").setExecutor(new Status(this));
        getCommand("Stats").setExecutor(new Stats(this));
        getCommand("Join").setExecutor(new JoinDungeon(this));
        getCommand("Leave").setExecutor(new LeaveDungeon(this));

        Bukkit.getPluginManager().registerEvents(new DungeonPlayerEvents(this), this);
        Bukkit.getPluginManager().registerEvents(new BossCreateEvent(this), this);
        Bukkit.getPluginManager().registerEvents(new BossDeathEvent(this), this);
        Bukkit.getPluginManager().registerEvents(new TriggerSetupEvents(this), this);
        Bukkit.getPluginManager().registerEvents(new MarketGuiInteract(this), this);
        Bukkit.getPluginManager().registerEvents(new DungeonCreateEvent(this), this);

        MagnesifyBoss create_boss = new MagnesifyBoss("Magnesify", "Magnesify");
        if(create_boss.create()) {
            Bukkit.getConsoleSender().sendMessage(parseHexColors("<#4b8eff>[Magnesify Dungeons] &fBoss 'Magnesify' oluşturuldu..."));
        }

        for(Player player : Bukkit.getOnlinePlayers()) {
            if(!Bukkit.getOnlinePlayers().isEmpty()) {
                DungeonPlayer dungeonPlayer = new DungeonPlayer(player);
                dungeonPlayer.create();
                if(options.get().getBoolean("options.send-new-data-log")) Bukkit.getConsoleSender().sendMessage(parseHexColors(String.format("<#4b8eff>[Magnesify Dungeons] %s için bir veri bulunamadı, veri oluşturuluyor.", player.getUniqueId().toString())));
            }
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if(creationSystemLevel.get(player.getUniqueId()) != null) {
                        DungeonPlayer dungeonPlayer = new DungeonPlayer(player);
                        dungeonPlayer.messageManager().actionbar(String.format("&fZindan Adı: &d&l%s" +
                                "&e| &fKategori: &d&l%s " +
                                "&e| &fBoss Kimliği: &d&l%s " +
                                "&e| &fOynama Süresi: &d&l%s " +
                                "&e| &fBekleme Süresi: &d&l%s " +
                                "&e| &fSeviye: &d&l%s", data.get("Name") == null ? "Seçilmemiş" : data.get("Name"),data.get("Category") == null ? "Seçilmemiş" : data.get("Category"),data.get("BossID") == null ? "Seçilmemiş" : data.get("BossID"),data.get("PT") == null ? "Seçilmemiş" : data.get("PT"),data.get("ST") == null ? "Seçilmemiş" : data.get("ST"),data.get("lvl") == null ? "Seçilmemiş" : data.get("lvl")));
                    }
                }
            }
        }.runTaskTimer(get(), 0, 20);
    }

    public PlayerMethods getPlayers() {
        return new PlayerMethods();
    }
}
