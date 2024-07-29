package com.magnesify.magnesifydungeons.dungeon.entitys;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import static com.magnesify.magnesifydungeons.dungeon.entitys.DungeonPlayer.parseHexColors;

public class DungeonEntity {

    private static CommandSender sender;

    public DungeonEntity(CommandSender sender) {
        DungeonEntity.sender = sender;
    }


    public MessageManager EntityChatManager() {
        return new MessageManager();
    }

    public static class MessageManager {
        public void send(String message) {
            if(sender instanceof Player) {
                Player player = (Player) sender;
                player.sendMessage(parseHexColors(message));
            } else {
                Bukkit.getConsoleSender().sendMessage(parseHexColors(message));
            }
        }
    }

}
