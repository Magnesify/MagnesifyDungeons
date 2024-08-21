package com.magnesify.magnesifydungeons.dungeon.entitys;

import com.magnesify.magnesifydungeons.boss.MagnesifyBoss;
import com.magnesify.magnesifydungeons.dungeon.Dungeon;
import com.magnesify.magnesifydungeons.dungeon.types.challange.Challange;
import com.magnesify.magnesifydungeons.files.JsonStorage;
import com.magnesify.magnesifydungeons.languages.LanguageFile;
import com.magnesify.magnesifydungeons.modules.Defaults;
import com.magnesify.magnesifydungeons.storage.PlayerMethods;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.json.JSONObject;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.magnesify.magnesifydungeons.MagnesifyDungeons.get;

public class DungeonPlayer {

    private static Player player;

    public DungeonPlayer(Player player) {
        DungeonPlayer.player = player;
    }

    public void create() {
        PlayerMethods playerMethods = new PlayerMethods(player);
        playerMethods.createPlayer();
    }


    public void createDungeonAccount(PlayerJoinEvent event) {
        create();
    }

    public MessageManager messageManager() {
        return new MessageManager();
    }

    public static class MessageManager {

        public void chat(String message) {
            player.sendMessage(parseHexColors(message));
        }

        public void actionbar(String message) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(parseHexColors(message)));
        }

        public void title(String title, String subtitle) {
            player.sendTitle(parseHexColors(title), parseHexColors(subtitle), 20, 40, 20);
        }

        public void stay(String title, String subtitle) {
            player.sendTitle(parseHexColors(title), parseHexColors(subtitle), 20, 100, 20);
        }


    }

    public void join(Dungeon dungeon) {
        get().getPlayers().setDone(player, false);
        get().getPlayers().updateDungeonStatus(player, true);
        get().getPlayers().updateLastDungeon(player, dungeon.parameters().name());
    }

    public void challange(Challange dungeon) {
        get().getPlayers().setDone(player, false);
        get().getPlayers().updateDungeonStatus(player, true);
        get().getPlayers().updateLastDungeon(player, dungeon.parameters().name());
    }

    public boolean done() {
        return get().getPlayers().getDone(player);
    }


    public void leaveChallange(Challange dungeon) {
        Defaults defaults = new Defaults();
        dungeon.status(true);
        String last_dungeon= get().getPlayers().getLastBoss(player);
        MagnesifyBoss magnesifyBoss = new MagnesifyBoss();
        magnesifyBoss.killBoss(last_dungeon);
        DungeonConsole dungeonConsole = new DungeonConsole();
        if(defaults.MainSpawn().world() != null) {
            if (Bukkit.getWorld(defaults.MainSpawn().world()) != null) {
                Location loc = new Location(Bukkit.getWorld(defaults.MainSpawn().world()), defaults.MainSpawn().x(), defaults.MainSpawn().y(), defaults.MainSpawn().z(), (float) defaults.MainSpawn().yaw(), (float) defaults.MainSpawn().pitch());
                player.teleport(loc);
            } else {
                dungeonConsole.ConsoleOutputManager().write(new LanguageFile().getLanguage().getString("plugin.spawn-not-exists"));
            }
        } else {
            dungeonConsole.ConsoleOutputManager().write(new LanguageFile().getLanguage().getString("plugin.spawn-not-exists"));
        }
        get().getPlayers().updateDungeonStatus(player, false);
    }

    public void leave(Dungeon dungeon) {
        Defaults defaults = new Defaults();
        dungeon.status(true);
        DungeonConsole dungeonConsole = new DungeonConsole();
        if(defaults.MainSpawn().world() != null) {
            if (Bukkit.getWorld(defaults.MainSpawn().world()) != null) {
                Location loc = new Location(Bukkit.getWorld(defaults.MainSpawn().world()), defaults.MainSpawn().x(), defaults.MainSpawn().y(), defaults.MainSpawn().z(), (float) defaults.MainSpawn().yaw(), (float) defaults.MainSpawn().pitch());
                player.teleport(loc);
            } else {
                dungeonConsole.ConsoleOutputManager().write(new LanguageFile().getLanguage().getString("plugin.spawn-not-exists"));
            }
        } else {
            dungeonConsole.ConsoleOutputManager().write(new LanguageFile().getLanguage().getString("plugin.spawn-not-exists"));
        }
        get().getPlayers().updateDungeonStatus(player, false);
    }

    public boolean inDungeon() {
        return get().getPlayers().getDungeon(player);
    }


    public static String parseHexColors(String message) {
        message = message.replace("#prefix", get().getConfig().getString("settings.prefix"));
        Pattern hexPattern = Pattern.compile("<#([A-Fa-f0-9]{6})>");
        Matcher matcher = hexPattern.matcher(message);
        StringBuffer buffer = new StringBuffer();

        while (matcher.find()) {
            String hexColor = matcher.group(1);
            matcher.appendReplacement(buffer, ChatColor.of("#" + hexColor) + "");
        }
        matcher.appendTail(buffer);
        return ChatColor.translateAlternateColorCodes('&', buffer.toString());
    }


    public boolean isEnteredFirstTime(String dungeon) {
        Dungeon dungeonData = new Dungeon(dungeon);
        if(dungeonData.exists()) {
            JsonStorage players = new JsonStorage(get().getDataFolder() + "/caches/player_dungeon_cache.json");
            return players.getValue("cache." + player.getUniqueId().toString() + "." + dungeonData.parameters().category() + ".played_before") == null;
        }
        return false;
    }

    public void CreateNewDungeonPlayData(String dungeon) {
        Dungeon dungeonData = new Dungeon(dungeon);
        if(dungeonData.exists()) {
            Date date = new Date();
            JsonStorage players = new JsonStorage(get().getDataFolder() + "/caches/player_dungeon_cache.json");
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("cache." + player.getUniqueId().toString() + "." + dungeonData.parameters().category() + ".played_before", "yes");
            jsonObject.put("cache." + player.getUniqueId().toString() + "." + dungeonData.parameters().category() + ".first_join_date", date.getDate());
            jsonObject.put("cache." + player.getUniqueId().toString() + "." + dungeonData.parameters().category() + ".level", 1);
            players.createJsonFile(jsonObject);
        }
    }

    public int getCurrentLevelForDungeon(String dungeon) {
        Dungeon dungeonData = new Dungeon(dungeon);
        if(dungeonData.exists()) {
            JsonStorage players = new JsonStorage(get().getDataFolder() + "/caches/player_dungeon_cache.json");
            return (int) players.getValue("cache." + player.getUniqueId().toString() + "." + dungeonData.parameters().category() + ".level");
        }
        return 1;
    }

    public void updateCurrentLevelForDungeon(String dungeon, int levl) {
        Dungeon dungeonData = new Dungeon(dungeon);
        if(dungeonData.exists()) {
            JsonStorage players = new JsonStorage(get().getDataFolder() + "/caches/player_dungeon_cache.json");
            players.updateData("cache." + player.getUniqueId().toString() + "." + dungeonData.parameters().category() + ".level", levl);
        }
    }

}
