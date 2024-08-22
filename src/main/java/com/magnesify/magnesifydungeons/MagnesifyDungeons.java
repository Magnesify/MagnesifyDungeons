package com.magnesify.magnesifydungeons;

import com.magnesify.magnesifydungeons.boss.BossManager;
import com.magnesify.magnesifydungeons.boss.MagnesifyBoss;
import com.magnesify.magnesifydungeons.boss.events.BossCreateEvent;
import com.magnesify.magnesifydungeons.boss.events.BossDeathEvent;
import com.magnesify.magnesifydungeons.boss.gui.BossGuiInteract;
import com.magnesify.magnesifydungeons.boss.gui.drops.DropsGuiInteract;
import com.magnesify.magnesifydungeons.boss.gui.settings.SettingsGuiInteract;
import com.magnesify.magnesifydungeons.commands.Administrator;
import com.magnesify.magnesifydungeons.commands.player.Genus;
import com.magnesify.magnesifydungeons.commands.player.Profile;
import com.magnesify.magnesifydungeons.commands.player.Spawn;
import com.magnesify.magnesifydungeons.commands.player.Stats;
import com.magnesify.magnesifydungeons.commands.player.events.JoinDungeon;
import com.magnesify.magnesifydungeons.commands.player.events.LeaveDungeon;
import com.magnesify.magnesifydungeons.commands.player.events.options.SendMessage;
import com.magnesify.magnesifydungeons.commands.player.profile.ProfileGuiInteract;
import com.magnesify.magnesifydungeons.dungeon.entitys.DungeonPlayer;
import com.magnesify.magnesifydungeons.dungeon.types.challange.gui.ChallangeGuiInteract;
import com.magnesify.magnesifydungeons.dungeon.types.challange.gui.ChallangeGuiOpen;
import com.magnesify.magnesifydungeons.dungeon.types.trigger.commands.TriggerTypeDungeon;
import com.magnesify.magnesifydungeons.dungeon.types.trigger.commands.gui.TriggerGuiInteract;
import com.magnesify.magnesifydungeons.dungeon.types.trigger.events.TriggerSetupEvents;
import com.magnesify.magnesifydungeons.dungeon.types.trigger.gui.TriggerTypeLevelBossInteract;
import com.magnesify.magnesifydungeons.dungeon.types.trigger.gui.boss.MagnesifyBossGuiInteract;
import com.magnesify.magnesifydungeons.dungeon.types.trigger.gui.boss.type.BossTypeGuiInteract;
import com.magnesify.magnesifydungeons.dungeon.types.trigger.gui.dungeons.DungeonsGuiInteract;
import com.magnesify.magnesifydungeons.events.CustomCommands;
import com.magnesify.magnesifydungeons.events.DungeonCreateEvent;
import com.magnesify.magnesifydungeons.events.DungeonPlayerEvents;
import com.magnesify.magnesifydungeons.files.GenusFile;
import com.magnesify.magnesifydungeons.files.JsonStorage;
import com.magnesify.magnesifydungeons.files.Options;
import com.magnesify.magnesifydungeons.genus.events.PlayerGenusEvents;
import com.magnesify.magnesifydungeons.genus.gui.GenusGuiInteract;
import com.magnesify.magnesifydungeons.languages.LanguageFile;
import com.magnesify.magnesifydungeons.market.MarketManager;
import com.magnesify.magnesifydungeons.market.file.MarketFile;
import com.magnesify.magnesifydungeons.market.gui.MarketGuiInteract;
import com.magnesify.magnesifydungeons.modules.managers.DatabaseManager;
import com.magnesify.magnesifydungeons.mythic.MythicBossDeathEvent;
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
import static com.magnesify.magnesifydungeons.mythic.MythicAdapter.setMythic;
import static com.magnesify.magnesifydungeons.support.Vault.setVault;
import static com.magnesify.magnesifydungeons.support.Vault.setupEconomy;

public final class MagnesifyDungeons extends JavaPlugin {
    private DatabaseManager dbManager;
    private static MagnesifyDungeons instance;
    public synchronized static MagnesifyDungeons get() {return instance;}
    public void setInstance(MagnesifyDungeons magnesifyDungeons) {instance = magnesifyDungeons;}
    @Override
    public void onEnable() {
        long startTime = System.currentTimeMillis();
        setInstance(this);
        LanguageFile languageFile = new LanguageFile();
        languageFile.createLanguage();
        Options options = new Options(); options.reload();

        MarketFile marketFile = new MarketFile();
        marketFile.createKitsConfig();
        GenusFile genusFile = new GenusFile();
        genusFile.createGenusConfig();
        saveDefaultConfig();
        LogFilter.registerFilter();

        if(!options.get().getBoolean("options.clean-start")) {
            int a=0;
            for(int i = 1; i<=marketFile.getMarketConfig().getConfigurationSection("market").getKeys(false).size() - 2;i++) {
                a +=marketFile.getMarketConfig().getConfigurationSection("market." + i).getKeys(false).size();
            }
            Bukkit.getConsoleSender().sendMessage(parseHexColors(String.format( new LanguageFile().getLanguage().getString("plugin.market.info"),a)));
            if(Bukkit.getPluginManager().getPlugin("ItemsAdder") != null) {
                Bukkit.getConsoleSender().sendMessage(parseHexColors( new LanguageFile().getLanguage().getString("plugin.supports.items-adder.found")));
            } else {
                Bukkit.getConsoleSender().sendMessage(parseHexColors( new LanguageFile().getLanguage().getString("plugin.supports.items-adder.not-found")));
            }
            if(Bukkit.getPluginManager().getPlugin("Vault") != null) {
                Bukkit.getConsoleSender().sendMessage(parseHexColors( new LanguageFile().getLanguage().getString("plugin.supports.vault.found")));
                if (!setupEconomy() ) {
                    Bukkit.getConsoleSender().sendMessage(parseHexColors( new LanguageFile().getLanguage().getString("plugin.supports.vault.economy")));
                    setVault(false);
                    return;
                } else {
                    setVault(true);
                }
            } else {
                Bukkit.getConsoleSender().sendMessage(parseHexColors( new LanguageFile().getLanguage().getString("plugin.supports.vault.not-found")));
            }
            if(Bukkit.getPluginManager().getPlugin("MythicMobs") != null) {
                Bukkit.getConsoleSender().sendMessage(parseHexColors( new LanguageFile().getLanguage().getString("plugin.supports.mythicmobs.found")));
                Bukkit.getPluginManager().registerEvents(new MythicBossDeathEvent(this), this);
                setMythic(true);
            } else {
                Bukkit.getConsoleSender().sendMessage(parseHexColors( new LanguageFile().getLanguage().getString("plugin.supports.mythicmobs.not-found")));
                Bukkit.getPluginManager().registerEvents(new BossDeathEvent(this), this);
                setMythic(false);
            }
        }

        File dataFolder = getDataFolder();
        File cachesFolder = new File(dataFolder, "caches");
        if (!cachesFolder.exists()) {
            if (cachesFolder.mkdirs()) {
                Bukkit.getConsoleSender().sendMessage(parseHexColors( new LanguageFile().getLanguage().getString("plugin.folder.caches.created")));
            } else {
                Bukkit.getConsoleSender().sendMessage(parseHexColors( new LanguageFile().getLanguage().getString("plugin.folder.caches.error")));
            }
        } else {
            Bukkit.getConsoleSender().sendMessage(parseHexColors( new LanguageFile().getLanguage().getString("plugin.folder.caches.exists")));
        }
        JsonStorage cache = new JsonStorage(this.getDataFolder() + "/caches/player_dungeon_cache.json");

        JSONObject players_config = new JSONObject();
        players_config.put("json_config_version", "1");
        cache.writeData(players_config);

        dbManager = new DatabaseManager(this);
        dbManager.initialize();

        getCommand("Dungeons").setExecutor(new Administrator(this));
        getCommand("Dungeons").setTabCompleter(new Administrator(this));
        getCommand("DiscoveryDungeons").setExecutor(new TriggerTypeDungeon(this));
        getCommand("DiscoveryDungeons").setTabCompleter(new TriggerTypeDungeon(this));
        getCommand("Boss").setExecutor(new BossManager(this));
        getCommand("Boss").setTabCompleter(new BossManager(this));
        getCommand("Genus").setExecutor(new Genus(this));
        getCommand("DungeonMarket").setExecutor(new MarketManager(this));
        getCommand("DungeonProfile").setExecutor(new Profile(this));
        getCommand("DungeonSpawn").setExecutor(new Spawn(this));
        getCommand("Challanges").setExecutor(new ChallangeGuiOpen(this));
        getCommand("Stats").setExecutor(new Stats(this));
        getCommand("Join").setExecutor(new JoinDungeon(this));
        getCommand("Leave").setExecutor(new LeaveDungeon(this));

        Bukkit.getPluginManager().registerEvents(new GenusGuiInteract(this), this);
        Bukkit.getPluginManager().registerEvents(new DungeonPlayerEvents(this), this);
        Bukkit.getPluginManager().registerEvents(new BossCreateEvent(this), this);
        Bukkit.getPluginManager().registerEvents(new ProfileGuiInteract(this), this);
        Bukkit.getPluginManager().registerEvents(new ChallangeGuiInteract(this), this);
        Bukkit.getPluginManager().registerEvents(new BossTypeGuiInteract(this), this);
        Bukkit.getPluginManager().registerEvents(new TriggerTypeLevelBossInteract(this), this);
        Bukkit.getPluginManager().registerEvents(new BossGuiInteract(this), this);
        Bukkit.getPluginManager().registerEvents(new MagnesifyBossGuiInteract(this), this);
        Bukkit.getPluginManager().registerEvents(new CustomCommands(this), this);
        Bukkit.getPluginManager().registerEvents(new SettingsGuiInteract(this), this);
        Bukkit.getPluginManager().registerEvents(new TriggerGuiInteract(this), this);
        Bukkit.getPluginManager().registerEvents(new DropsGuiInteract(this), this);
        Bukkit.getPluginManager().registerEvents(new TriggerSetupEvents(this), this);
        Bukkit.getPluginManager().registerEvents(new DungeonsGuiInteract(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerGenusEvents(this), this);
        Bukkit.getPluginManager().registerEvents(new MarketGuiInteract(this), this);
        Bukkit.getPluginManager().registerEvents(new SendMessage(this), this);
        Bukkit.getPluginManager().registerEvents(new DungeonCreateEvent(this), this);

        MagnesifyBoss create_boss = new MagnesifyBoss("Magnesify", "Magnesify");
        if(create_boss.create()) {
            Bukkit.getConsoleSender().sendMessage(parseHexColors( new LanguageFile().getLanguage().getString("plugin.boss.created")));
        }

        for(Player player : Bukkit.getOnlinePlayers()) {
            if(!Bukkit.getOnlinePlayers().isEmpty()) {
                DungeonPlayer dungeonPlayer = new DungeonPlayer(player);
                dungeonPlayer.create();
                if(options.get().getBoolean("options.send-new-data-log")) Bukkit.getConsoleSender().sendMessage(parseHexColors(String.format(new LanguageFile().getLanguage().getString("plugin.data-not-found"), player.getUniqueId().toString())));
            }
        }
        long endTime = System.currentTimeMillis();
        Bukkit.getConsoleSender().sendMessage(parseHexColors(String.format( new LanguageFile().getLanguage().getString("plugin.loaded"),String.valueOf(endTime-startTime))));

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if(creationSystemLevel.get(player.getUniqueId()) != null) {
                        DungeonPlayer dungeonPlayer = new DungeonPlayer(player);
                        if(challange.get(player.getUniqueId()) != null) {
                            dungeonPlayer.messageManager().stay("<#4b8eff>&lM<#5286ff>&la<#597eff>&lg<#5f75ff>&ln<#666dff>&le<#6d65fe>&ls<#745dfe>&li<#7a55fe>&lf<#814dfe>&ly <#8844fe>&lD<#8f3cfe>&lu<#9534fe>&ln<#9c2cfe>&lg<#a324fd>&le<#aa1bfd>&lo<#b013fd>&ln<#b70bfd>&ls&r", new LanguageFile().getLanguage().getString("plugin.challange.setup-subtitle"));
                        }
                        dungeonPlayer.messageManager().actionbar(String.format("&f"+getConfig().getString("settings.holders.name")+": <#4b8eff>%s " +
                                "&b| &f"+getConfig().getString("settings.holders.category")+": <#4b8eff>%s " +
                                "&b| &f"+getConfig().getString("settings.holders.boss_id")+": <#4b8eff>%s " +
                                "&b| &f"+getConfig().getString("settings.holders.play-time")+": <#4b8eff>%s " +
                                "&b| &f"+getConfig().getString("settings.holders.start-time")+": <#4b8eff>%s " +
                                "&b| &f"+getConfig().getString("settings.holders.level")+": <#4b8eff>%s", data.get("Name") == null ? getConfig().getString("settings.holders.not-selected") : data.get("Name"),data.get("Category") == null ? getConfig().getString("settings.holders.not-selected") : data.get("Category"),data.get("BossID") == null ? getConfig().getString("settings.holders.not-selected") : data.get("BossID"),data.get("PT") == null ? getConfig().getString("settings.holders.not-selected") : data.get("PT"),data.get("ST") == null ? getConfig().getString("settings.holders.not-selected") : data.get("ST"),data.get("lvl") == null ? getConfig().getString("settings.holders.not-selected") : data.get("lvl")));
                    }
                }
            }
        }.runTaskTimer(get(), 0, 20);
    }

    public PlayerMethods getPlayers() {
        return new PlayerMethods();
    }
}
