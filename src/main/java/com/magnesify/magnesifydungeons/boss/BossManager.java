package com.magnesify.magnesifydungeons.boss;

import com.magnesify.magnesifydungeons.MagnesifyDungeons;
import com.magnesify.magnesifydungeons.dungeon.entitys.DungeonConsole;
import com.magnesify.magnesifydungeons.dungeon.entitys.DungeonEntity;
import com.magnesify.magnesifydungeons.dungeon.entitys.DungeonPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static com.magnesify.magnesifydungeons.MagnesifyDungeons.get;
import static com.magnesify.magnesifydungeons.boss.events.BossCreateEvent.bossSystemLevel;

public class BossManager implements CommandExecutor {

    public BossManager(MagnesifyDungeons magnesifyDungeons) {}

    public void help(CommandSender sender) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            DungeonPlayer dungeonPlayer = new DungeonPlayer(player);
            for(String messages : get().getConfig().getStringList("settings.messages.helps.boss")) {
                dungeonPlayer.messageManager().chat(messages);
            }
        } else {
            DungeonConsole dungeonConsole = new DungeonConsole(sender);
            for(String messages : get().getConfig().getStringList("settings.messages.helps.boss")) {
                dungeonConsole.ConsoleOutputManager().write(messages);
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        DungeonEntity dungeonEntity = new DungeonEntity(commandSender);
        if(commandSender instanceof Player) {
            Player player = (Player) commandSender;
            DungeonPlayer dungeonPlayer = new DungeonPlayer(player);
            if(player.hasPermission("mgdb.admin")) {
                if (strings.length == 0) {
                    help(commandSender);
                } else if (strings.length == 1) {
                    if (strings[0].equalsIgnoreCase("create")) {
                        if (bossSystemLevel.get(player.getUniqueId()) != null) {
                            dungeonPlayer.messageManager().chat(get().getConfig().getString("settings.messages.boss.already-in-progress"));
                            return false;
                        } else {
                            bossSystemLevel.put(player.getUniqueId(), 1);
                            dungeonPlayer.messageManager().chat(get().getConfig().getString("settings.messages.boss.creation-progress-started"));
                            return true;
                        }
                    } else {
                        help(commandSender);
                    }
                } else {
                    help(commandSender);
                }
            } else {
                dungeonEntity.EntityChatManager().send(get().getConfig().getString("settings.messages.no-permission"));

            }
        } else {
            dungeonEntity.EntityChatManager().send(get().getConfig().getString("settings.messages.in-game-command"));
        }
        return false;
    }
}
