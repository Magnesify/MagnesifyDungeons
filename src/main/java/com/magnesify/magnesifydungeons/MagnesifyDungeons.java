package com.magnesify.magnesifydungeons;

import com.magnesify.magnesifydungeons.boss.BossManager;
import com.magnesify.magnesifydungeons.boss.events.BossCreateEvent;
import com.magnesify.magnesifydungeons.boss.events.BossDeathEvent;
import com.magnesify.magnesifydungeons.commands.Administrator;
import com.magnesify.magnesifydungeons.commands.player.events.JoinDungeon;
import com.magnesify.magnesifydungeons.commands.player.Status;
import com.magnesify.magnesifydungeons.commands.player.events.LeaveDungeon;
import com.magnesify.magnesifydungeons.dungeon.entitys.DungeonConsole;
import com.magnesify.magnesifydungeons.dungeon.entitys.DungeonPlayer;
import com.magnesify.magnesifydungeons.files.Options;
import com.magnesify.magnesifydungeons.files.Players;
import com.magnesify.magnesifydungeons.modules.Defaults;
import com.magnesify.magnesifydungeons.storage.PlayerMethods;
import com.magnesify.magnesifydungeons.events.DungeonCreateEvent;
import com.magnesify.magnesifydungeons.events.DungeonPlayerEvents;
import com.magnesify.magnesifydungeons.files.Boss;
import com.magnesify.magnesifydungeons.files.Dungeons;
import com.magnesify.magnesifydungeons.hanapi.GuiEvents;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import java.sql.SQLException;
import java.util.UUID;

import static com.magnesify.magnesifydungeons.dungeon.entitys.DungeonPlayer.parseHexColors;
import static com.magnesify.magnesifydungeons.events.DungeonCreateEvent.creationSystemLevel;
import static com.magnesify.magnesifydungeons.events.DungeonCreateEvent.data;

public final class MagnesifyDungeons extends JavaPlugin {
    private static MagnesifyDungeons instance;
    public synchronized static MagnesifyDungeons get() {return instance;}
    public void setInstance(MagnesifyDungeons magnesifyDungeons) {instance = magnesifyDungeons;}


    @Override
    public void onEnable() {
        setInstance(this);

        Boss boss = new Boss(); boss.reload();
        Players players = new Players(); players.reload();
        Options options = new Options(); options.reload();
        Dungeons dungeons = new Dungeons(); dungeons.reload();
        saveDefaultConfig();

        getCommand("MagnesifyDungeons").setExecutor(new Administrator(this));
        getCommand("MagnesifyDungeonsBoss").setExecutor(new BossManager(this));
        getCommand("DungeonProfile").setExecutor(new Status(this));
        getCommand("Join").setExecutor(new JoinDungeon(this));
        getCommand("Leave").setExecutor(new LeaveDungeon(this));
        if(options.get().getBoolean("options.clean-start")) Bukkit.getConsoleSender().sendMessage(parseHexColors("<#4f91fc>\n" +
                "                                          _ ____     \n" +
                "   ____ ___  ____ _____ _____  ___  _____(_) __/_  __\n" +
                "  / __ `__ \\/ __ `/ __ `/ __ \\/ _ \\/ ___/ / /_/ / / /\n" +
                " / / / / / / /_/ / /_/ / / / /  __(__  ) / __/ /_/ / \n" +
                "/_/ /_/ /_/\\__,_/\\__, /_/ /_/\\___/____/_/_/  \\__, /  \n" +
                "                /____/                      /____/   \n"));

        Bukkit.getPluginManager().registerEvents(new DungeonPlayerEvents(this), this);
        Bukkit.getPluginManager().registerEvents(new GuiEvents(this), this);
        Bukkit.getPluginManager().registerEvents(new BossCreateEvent(this), this);
        Bukkit.getPluginManager().registerEvents(new BossDeathEvent(this), this);
        Bukkit.getPluginManager().registerEvents(new DungeonCreateEvent(this), this);

        for(Player player : Bukkit.getOnlinePlayers()) {
            if(!Bukkit.getOnlinePlayers().isEmpty()) {
                DungeonPlayer dungeonPlayer = new DungeonPlayer(player);
                dungeonPlayer.create();
                if(options.get().getBoolean("options.send-new-data-log")) Bukkit.getConsoleSender().sendMessage(parseHexColors(String.format("<#4f91fc>[Magnesify Dungeons] %s için bir veri bulunamadı, veri oluşturuluyor.", player.getUniqueId().toString())));
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
