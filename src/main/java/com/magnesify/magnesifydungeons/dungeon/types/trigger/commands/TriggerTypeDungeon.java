package com.magnesify.magnesifydungeons.dungeon.types.trigger.commands;

import com.magnesify.magnesifydungeons.MagnesifyDungeons;
import com.magnesify.magnesifydungeons.dungeon.entitys.DungeonConsole;
import com.magnesify.magnesifydungeons.dungeon.entitys.DungeonEntity;
import com.magnesify.magnesifydungeons.dungeon.entitys.DungeonPlayer;
import com.magnesify.magnesifydungeons.dungeon.types.TriggerType;
import com.magnesify.magnesifydungeons.dungeon.types.trigger.TriggerSetup;
import com.magnesify.magnesifydungeons.modules.managers.DatabaseManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

import static com.magnesify.magnesifydungeons.MagnesifyDungeons.get;
import static com.magnesify.magnesifydungeons.modules.Defaults.TEXT_PREFIX;

public class TriggerTypeDungeon implements CommandExecutor {

    public static HashMap<String, String> new_dungeon = new HashMap<>();

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
            DatabaseManager databaseManager = new DatabaseManager(get());
            if (strings.length == 0) {
                help(commandSender);
            } else if (strings.length == 2) {
                if(strings[0].equalsIgnoreCase("join")) {
                    String name = strings[1];
                    if(commandSender instanceof Player) {
                        if(databaseManager.TriggerTypeDungeons().isDungeonExists(name)) {
                            Player player = ((Player) commandSender).getPlayer();
                            TriggerType triggerType = new TriggerType(player);
                            triggerType.join(name);
                            dungeonEntity.EntityChatManager().send(TEXT_PREFIX + " &fZindana giriş yaptın !");
                        } else {
                            dungeonEntity.EntityChatManager().send(get().getConfig().getString("settings.messages.dungeon.unknow-dungeon").replace("#name", strings[1]));
                        }
                    } else {
                        dungeonEntity.EntityChatManager().send(get().getConfig().getString("settings.messages.in-game-command"));
                    }
                } else if (strings[0].equalsIgnoreCase("delete")) {
                    if (databaseManager.TriggerTypeDungeons().isDungeonExists(strings[1])) {
                        databaseManager.TriggerTypeDungeons().deleteDungeon(strings[1]);
                        dungeonEntity.EntityChatManager().send(get().getConfig().getString("settings.messages.dungeon.deleted").replace("#name", strings[1]));
                    } else {
                        dungeonEntity.EntityChatManager().send(get().getConfig().getString("settings.messages.dungeon.unknow-dungeon").replace("#name", strings[1]));
                    }
                } else {
                    help(commandSender);
                }
            } else if (strings.length == 3) {
                if (strings[0].equalsIgnoreCase("setup")) {
                    String name = strings[1];
                    String category = strings[2];
                    if(commandSender instanceof Player) {
                        if(!databaseManager.TriggerTypeDungeons().isDungeonExists(name)) {
                            Player player = ((Player) commandSender).getPlayer();
                            new_dungeon.put("new", name);
                            TriggerSetup setup = new TriggerSetup();
                            setup.StartSetup(player, player.getLocation());
                            dungeonEntity.EntityChatManager().send(TEXT_PREFIX + " &fTrigger Type zindan kurulumu başladı.");
                        } else {
                            dungeonEntity.EntityChatManager().send(get().getConfig().getString("settings.messages.error.cannot-created").replace("#category", category).replace("#name", name));
                        }
                    } else {
                        dungeonEntity.EntityChatManager().send(get().getConfig().getString("settings.messages.in-game-command"));
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
        return false;
    }
}
