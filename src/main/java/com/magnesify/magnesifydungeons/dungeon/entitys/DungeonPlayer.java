package com.magnesify.magnesifydungeons.dungeon.entitys;

import com.magnesify.magnesifydungeons.boss.MagnesifyBoss;
import com.magnesify.magnesifydungeons.files.Players;
import com.magnesify.magnesifydungeons.storage.PlayerMethods;
import com.magnesify.magnesifydungeons.dungeon.Dungeon;
import com.magnesify.magnesifydungeons.modules.Defaults;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.SQLException;
import java.util.List;
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

    }

    public void join(Dungeon dungeon) {
        get().getPlayers().setDone(player, false);
        get().getPlayers().updateDungeonStatus(player, true);
        get().getPlayers().updateLastDungeon(player, dungeon.parameters().name());
    }

    public boolean done() {
        return get().getPlayers().getDone(player);
    }


    public List<String> dungeons() {
        Players players = new Players();
        return players.get().getStringList("players." + player.getUniqueId().toString() + ".dungeons");
    }
    public String stringDungeon() {
        Players players = new Players();
        return players.get().getString("players." + player.getUniqueId().toString() + ".dungeons");
    }

    public void leave(Dungeon dungeon) {
        Defaults defaults = new Defaults();
        dungeon.status(true);
        String last_dungeon= get().getPlayers().getLastDungeon(player);
        MagnesifyBoss magnesifyBoss = new MagnesifyBoss(last_dungeon);
        magnesifyBoss.killBoss();
        if (Bukkit.getWorld(defaults.MainSpawn().world()) != null) {
            Location loc = new Location(Bukkit.getWorld(defaults.MainSpawn().world()), defaults.MainSpawn().x(), defaults.MainSpawn().y(), defaults.MainSpawn().z(), (float) defaults.MainSpawn().yaw(), (float) defaults.MainSpawn().pitch());
            player.teleport(loc);
        } else {
            DungeonConsole dungeonConsole = new DungeonConsole();
            dungeonConsole.ConsoleOutputManager().write("<#4f91fc>[Magnesify Dungeons] &fBaşlangıcın kayıtlı olduğu dünya bulunamadı, dünya silindimi ?");
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

}
