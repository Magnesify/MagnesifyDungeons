package com.magnesify.magnesifydungeons.commands;

import com.magnesify.magnesifydungeons.MagnesifyDungeons;
import com.magnesify.magnesifydungeons.boss.MagnesifyBoss;
import com.magnesify.magnesifydungeons.commands.administrator.Arguments;
import com.magnesify.magnesifydungeons.dungeon.Dungeon;
import com.magnesify.magnesifydungeons.dungeon.entitys.DungeonConsole;
import com.magnesify.magnesifydungeons.dungeon.entitys.DungeonEntity;
import com.magnesify.magnesifydungeons.dungeon.entitys.DungeonPlayer;
import com.magnesify.magnesifydungeons.files.JsonStorage;
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
import java.util.Random;

import static com.magnesify.magnesifydungeons.MagnesifyDungeons.get;
import static com.magnesify.magnesifydungeons.events.DungeonCreateEvent.creationSystemLevel;
import static com.magnesify.magnesifydungeons.events.DungeonCreateEvent.data;

public class Administrator implements Arguments, CommandExecutor, TabCompleter {
    public Administrator(MagnesifyDungeons magnesifyDungeons) {}

    @Override
    public long reload() {
        long startTime = System.currentTimeMillis();
        get().reloadConfig();
        long endTime = System.currentTimeMillis();
        return endTime - startTime;
    }

    public boolean isNumeric(String str) {
        if (str == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
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

    public static String generateRandomString() {
        final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 3; i++) {
            if (i > 0) {
                sb.append("b");
            }
            for (int j = 0; j < 2; j++) {
                sb.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
            }
        }

        return sb.toString();
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
                } else if (strings[0].equalsIgnoreCase("test")) {
                    DatabaseManager databaseManager = new DatabaseManager(get());
                    for(int i = 0;i<15;i++) {
                        databaseManager.CreateTestStats(generateRandomString(), generateRandomString());
                    }
                    dungeonEntity.EntityChatManager().send("&aTest verileri oluşturuldu.");
                } else {
                    help(commandSender);
                }
            } else if (strings.length == 2) {
                if (strings[0].equalsIgnoreCase("delete")) {
                    Dungeon dungeon = new Dungeon(strings[1]);
                    if (dungeon.exists()) {
                        dungeon.delete();
                        dungeonEntity.EntityChatManager().send(get().getConfig().getString("settings.messages.dungeon.deleted").replace("#name", strings[1]));
                    } else {
                        dungeonEntity.EntityChatManager().send(get().getConfig().getString("settings.messages.dungeon.unknow-dungeon").replace("#name", strings[1]));
                    }
                }else if (strings[0].equalsIgnoreCase("join")) {
                    if (commandSender instanceof Player) {
                        Player player = ((Player) commandSender).getPlayer();
                        DungeonPlayer dungeonPlayer = new DungeonPlayer(player);
                        Dungeon dungeon = new Dungeon(strings[1]);
                        if (dungeon.exists()) {
                            dungeonPlayer.join(dungeon);
                            dungeon.events().wait(player, dungeon);
                            dungeon.updateCurrentPlayer(player.getName());
                            dungeon.status(false);
                            MagnesifyBoss magnesifyBoss = new MagnesifyBoss(dungeon.parameters().boss());
                            for (String messages : get().getConfig().getStringList("settings.messages.events.joined")) {
                                dungeonPlayer.messageManager().chat(messages.replace("#boss_name", magnesifyBoss.name()).replace("#boss_health", String.valueOf(magnesifyBoss.health())).replace("#next_level", String.valueOf(dungeon.parameters().next())).replace("#category", dungeon.parameters().category()).replace("#playtime", String.valueOf(dungeon.parameters().play())));
                            }
                        } else {
                            dungeonEntity.EntityChatManager().send(get().getConfig().getString("settings.messages.dungeon.unknow-dungeon").replace("#name", strings[1]));

                        }
                    } else {
                        dungeonEntity.EntityChatManager().send(get().getConfig().getString("settings.messages.in-game-command"));

                    }
                } else {
                    help(commandSender);
                }
            }else if (strings.length == 3) {
                if (strings[0].equalsIgnoreCase("update")) {
                    if (strings[1].equalsIgnoreCase("spawn")) {
                        if (commandSender instanceof Player) {
                            Player player = ((Player) commandSender).getPlayer();
                            String zindan = strings[2];
                            Dungeon dungeon = new Dungeon(zindan);
                            if (dungeon.exists()) {
                                DatabaseManager databaseManager = new DatabaseManager(get());
                                databaseManager.setSpawn(strings[2], player.getLocation());
                                dungeonEntity.EntityChatManager().send(get().getConfig().getString("settings.messages.update.spawn").replace("#value", strings[3]).replace("#name", strings[2]));
                            } else {
                                dungeonEntity.EntityChatManager().send(get().getConfig().getString("settings.messages.dungeon.unknow-dungeon").replace("#name", strings[2]));
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
            }else if (strings.length == 4) {
                if (strings[0].equalsIgnoreCase("update")) {
                    if (strings[1].equalsIgnoreCase("point")) {
                        String zindan = strings[2];
                        if (isNumeric(strings[3])) {
                            Dungeon dungeon = new Dungeon(zindan);
                            if(dungeon.exists()) {
                                DatabaseManager databaseManager = new DatabaseManager(get());
                                databaseManager.setPoint(strings[2], Integer.parseInt(strings[3]));
                                dungeonEntity.EntityChatManager().send(get().getConfig().getString("settings.messages.update.point").replace("#value", strings[3]).replace("#name", strings[2]));
                            } else {
                                dungeonEntity.EntityChatManager().send(get().getConfig().getString("settings.messages.dungeon.unknow-dungeon").replace("#name", strings[2]));
                            }
                        } else {
                            dungeonEntity.EntityChatManager().send(get().getConfig().getString("settings.messages.dungeon.canceled.must-be-number"));
                        }
                    } else if (strings[1].equalsIgnoreCase("level")) {
                        String zindan = strings[2];
                        if (isNumeric(strings[3])) {
                            Dungeon dungeon = new Dungeon(zindan);
                            if(dungeon.exists()) {
                                DatabaseManager databaseManager = new DatabaseManager(get());
                                databaseManager.setLevel(strings[2], Integer.parseInt(strings[3]));
                                dungeonEntity.EntityChatManager().send(get().getConfig().getString("settings.messages.update.level").replace("#value", strings[3]).replace("#name", strings[2]));
                            } else {
                                dungeonEntity.EntityChatManager().send(get().getConfig().getString("settings.messages.dungeon.unknow-dungeon").replace("#name", strings[2]));
                            }
                        } else {
                            dungeonEntity.EntityChatManager().send(get().getConfig().getString("settings.messages.dungeon.canceled.must-be-number"));
                        }
                    }else if (strings[1].equalsIgnoreCase("next-level")) {
                        String zindan = strings[2];
                        if (isNumeric(strings[3])) {
                            Dungeon dungeon = new Dungeon(zindan);
                            if(dungeon.exists()) {
                                DatabaseManager databaseManager = new DatabaseManager(get());
                                databaseManager.setNextLevel(strings[2], Integer.parseInt(strings[3]));
                                dungeonEntity.EntityChatManager().send(get().getConfig().getString("settings.messages.update.next-level").replace("#value", strings[3]).replace("#name", strings[2]));
                            } else {
                                dungeonEntity.EntityChatManager().send(get().getConfig().getString("settings.messages.dungeon.unknow-dungeon").replace("#name", strings[2]));
                            }
                        } else {
                            dungeonEntity.EntityChatManager().send(get().getConfig().getString("settings.messages.dungeon.canceled.must-be-number"));
                        }
                    }else if (strings[1].equalsIgnoreCase("play-time")) {
                        String zindan = strings[2];
                        if (isNumeric(strings[3])) {
                            Dungeon dungeon = new Dungeon(zindan);
                            if(dungeon.exists()) {
                                DatabaseManager databaseManager = new DatabaseManager(get());
                                databaseManager.setPlaytime(strings[2], Integer.parseInt(strings[3]));
                                dungeonEntity.EntityChatManager().send(get().getConfig().getString("settings.messages.update.play-time").replace("#value", strings[3]).replace("#name", strings[2]));
                            } else {
                                dungeonEntity.EntityChatManager().send(get().getConfig().getString("settings.messages.dungeon.unknow-dungeon").replace("#name", strings[2]));
                            }
                        } else {
                            dungeonEntity.EntityChatManager().send(get().getConfig().getString("settings.messages.dungeon.canceled.must-be-number"));
                        }
                    }else if (strings[1].equalsIgnoreCase("start-time")) {
                        String zindan = strings[2];
                        if (isNumeric(strings[3])) {
                            Dungeon dungeon = new Dungeon(zindan);
                            if(dungeon.exists()) {
                                DatabaseManager databaseManager = new DatabaseManager(get());
                                databaseManager.setStarttime(strings[2], Integer.parseInt(strings[3]));
                                dungeonEntity.EntityChatManager().send(get().getConfig().getString("settings.messages.update.start-time").replace("#value", strings[3]).replace("#name", strings[2]));
                            } else {
                                dungeonEntity.EntityChatManager().send(get().getConfig().getString("settings.messages.dungeon.unknow-dungeon").replace("#name", strings[2]));
                            }
                        } else {
                            dungeonEntity.EntityChatManager().send(get().getConfig().getString("settings.messages.dungeon.canceled.must-be-number"));
                        }
                    }else if (strings[1].equalsIgnoreCase("category")) {
                        String zindan = strings[2];
                        Dungeon dungeon = new Dungeon(zindan);
                        if(dungeon.exists()) {
                            DatabaseManager databaseManager = new DatabaseManager(get());
                            databaseManager.setCategory(strings[2], strings[3]);
                            dungeonEntity.EntityChatManager().send(get().getConfig().getString("settings.messages.update.next-level").replace("#value", strings[3]).replace("#name", strings[2]));
                        } else {
                            dungeonEntity.EntityChatManager().send(get().getConfig().getString("settings.messages.dungeon.unknow-dungeon").replace("#name", strings[2]));
                        }
                    }else if (strings[1].equalsIgnoreCase("name")) {
                        String zindan = strings[2];
                        Dungeon dungeon = new Dungeon(zindan);
                        if(dungeon.exists()) {
                            DatabaseManager databaseManager = new DatabaseManager(get());
                            databaseManager.setName(strings[2], strings[3]);
                            dungeonEntity.EntityChatManager().send(get().getConfig().getString("settings.messages.update.name").replace("#value", strings[3]).replace("#name", strings[2]));
                        } else {
                            dungeonEntity.EntityChatManager().send(get().getConfig().getString("settings.messages.dungeon.unknow-dungeon").replace("#name", strings[2]));
                        }
                    }else if (strings[1].equalsIgnoreCase("boss")) {
                        String zindan = strings[2];
                        Dungeon dungeon = new Dungeon(zindan);
                        if(dungeon.exists()) {
                            DatabaseManager databaseManager = new DatabaseManager(get());
                            databaseManager.setBossID(strings[2], strings[3]);
                            dungeonEntity.EntityChatManager().send(get().getConfig().getString("settings.messages.update.boss").replace("#value", strings[3]).replace("#name", strings[2]));
                        } else {
                            dungeonEntity.EntityChatManager().send(get().getConfig().getString("settings.messages.dungeon.unknow-dungeon").replace("#name", strings[2]));
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
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("update")) {
                commands.add("boss");
                commands.add("spawn");
                commands.add("next-level");
                commands.add("level");
                commands.add("point");
                commands.add("name");
                commands.add("category");
                commands.add("play-time");
                commands.add("start-time");
                StringUtil.copyPartialMatches(args[1], commands, completions);
            }
            if (args[0].equalsIgnoreCase("delete")) {
                DatabaseManager databaseManager = new DatabaseManager(get());
                for(int i = 0; i<databaseManager.getAllDungeons().size(); i++) {
                    commands.add(databaseManager.getAllDungeons().get(i));
                }
                StringUtil.copyPartialMatches(args[1], commands, completions);
            }
            if (args[0].equalsIgnoreCase("join")) {
                DatabaseManager databaseManager = new DatabaseManager(get());
                for(int i = 0; i<databaseManager.getAllDungeons().size(); i++) {
                    commands.add(databaseManager.getAllDungeons().get(i));
                }
                StringUtil.copyPartialMatches(args[1], commands, completions);
            }
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("update")) {
                DatabaseManager databaseManager = new DatabaseManager(get());
                for(int i = 0; i<databaseManager.getAllDungeons().size(); i++) {
                    commands.add(databaseManager.getAllDungeons().get(i));
                }
                StringUtil.copyPartialMatches(args[2], commands, completions);
            }
        }
        Collections.sort(completions);
        return completions;
    }

}
