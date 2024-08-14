package com.magnesify.magnesifydungeons;

import com.magnesify.magnesifydungeons.boss.BossManager;
import com.magnesify.magnesifydungeons.boss.MagnesifyBoss;
import com.magnesify.magnesifydungeons.boss.events.BossCreateEvent;
import com.magnesify.magnesifydungeons.boss.events.BossDeathEvent;
import com.magnesify.magnesifydungeons.commands.Administrator;
import com.magnesify.magnesifydungeons.commands.player.Stats;
import com.magnesify.magnesifydungeons.commands.player.Profile;
import com.magnesify.magnesifydungeons.commands.player.events.JoinDungeon;
import com.magnesify.magnesifydungeons.commands.player.events.LeaveDungeon;
import com.magnesify.magnesifydungeons.commands.player.events.options.SendMessage;
import com.magnesify.magnesifydungeons.commands.player.profile.ProfileGuiInteract;
import com.magnesify.magnesifydungeons.dungeon.entitys.DungeonPlayer;
import com.magnesify.magnesifydungeons.dungeon.types.challange.gui.ChallangeGuiInteract;
import com.magnesify.magnesifydungeons.dungeon.types.challange.gui.ChallangeGuiOpen;
import com.magnesify.magnesifydungeons.dungeon.types.trigger.commands.TriggerTypeDungeon;
import com.magnesify.magnesifydungeons.dungeon.types.trigger.events.TriggerSetupEvents;
import com.magnesify.magnesifydungeons.events.DungeonCreateEvent;
import com.magnesify.magnesifydungeons.events.DungeonPlayerEvents;
import com.magnesify.magnesifydungeons.files.GenusFile;
import com.magnesify.magnesifydungeons.files.JsonStorage;
import com.magnesify.magnesifydungeons.files.Options;
import com.magnesify.magnesifydungeons.genus.events.PlayerBlockBreakEvent;
import com.magnesify.magnesifydungeons.genus.gui.GenusGuiInteract;
import com.magnesify.magnesifydungeons.market.MarketManager;
import com.magnesify.magnesifydungeons.market.file.MarketFile;
import com.magnesify.magnesifydungeons.market.gui.MarketGuiInteract;
import com.magnesify.magnesifydungeons.modules.managers.DatabaseManager;
import com.magnesify.magnesifydungeons.storage.PlayerMethods;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.JSONObject;

import java.io.File;

import static com.magnesify.magnesifydungeons.commands.Administrator.challange;
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
        long startTime = System.currentTimeMillis();
        setInstance(this);
        Options options = new Options(); options.reload();
        MarketFile marketFile = new MarketFile();
        marketFile.createKitsConfig();
        GenusFile genusFile = new GenusFile();
        genusFile.createGenusConfig();
        saveDefaultConfig();
        LogFilter.registerFilter();

        if(!options.get().getBoolean("options.clean-start")) {
            Bukkit.getConsoleSender().sendMessage(parseHexColors("<#4f91fc>\n" +
                    "                                          _ ____     \n" +
                    "   ____ ___  ____ _____ _____  ___  _____(_) __/_  __\n" +
                    "  / __ `__ \\/ __ `/ __ `/ __ \\/ _ \\/ ___/ / /_/ / / /\n" +
                    " / / / / / / /_/ / /_/ / / / /  __(__  ) / __/ /_/ / \n" +
                    "/_/ /_/ /_/\\__,_/\\__, /_/ /_/\\___/____/_/_/  \\__, /  \n" +
                    "                /____/                      /____/   \n" +
                    "\n\n<#4b8eff>Magnesify Dungeons&f, Hacı Mert Gökhan tarafından geliştirildi."));
            int a=0;
            for(int i = 1; i<=marketFile.getMarketConfig().getConfigurationSection("market").getKeys(false).size() - 2;i++) {
                a +=marketFile.getMarketConfig().getConfigurationSection("market." + i).getKeys(false).size();
            }
            Bukkit.getConsoleSender().sendMessage(parseHexColors("<#4b8eff>[Magnesify Dungeons] &fMarkette mevcut olarak <#4b8eff>" + a + "&f adet ürün bulunuyor."));
            if(Bukkit.getPluginManager().getPlugin("ItemsAdder") != null) {
                Bukkit.getConsoleSender().sendMessage(parseHexColors("<#4b8eff>[Magnesify Dungeons] &fDesteklenen eklenti 'ItemsAdder' tespit edildi. Markete uyarlanıyor..."));
            } else {
                Bukkit.getConsoleSender().sendMessage(parseHexColors("<#4b8eff>[Magnesify Dungeons] &fItemsAdder bulunamadı, bu eklenti için uyarlama işlemi atlanıyor."));
            }
            if(Bukkit.getPluginManager().getPlugin("Vault") != null) {
                Bukkit.getConsoleSender().sendMessage(parseHexColors("<#4b8eff>[Magnesify Dungeons] &fDesteklenen eklenti 'Vault' tespit edildi. Markete uyarlanıyor..."));
            } else {
                Bukkit.getConsoleSender().sendMessage(parseHexColors("<#4b8eff>[Magnesify Dungeons] &fVault bulunamadı, bu eklenti için uyarlama işlemi atlanıyor."));
            }
            if(Bukkit.getPluginManager().getPlugin("MythicMobs") != null) {
                Bukkit.getConsoleSender().sendMessage(parseHexColors("<#4b8eff>[Magnesify Dungeons] &fDesteklenen eklenti 'MythicMobs' tespit edildi. Eklentiye uyarlanıyor..."));
            } else {
                Bukkit.getConsoleSender().sendMessage(parseHexColors("<#4b8eff>[Magnesify Dungeons] &fMythicMobs bulunamadı, bu eklenti için uyarlama işlemi atlanıyor."));
            }
            if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
                Bukkit.getConsoleSender().sendMessage(parseHexColors("<#4b8eff>[Magnesify Dungeons] &fDesteklenen eklenti 'PlaceholderAPI' tespit edildi. Eklentiye uyarlanıyor..."));
            } else {
                Bukkit.getConsoleSender().sendMessage(parseHexColors("<#4b8eff>[Magnesify Dungeons] &fPlaceholderAPI bulunamadı, bu eklenti için uyarlama işlemi atlanıyor."));
            }
        }

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

        JsonStorage stats = new JsonStorage(this.getDataFolder() + "/caches/genus.json");

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
        getCommand("Challanges").setExecutor(new ChallangeGuiOpen(this));
        getCommand("DungeonProfile").setExecutor(new Profile(this));
        getCommand("Stats").setExecutor(new Stats(this));
        getCommand("Join").setExecutor(new JoinDungeon(this));
        getCommand("Leave").setExecutor(new LeaveDungeon(this));

        Bukkit.getPluginManager().registerEvents(new GenusGuiInteract(this), this);
        Bukkit.getPluginManager().registerEvents(new DungeonPlayerEvents(this), this);
        Bukkit.getPluginManager().registerEvents(new BossCreateEvent(this), this);
        Bukkit.getPluginManager().registerEvents(new ProfileGuiInteract(this), this);
        Bukkit.getPluginManager().registerEvents(new BossDeathEvent(this), this);
        Bukkit.getPluginManager().registerEvents(new ChallangeGuiInteract(this), this);
        Bukkit.getPluginManager().registerEvents(new TriggerSetupEvents(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerBlockBreakEvent(this), this);
        Bukkit.getPluginManager().registerEvents(new MarketGuiInteract(this), this);
        Bukkit.getPluginManager().registerEvents(new SendMessage(this), this);
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
        long endTime = System.currentTimeMillis();
        Bukkit.getConsoleSender().sendMessage(parseHexColors("<#4b8eff>[Magnesify Dungeons] &fEklenti " + String.valueOf(endTime-startTime) + " ms`de yüklendi..."));

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if(creationSystemLevel.get(player.getUniqueId()) != null) {
                        DungeonPlayer dungeonPlayer = new DungeonPlayer(player);
                        if(challange.get(player.getUniqueId()) != null) {
                            dungeonPlayer.messageManager().stay("<#4b8eff>&lM<#5286ff>&la<#597eff>&lg<#5f75ff>&ln<#666dff>&le<#6d65fe>&ls<#745dfe>&li<#7a55fe>&lf<#814dfe>&ly <#8844fe>&lD<#8f3cfe>&lu<#9534fe>&ln<#9c2cfe>&lg<#a324fd>&le<#aa1bfd>&lo<#b013fd>&ln<#b70bfd>&ls&r", "<#4b8eff>Şu an Meydan Okuma modunda zindan oluşturuyorsun !");
                        }
                        dungeonPlayer.messageManager().actionbar(String.format("&fZindan Adı: <#4b8eff>%s " +
                                "&b| &fKategori: <#4b8eff>%s " +
                                "&b| &fBoss Kimliği: <#4b8eff>%s " +
                                "&b| &fOynama Süresi: <#4b8eff>%s " +
                                "&b| &fBekleme Süresi: <#4b8eff>%s " +
                                "&b| &fSeviye: <#4b8eff>%s", data.get("Name") == null ? getConfig().getString("settings.holders.not-selected") : data.get("Name"),data.get("Category") == null ? getConfig().getString("settings.holders.not-selected") : data.get("Category"),data.get("BossID") == null ? getConfig().getString("settings.holders.not-selected") : data.get("BossID"),data.get("PT") == null ? getConfig().getString("settings.holders.not-selected") : data.get("PT"),data.get("ST") == null ? getConfig().getString("settings.holders.not-selected") : data.get("ST"),data.get("lvl") == null ? getConfig().getString("settings.holders.not-selected") : data.get("lvl")));
                    }
                }
            }
        }.runTaskTimer(get(), 0, 20);
    }

    public PlayerMethods getPlayers() {
        return new PlayerMethods();
    }
}
