package com.magnesify.magnesifydungeons.commands;

import com.magnesify.magnesifydungeons.MagnesifyDungeons;
import com.magnesify.magnesifydungeons.commands.administrator.Arguments;
import com.magnesify.magnesifydungeons.dungeon.Dungeon;
import com.magnesify.magnesifydungeons.dungeon.entitys.DungeonConsole;
import com.magnesify.magnesifydungeons.dungeon.entitys.DungeonEntity;
import com.magnesify.magnesifydungeons.dungeon.entitys.DungeonPlayer;
import com.magnesify.magnesifydungeons.files.Boss;
import com.magnesify.magnesifydungeons.files.JsonStorage;
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
import static com.magnesify.magnesifydungeons.events.DungeonCreateEvent.creationSystemLevel;
import static com.magnesify.magnesifydungeons.events.DungeonCreateEvent.data;

public class Administrator implements Arguments, CommandExecutor, TabCompleter {
    public Administrator(MagnesifyDungeons magnesifyDungeons) {}

    @Override
    public long reload() {
        long startTime = System.currentTimeMillis();
        Boss boss = new Boss();
        boss.reload();
        get().reloadConfig();
        long endTime = System.currentTimeMillis();
        return endTime - startTime;
    }

    @Override
    public void help(CommandSender sender) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            DungeonPlayer dungeonPlayer = new DungeonPlayer(player);
            for(String messages : get().getConfig().getStringList("settings.messages.helps.admin")) {
                dungeonPlayer.messageManager().chat(messages);
            }
        } else {
            DungeonConsole dungeonConsole = new DungeonConsole(sender);
            for(String messages : get().getConfig().getStringList("settings.messages.helps.admin")) {
                dungeonConsole.ConsoleOutputManager().write(messages);
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        DungeonEntity dungeonEntity = new DungeonEntity(commandSender);
        if(commandSender.hasPermission("mgd.admin")) {
            if (strings.length == 0) {
                help(commandSender);
            } else if (strings.length == 1) {
                JsonStorage jsonStorage = new JsonStorage(get().getDataFolder() + "/datas/plugin_datas.json");
                if (strings[0].equalsIgnoreCase("reload")) {
                    long millis = reload();
                    dungeonEntity.EntityChatManager().send(get().getConfig().getString("settings.messages.reload").replace("#ms", String.valueOf(millis)));
                } else if (strings[0].equalsIgnoreCase("create")) {
                    if (commandSender instanceof Player) {
                        Player player = ((Player) commandSender).getPlayer();
                        DungeonPlayer dungeonPlayer = new DungeonPlayer(player);
                        if(jsonStorage.getValue("spawn.world").equals("world")) {
                            dungeonPlayer.messageManager().chat(get().getConfig().getString("settings.messages.error.select-spawn-first"));
                        } else {
                            if (creationSystemLevel.get(player.getUniqueId()) != null) {
                                dungeonPlayer.messageManager().chat(get().getConfig().getString("settings.messages.dungeon.already-in-progress"));
                                return false;
                            } else {
                                creationSystemLevel.put(player.getUniqueId(), 1);
                                dungeonPlayer.messageManager().chat(get().getConfig().getString("settings.messages.dungeon.creation-progress-started"));
                                return true;
                            }
                        }
                    } else {
                        dungeonEntity.EntityChatManager().send(get().getConfig().getString("settings.messages.in-game-command"));
                    }
                } else if (strings[0].equalsIgnoreCase("setmainspawn")) {
                    if (commandSender instanceof Player) {
                        Player player = ((Player) commandSender).getPlayer();
                        DungeonPlayer dungeonPlayer = new DungeonPlayer(player);
                        jsonStorage.updateData("spawn.world", player.getLocation().getWorld().getName() );
                        jsonStorage.updateData("spawn.x",  player.getLocation().getX());
                        jsonStorage.updateData("spawn.y",  player.getLocation().getY());
                        jsonStorage.updateData("spawn.z", player.getLocation().getZ());
                        jsonStorage.updateData("spawn.yaw", player.getLocation().getYaw());
                        jsonStorage.updateData("spawn.pitch", player.getLocation().getPitch());
                        dungeonPlayer.messageManager().chat(get().getConfig().getString("settings.messages.main-spawn-selected"));
                    } else {
                        dungeonEntity.EntityChatManager().send(get().getConfig().getString("settings.messages.in-game-command"));
                    }
                } else if (strings[0].equalsIgnoreCase("cancel")) {
                    if (commandSender instanceof Player) {
                        Player player = ((Player) commandSender).getPlayer();
                        DungeonPlayer dungeonPlayer = new DungeonPlayer(player);
                        if (creationSystemLevel.get(player.getUniqueId()) != null) {
                            dungeonPlayer.messageManager().chat(get().getConfig().getString("settings.messages.dungeon.cancelled"));
                            creationSystemLevel.remove(player.getUniqueId());
                            data.clear();
                            return false;
                        } else {
                            dungeonPlayer.messageManager().chat(get().getConfig().getString("settings.messages.dungeon.no-progress-found"));
                            return true;
                        }
                    } else {
                        dungeonEntity.EntityChatManager().send(get().getConfig().getString("settings.messages.in-game-command"));
                    }
                } else {
                    help(commandSender);
                }
            } else if (strings.length == 2) {
                if (strings[0].equalsIgnoreCase("delete")) {
                    Dungeon dungeon = new Dungeon(strings[1]);
                    if(dungeon.exists()) {
                        dungeon.delete();
                        dungeonEntity.EntityChatManager().send(get().getConfig().getString("settings.messages.dungeon.deleted").replace("#name", strings[1]));
                    } else {
                        dungeonEntity.EntityChatManager().send(get().getConfig().getString("settings.messages.dungeon.unknow-dungeon").replace("#name", strings[1]));
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

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> completions = new ArrayList<>();
        List<String> commands = new ArrayList<>();

        if (args.length == 1) {
            commands.add("setmainspawn");
            commands.add("cancel");
            commands.add("reload");
            commands.add("create");
            commands.add("delete");
            commands.add("update");
            StringUtil.copyPartialMatches(args[0], commands, completions);
        }
        Collections.sort(completions);
        return completions;
    }

}
