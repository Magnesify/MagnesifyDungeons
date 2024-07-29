package com.magnesify.magnesifydungeons.commands.player;

import com.magnesify.magnesifydungeons.MagnesifyDungeons;
import com.magnesify.magnesifydungeons.dungeon.entitys.DungeonPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;

import static com.magnesify.magnesifydungeons.MagnesifyDungeons.get;

public class Status implements CommandExecutor {
    public Status(MagnesifyDungeons magnesifyDungeons) {}

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if (commandSender instanceof Player) {

            Player player = (Player) commandSender;
            DungeonPlayer dungeonPlayer = new DungeonPlayer(player);
            dungeonPlayer.messageManager().chat("&fŞu an zindandamı: &d" + get().getPlayers().getDungeon(player));
            dungeonPlayer.messageManager().chat("&fGirdiği en son zindan: &d" + get().getPlayers().getLastDungeon(player));
            dungeonPlayer.messageManager().chat("&fPuan: &d" + get().getPlayers().getPoints(player));
        }

        return false;
    }
}
