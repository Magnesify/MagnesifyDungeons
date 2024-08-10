package com.magnesify.magnesifydungeons.commands.player;

import com.magnesify.magnesifydungeons.MagnesifyDungeons;
import com.magnesify.magnesifydungeons.dungeon.entitys.DungeonPlayer;
import com.magnesify.magnesifydungeons.modules.managers.DatabaseManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import static com.magnesify.magnesifydungeons.MagnesifyDungeons.get;

public class Stats implements CommandExecutor {
    public Stats(MagnesifyDungeons magnesifyDungeons) {}

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if (commandSender instanceof Player) {

            Player sender = (Player) commandSender;
            DungeonPlayer dungeonPlayer = new DungeonPlayer(sender);
            dungeonPlayer.messageManager().chat("&a&lSıralama");
            DatabaseManager databaseManager = new DatabaseManager(get());
            List<DatabaseManager.Stats.Sort.Player> sortedPlayers = databaseManager.stats().sort().getSortedPlayersByKill();
            for (int i = 0; i < Math.min(3, sortedPlayers.size()); i++) {
                DatabaseManager.Stats.Sort.Player player = sortedPlayers.get(i);
                dungeonPlayer.messageManager().chat("Rank " + (i + 1) + ": " + player.getName() + " - Kills: " + player.getKill());
            }
        }

        return false;
    }
}
