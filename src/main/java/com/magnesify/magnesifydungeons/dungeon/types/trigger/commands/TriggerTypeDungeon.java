package com.magnesify.magnesifydungeons.dungeon.types.trigger.commands;

import com.magnesify.magnesifydungeons.MagnesifyDungeons;
import com.magnesify.magnesifydungeons.dungeon.TriggerType;
import com.magnesify.magnesifydungeons.dungeon.entitys.DungeonConsole;
import com.magnesify.magnesifydungeons.dungeon.entitys.DungeonEntity;
import com.magnesify.magnesifydungeons.dungeon.entitys.DungeonPlayer;
import com.magnesify.magnesifydungeons.dungeon.types.trigger.TriggerSetup;
import com.magnesify.magnesifydungeons.modules.managers.DatabaseManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static com.magnesify.magnesifydungeons.MagnesifyDungeons.get;
import static com.magnesify.magnesifydungeons.dungeon.types.trigger.events.TriggerSetupEvents.setupDataHolder;
import static com.magnesify.magnesifydungeons.modules.Defaults.TEXT_PREFIX;
import static com.magnesify.magnesifydungeons.modules.managers.DungeonContentManager.dungeonChestCreation;

public class TriggerTypeDungeon implements CommandExecutor, TabCompleter {

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
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> completions = new ArrayList<>();
        List<String> commands = new ArrayList<>();

        if (args.length == 1) {
            commands.add("setup");
            commands.add("join");
            commands.add("delete");
            commands.add("chest-mode");
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
                    commands.add(databaseManager.TriggerTypeDungeons().getAllDungeons().get(i));
                }
                StringUtil.copyPartialMatches(args[1], commands, completions);
            }else if (args[0].equalsIgnoreCase("chest-mode")) {
                DatabaseManager databaseManager = new DatabaseManager(get());
                for(int i = 0; i<databaseManager.getAllDungeons().size(); i++) {
                    commands.add(databaseManager.TriggerTypeDungeons().getAllDungeons().get(i));
                }
                StringUtil.copyPartialMatches(args[1], commands, completions);
            }else if (args[0].equalsIgnoreCase("join")) {
                DatabaseManager databaseManager = new DatabaseManager(get());
                for(int i = 0; i<databaseManager.getAllDungeons().size(); i++) {
                    commands.add(databaseManager.TriggerTypeDungeons().getAllDungeons().get(i));
                }
                StringUtil.copyPartialMatches(args[1], commands, completions);
            }
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("update")) {
                DatabaseManager databaseManager = new DatabaseManager(get());
                for(int i = 0; i<databaseManager.getAllDungeons().size(); i++) {
                    commands.add(databaseManager.TriggerTypeDungeons().getAllDungeons().get(i));
                }
                StringUtil.copyPartialMatches(args[2], commands, completions);
            }
        }
        Collections.sort(completions);
        return completions;
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
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        DungeonEntity dungeonEntity = new DungeonEntity(commandSender);
        if(commandSender.hasPermission("mgd.ttd")) {
            DatabaseManager databaseManager = new DatabaseManager(get());
            if (strings.length == 0) {
                help(commandSender);
            }else if (strings.length == 4) {
                if (strings[0].equalsIgnoreCase("update")) {
                    if (strings[1].equalsIgnoreCase("point")) {
                        String zindan = strings[2];
                        if (isNumeric(strings[3])) {
                            if(databaseManager.TriggerTypeDungeons().isDungeonExists(zindan)) {
                                databaseManager.TriggerTypeDungeons().setPoint(strings[2], Integer.parseInt(strings[3]));
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
                            if(databaseManager.TriggerTypeDungeons().isDungeonExists(zindan)) {
                                databaseManager.TriggerTypeDungeons().setLevel(strings[2], Integer.parseInt(strings[3]));
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
                            if(databaseManager.TriggerTypeDungeons().isDungeonExists(zindan)) {
                                databaseManager.TriggerTypeDungeons().setNextLevel(strings[2], Integer.parseInt(strings[3]));
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
                            if(databaseManager.TriggerTypeDungeons().isDungeonExists(zindan)) {
                                databaseManager.TriggerTypeDungeons().setPlaytime(strings[2], Integer.parseInt(strings[3]));
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
                            if(databaseManager.TriggerTypeDungeons().isDungeonExists(zindan)) {
                                databaseManager.TriggerTypeDungeons().setStarttime(strings[2], Integer.parseInt(strings[3]));
                                dungeonEntity.EntityChatManager().send(get().getConfig().getString("settings.messages.update.start-time").replace("#value", strings[3]).replace("#name", strings[2]));
                            } else {
                                dungeonEntity.EntityChatManager().send(get().getConfig().getString("settings.messages.dungeon.unknow-dungeon").replace("#name", strings[2]));
                            }
                        } else {
                            dungeonEntity.EntityChatManager().send(get().getConfig().getString("settings.messages.dungeon.canceled.must-be-number"));
                        }
                    }else if (strings[1].equalsIgnoreCase("category")) {
                        String zindan = strings[2];
                        if(databaseManager.TriggerTypeDungeons().isDungeonExists(zindan)) {
                            databaseManager.TriggerTypeDungeons().setCategory(strings[2], strings[3]);
                            dungeonEntity.EntityChatManager().send(get().getConfig().getString("settings.messages.update.next-level").replace("#value", strings[3]).replace("#name", strings[2]));
                        } else {
                            dungeonEntity.EntityChatManager().send(get().getConfig().getString("settings.messages.dungeon.unknow-dungeon").replace("#name", strings[2]));
                        }
                    }else if (strings[1].equalsIgnoreCase("name")) {
                        String zindan = strings[2];
                        if(databaseManager.TriggerTypeDungeons().isDungeonExists(zindan)) {
                            databaseManager.TriggerTypeDungeons().setName(strings[2], strings[3]);
                            dungeonEntity.EntityChatManager().send(get().getConfig().getString("settings.messages.update.name").replace("#value", strings[3]).replace("#name", strings[2]));
                        } else {
                            dungeonEntity.EntityChatManager().send(get().getConfig().getString("settings.messages.dungeon.unknow-dungeon").replace("#name", strings[2]));
                        }
                    }else if (strings[1].equalsIgnoreCase("boss")) {
                        String zindan = strings[2];
                        if(databaseManager.TriggerTypeDungeons().isDungeonExists(zindan)) {
                            databaseManager.TriggerTypeDungeons().setBossID(strings[2], strings[3]);
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
            } else if (strings.length == 2) {
                if(strings[0].equalsIgnoreCase("join")) {
                    String name = strings[1];
                    if(commandSender instanceof Player) {
                        if(databaseManager.TriggerTypeDungeons().isDungeonExists(name)) {
                            if(databaseManager.TriggerTypeDungeons().getAvailable(name)) {
                                Player player = ((Player) commandSender).getPlayer();
                                TriggerType triggerType = new TriggerType(player);
                                databaseManager.TriggerTypeDungeons().setAvailable(name, false);
                                triggerType.join(name);
                                dungeonEntity.EntityChatManager().send(TEXT_PREFIX + " &fZindana giriş yaptın !");
                            } else {
                                dungeonEntity.EntityChatManager().send(get().getConfig().getString("settings.messages.dungeon.full").replace("#name", name).replace("#countdown", ""));
                            }
                        } else {
                            dungeonEntity.EntityChatManager().send(get().getConfig().getString("settings.messages.dungeon.unknow-dungeon").replace("#name", strings[1]));
                        }
                    } else {
                        dungeonEntity.EntityChatManager().send(get().getConfig().getString("settings.messages.in-game-command"));
                    }
                } else if (strings[0].equalsIgnoreCase("delete")) {
                    if (databaseManager.TriggerTypeDungeons().isDungeonExists(strings[1])) {
                        databaseManager.TriggerTypeDungeons().deleteDungeon(strings[1]);
                        databaseManager.deleteBosspointRecordsContainingParameter(strings[1]);
                        databaseManager.deleteCheckpointRecordsContainingParameter(strings[1]);
                        dungeonEntity.EntityChatManager().send(get().getConfig().getString("settings.messages.dungeon.deleted").replace("#name", strings[1]));
                    } else {
                        dungeonEntity.EntityChatManager().send(get().getConfig().getString("settings.messages.dungeon.unknow-dungeon").replace("#name", strings[1]));
                    }
                } else if (strings[0].equalsIgnoreCase("chest-mode")) {
                    if(commandSender instanceof Player) {
                        if (databaseManager.TriggerTypeDungeons().isDungeonExists(strings[1])) {
                            Player player = ((Player) commandSender).getPlayer();
                            if(dungeonChestCreation.get(player.getUniqueId()) == null) {
                                dungeonChestCreation.put(player.getUniqueId(), true);
                                new_dungeon.put("chestdata", strings[1]);
                                dungeonEntity.EntityChatManager().send(get().getConfig().getString("settings.messages.dungeon.chest-mode.enabled").replace("#name", strings[1]));
                            } else {
                                dungeonChestCreation.remove(player.getUniqueId());
                                setupDataHolder.remove("chest_level");
                                setupDataHolder.remove("chestdata");
                                dungeonEntity.EntityChatManager().send(get().getConfig().getString("settings.messages.dungeon.chest-mode.disabled").replace("#name", strings[1]));
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
                } else if (strings[0].equalsIgnoreCase("update")) {
                    if (strings[1].equalsIgnoreCase("spawn")) {
                        if (commandSender instanceof Player) {
                            String name = strings[1];
                            Player player = ((Player) commandSender).getPlayer();
                            if(!databaseManager.TriggerTypeDungeons().isDungeonExists(name)) {
                                databaseManager.TriggerTypeDungeons().setSpawn(strings[2], player.getLocation());
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
            } else {
                help(commandSender);
            }
        } else {
            dungeonEntity.EntityChatManager().send(get().getConfig().getString("settings.messages.no-permission"));
        }
        return false;
    }
}
