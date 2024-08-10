package com.magnesify.magnesifydungeons.kits;

import com.magnesify.magnesifydungeons.MagnesifyDungeons;
import com.magnesify.magnesifydungeons.dungeon.entitys.DungeonConsole;
import com.magnesify.magnesifydungeons.dungeon.entitys.DungeonEntity;
import com.magnesify.magnesifydungeons.dungeon.entitys.DungeonPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static com.magnesify.magnesifydungeons.MagnesifyDungeons.get;
import static com.magnesify.magnesifydungeons.modules.Defaults.TEXT_PREFIX;

public class KitManager implements CommandExecutor {

    public KitManager(MagnesifyDungeons magnesifyDungeons) {}

    public void help(CommandSender sender) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            DungeonPlayer dungeonPlayer = new DungeonPlayer(player);
            for(String messages : get().getConfig().getStringList("settings.messages.helps.kits.admin")) {
                dungeonPlayer.messageManager().chat(messages);
            }
        } else {
            DungeonConsole dungeonConsole = new DungeonConsole(sender);
            for(String messages : get().getConfig().getStringList("settings.messages.helps.kits.admin")) {
                dungeonConsole.ConsoleOutputManager().write(messages);
            }
        }
    }


    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        DungeonEntity dungeonEntity = new DungeonEntity(commandSender);
        if (commandSender.hasPermission("mgd.kit")) {
            if(strings.length == 0) {
                help(commandSender);
            } else if (strings.length == 1) {
                if (strings[0].equalsIgnoreCase("reload")) {
                    KitsFile kitsFile = new KitsFile();
                    kitsFile.saveKitsConfig();
                    dungeonEntity.EntityChatManager().send(TEXT_PREFIX + " &fkits.yml başarıyla kaydedildi...");
                } else {
                    if(commandSender instanceof Player) {
                        Player player = (Player) commandSender;
                        KitGuiLoader.openInventory(player);
                    }
                }
            }
        } else {
            dungeonEntity.EntityChatManager().send(get().getConfig().getString("settings.messages.no-permission"));

        }
        return false;
    }
}
