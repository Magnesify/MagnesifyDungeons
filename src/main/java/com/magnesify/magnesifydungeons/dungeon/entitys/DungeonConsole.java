package com.magnesify.magnesifydungeons.dungeon.entitys;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static com.magnesify.magnesifydungeons.dungeon.entitys.DungeonPlayer.parseHexColors;

public class DungeonConsole {

    private static CommandSender sender;

    public DungeonConsole(CommandSender sender) {
        DungeonConsole.sender = sender;
    }

    public DungeonConsole() {}

    public MessageManager ConsoleOutputManager() {
        return new MessageManager();
    }

    public static class MessageManager {

        public void write(String message) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                player.sendMessage(parseHexColors(message));
            } else {
                Bukkit.getConsoleSender().sendMessage(parseHexColors(message));
            }
        }

    }

}
