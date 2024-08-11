package com.magnesify.magnesifydungeons.boss;

import com.magnesify.magnesifydungeons.MagnesifyDungeons;
import com.magnesify.magnesifydungeons.dungeon.entitys.DungeonConsole;
import com.magnesify.magnesifydungeons.dungeon.entitys.DungeonEntity;
import com.magnesify.magnesifydungeons.dungeon.entitys.DungeonPlayer;
import com.magnesify.magnesifydungeons.modules.managers.DatabaseManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.magnesify.magnesifydungeons.MagnesifyDungeons.get;
import static com.magnesify.magnesifydungeons.boss.events.BossCreateEvent.bossSystemLevel;

public class BossManager implements CommandExecutor, TabCompleter {

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
            DatabaseManager databaseManager = new DatabaseManager(get());
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
                } else if (strings.length == 2) {
                    if (strings[0].equalsIgnoreCase("delete")) {
                        String name = strings[1];
                        if(databaseManager.isBossAvailable(name)) {
                            databaseManager.boss().delete(name);
                            dungeonPlayer.messageManager().chat(get().getConfig().getString("settings.messages.boss.deleted"));
                        } else {
                            dungeonPlayer.messageManager().chat(get().getConfig().getString("settings.messages.boss.unknow-boss"));
                        }
                    } else {
                        help(commandSender);
                    }
                } else if (strings.length == 4) {
                    if (strings[0].equalsIgnoreCase("update")) {
                        if (strings[1].equalsIgnoreCase("type")) {
                            String name = strings[2];
                            if (databaseManager.isBossAvailable(name)) {
                                databaseManager.boss().setType(name, strings[3]);
                                dungeonPlayer.messageManager().chat(get().getConfig().getString("settings.messages.boss.updated.type"));
                            } else {
                                dungeonPlayer.messageManager().chat(get().getConfig().getString("settings.messages.boss.unknow-boss"));
                            }
                        } else {
                            help(commandSender);
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

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> completions = new ArrayList<>();
        List<String> commands = new ArrayList<>();

        if (args.length == 1) {
            commands.add("create");
            commands.add("delete");
            commands.add("update");
            StringUtil.copyPartialMatches(args[0], commands, completions);
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("delete")) {
                DatabaseManager databaseManager = new DatabaseManager(get());
                for(int i = 0; i<databaseManager.boss().getAllBoss().size(); i++) {
                    commands.add(databaseManager.boss().getAllBoss().get(i));
                }
                StringUtil.copyPartialMatches(args[1], commands, completions);
            }
            if (args[0].equalsIgnoreCase("update")) {
                commands.add("type");
                StringUtil.copyPartialMatches(args[1], commands, completions);
            }
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("update")) {
                DatabaseManager databaseManager = new DatabaseManager(get());
                for(int i = 0; i<databaseManager.boss().getAllBoss().size(); i++) {
                    commands.add(databaseManager.boss().getAllBoss().get(i));
                }
                StringUtil.copyPartialMatches(args[2], commands, completions);
            }
        }
        Collections.sort(completions);
        return completions;
    }

}
