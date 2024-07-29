package com.magnesify.magnesifydungeons.commands;

import com.magnesify.magnesifydungeons.MagnesifyDungeons;
import com.magnesify.magnesifydungeons.commands.administrator.Arguments;
import com.magnesify.magnesifydungeons.dungeon.Dungeon;
import com.magnesify.magnesifydungeons.dungeon.entitys.DungeonConsole;
import com.magnesify.magnesifydungeons.dungeon.entitys.DungeonEntity;
import com.magnesify.magnesifydungeons.dungeon.entitys.DungeonPlayer;
import com.magnesify.magnesifydungeons.files.Boss;
import com.magnesify.magnesifydungeons.files.Dungeons;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import static com.magnesify.magnesifydungeons.MagnesifyDungeons.get;
import static com.magnesify.magnesifydungeons.dungeon.Dungeon.list;
import static com.magnesify.magnesifydungeons.events.DungeonCreateEvent.creationSystemLevel;
import static com.magnesify.magnesifydungeons.events.DungeonCreateEvent.data;

public class Administrator implements Arguments, CommandExecutor {
    public Administrator(MagnesifyDungeons magnesifyDungeons) {}

    @Override
    public long reload() {
        long startTime = System.currentTimeMillis();
        Dungeons dungeons = new Dungeons();
        Boss boss = new Boss();
        boss.reload();
        dungeons.reload();
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
                if (strings[0].equalsIgnoreCase("reload")) {
                    long millis = reload();
                    dungeonEntity.EntityChatManager().send(get().getConfig().getString("settings.messages.reload").replace("#ms", String.valueOf(millis)));
                } else if (strings[0].equalsIgnoreCase("create")) {
                    if (commandSender instanceof Player) {
                        Player player = ((Player) commandSender).getPlayer();
                        DungeonPlayer dungeonPlayer = new DungeonPlayer(player);
                        if(get().getConfig().getString("settings.main-spawn.world") == null ) {
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
                        get().getConfig().set("settings.main-spawn.world", player.getLocation().getWorld().getName());
                        get().getConfig().set("settings.main-spawn.x", player.getLocation().getX());
                        get().getConfig().set("settings.main-spawn.y", player.getLocation().getY());
                        get().getConfig().set("settings.main-spawn.z", player.getLocation().getZ());
                        get().getConfig().set("settings.main-spawn.yaw", player.getLocation().getYaw());
                        get().getConfig().set("settings.main-spawn.pitch", player.getLocation().getPitch());
                        get().saveConfig();
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
                } else if (strings[0].equalsIgnoreCase("list")) {
                    if(list() != null) {
                        dungeonEntity.EntityChatManager().send(get().getConfig().getStringList("settings.messages.dungeon.list").get(0));
                        for(String dngList : list()) {
                            dungeonEntity.EntityChatManager().send(get().getConfig().getStringList("settings.messages.dungeon.list").get(1).replace("#name", dngList));
                        }
                    } else {
                        dungeonEntity.EntityChatManager().send(get().getConfig().getString("settings.messages.error.no-data-found"));
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
}
