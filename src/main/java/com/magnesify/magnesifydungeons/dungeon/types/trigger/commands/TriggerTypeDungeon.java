package com.magnesify.magnesifydungeons.dungeon.types.trigger.commands;

import com.magnesify.magnesifydungeons.MagnesifyDungeons;
import com.magnesify.magnesifydungeons.dungeon.entitys.DungeonConsole;
import com.magnesify.magnesifydungeons.dungeon.entitys.DungeonEntity;
import com.magnesify.magnesifydungeons.dungeon.entitys.DungeonPlayer;
import com.magnesify.magnesifydungeons.dungeon.types.trigger.TriggerSetup;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static com.magnesify.magnesifydungeons.MagnesifyDungeons.get;

public class TriggerTypeDungeon implements CommandExecutor {
    public TriggerTypeDungeon(MagnesifyDungeons magnesifyDungeons) {}

    public void help(CommandSender sender) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            DungeonPlayer dungeonPlayer = new DungeonPlayer(player);
            for(String messages : get().getConfig().getStringList("settings.messages.helps.trigger-type-dungeon.admin")) {
                dungeonPlayer.messageManager().chat(messages);
            }
        } else {
            DungeonConsole dungeonConsole = new DungeonConsole(sender);
            for(String messages : get().getConfig().getStringList("settings.messages.helps.trigger-type-dungeon.admin")) {
                dungeonConsole.ConsoleOutputManager().write(messages);
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        DungeonEntity dungeonEntity = new DungeonEntity(commandSender);
        if(commandSender.hasPermission("mgd.ttd")) {
            if (strings.length == 1) {
                help(commandSender);
            } else if (strings.length == 2) {
                if (strings[0].equalsIgnoreCase("setup")) {
                    if(commandSender instanceof Player) {
                        Player player = ((Player) commandSender).getPlayer();
                        TriggerSetup setup = new TriggerSetup();
                        setup.StartSetup(player);
                        dungeonEntity.EntityChatManager().send("&aKurulum işlemi başlatıldı.");
                    } else {
                        dungeonEntity.EntityChatManager().send(get().getConfig().getString("settings.messages.in-game-command"));

                    }
                }
            }
        }

        return false;
    }
}
