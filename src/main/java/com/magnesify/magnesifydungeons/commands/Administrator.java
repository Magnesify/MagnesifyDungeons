package com.magnesify.magnesifydungeons.commands;

import com.magnesify.magnesifydungeons.MagnesifyDungeons;
import com.magnesify.magnesifydungeons.boss.MagnesifyBoss;
import com.magnesify.magnesifydungeons.commands.administrator.Arguments;
import com.magnesify.magnesifydungeons.dungeon.Dungeon;
import com.magnesify.magnesifydungeons.dungeon.entitys.DungeonConsole;
import com.magnesify.magnesifydungeons.dungeon.entitys.DungeonEntity;
import com.magnesify.magnesifydungeons.dungeon.entitys.DungeonPlayer;
import com.magnesify.magnesifydungeons.files.GenusFile;
import com.magnesify.magnesifydungeons.files.JsonStorage;
import com.magnesify.magnesifydungeons.files.Options;
import com.magnesify.magnesifydungeons.genus.gui.GenusGuiLoader;
import com.magnesify.magnesifydungeons.genus.gui.IAGenusGuiLoader;
import com.magnesify.magnesifydungeons.languages.LanguageFile;
import com.magnesify.magnesifydungeons.market.file.MarketFile;
import com.magnesify.magnesifydungeons.modules.managers.DatabaseManager;
import com.magnesify.magnesifydungeons.storage.PlayerMethods;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.StringUtil;

import java.util.*;

import static com.magnesify.magnesifydungeons.MagnesifyDungeons.get;
import static com.magnesify.magnesifydungeons.dungeon.entitys.DungeonPlayer.parseHexColors;
import static com.magnesify.magnesifydungeons.events.DungeonCreateEvent.creationSystemLevel;
import static com.magnesify.magnesifydungeons.events.DungeonCreateEvent.data;
import static com.magnesify.magnesifydungeons.modules.Defaults.TEXT_PREFIX;
import static com.magnesify.magnesifydungeons.support.Vault.setVault;
import static com.magnesify.magnesifydungeons.support.Vault.setupEconomy;

public class Administrator implements Arguments, CommandExecutor, TabCompleter {
    public Administrator(MagnesifyDungeons magnesifyDungeons) {}

    public static HashMap<UUID, Boolean> challange = new HashMap<>();

    @Override
    public long reload() {
        long startTime = System.currentTimeMillis();
        get().reloadConfig();
        MarketFile marketFile = new MarketFile();
        marketFile.saveKitsConfig();
        GenusFile genusFile = new GenusFile();
        genusFile.saveGenusConfig();
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
            for(String messages : new LanguageFile().getLanguage().getStringList("messages.helps.admin")) {
                dungeonPlayer.messageManager().chat(messages);
            }
        } else {
            DungeonConsole dungeonConsole = new DungeonConsole(sender);
            for(String messages : new LanguageFile().getLanguage().getStringList("messages.helps.admin")) {
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
                    dungeonEntity.EntityChatManager().send(new LanguageFile().getLanguage().getString("messages.reload").replace("#ms", String.valueOf(millis)));
                } else if (strings[0].equalsIgnoreCase("create")) {
                    if (commandSender instanceof Player) {
                        Player player = ((Player) commandSender).getPlayer();
                        DungeonPlayer dungeonPlayer = new DungeonPlayer(player);
                        Options options = new Options();
                        if(options.get().getString("spawn.world") == null) {
                            dungeonPlayer.messageManager().chat(new LanguageFile().getLanguage().getString("messages.error.select-spawn-first"));
                        } else {
                            if (creationSystemLevel.get(player.getUniqueId()) != null) {
                                dungeonPlayer.messageManager().chat(new LanguageFile().getLanguage().getString("messages.dungeon.already-in-progress"));
                                return false;
                            } else {
                                creationSystemLevel.put(player.getUniqueId(), 1);
                                dungeonPlayer.messageManager().chat(new LanguageFile().getLanguage().getString("messages.dungeon.creation-progress-started"));
                                return true;
                            }
                        }
                    } else {
                        dungeonEntity.EntityChatManager().send(new LanguageFile().getLanguage().getString("messages.in-game-command"));
                    }
                } else if (strings[0].equalsIgnoreCase("challange")) {
                    if (commandSender instanceof Player) {
                        Player player = ((Player) commandSender).getPlayer();
                        DungeonPlayer dungeonPlayer = new DungeonPlayer(player);
                        Options options = new Options();
                        if(options.get().getString("spawn.world") == null) {
                            dungeonPlayer.messageManager().chat(new LanguageFile().getLanguage().getString("messages.error.select-spawn-first"));
                        } else {
                            if (creationSystemLevel.get(player.getUniqueId()) != null) {
                                dungeonPlayer.messageManager().chat(new LanguageFile().getLanguage().getString("messages.dungeon.already-in-progress"));
                                return false;
                            } else {
                                challange.put(player.getUniqueId(), true);
                                creationSystemLevel.put(player.getUniqueId(), 1);
                                dungeonPlayer.messageManager().chat(new LanguageFile().getLanguage().getString("messages.dungeon.creation-progress-started"));
                                return true;
                            }
                        }
                    } else {
                        dungeonEntity.EntityChatManager().send(new LanguageFile().getLanguage().getString("messages.in-game-command"));
                    }
                } else if (strings[0].equalsIgnoreCase("setmainspawn")) {
                    if (commandSender instanceof Player) {
                        Player player = ((Player) commandSender).getPlayer();
                        DungeonPlayer dungeonPlayer = new DungeonPlayer(player);
                        Options options = new Options();
                        options.get().set("spawn.world", player.getLocation().getWorld().getName());
                        options.get().set("spawn.x", player.getLocation().getX());
                        options.get().set("spawn.y", player.getLocation().getY());
                        options.get().set("spawn.z", player.getLocation().getZ());
                        options.get().set("spawn.yaw", player.getLocation().getYaw());
                        options.get().set("spawn.pitch", player.getLocation().getPitch());
                        options.save();
                        dungeonPlayer.messageManager().chat(new LanguageFile().getLanguage().getString("messages.main-spawn-selected"));
                    } else {
                        dungeonEntity.EntityChatManager().send(new LanguageFile().getLanguage().getString("messages.in-game-command"));
                    }
                } else if (strings[0].equalsIgnoreCase("genus-gui")) {
                    if (commandSender instanceof Player) {
                        Player player = (Player) commandSender;
                        boolean textc = get().getConfig().isSet("settings.genus.custom-gui-texture");
                        if(textc) {
                            if(Bukkit.getPluginManager().getPlugin("ItemsAdder") != null) {
                                IAGenusGuiLoader.openInventory(player);
                            } else {
                                Bukkit.getConsoleSender().sendMessage(parseHexColors(new LanguageFile().getLanguage().getString("plugin.error.ia.genus")));
                                GenusGuiLoader.openInventory(player);
                            }
                        } else {
                            GenusGuiLoader.openInventory(player);
                        }
                    } else {
                        dungeonEntity.EntityChatManager().send(new LanguageFile().getLanguage().getString("messages.in-game-command"));

                    }
                } else if (strings[0].equalsIgnoreCase("cancel")) {
                    if (commandSender instanceof Player) {
                        Player player = ((Player) commandSender).getPlayer();
                        DungeonPlayer dungeonPlayer = new DungeonPlayer(player);
                        if (creationSystemLevel.get(player.getUniqueId()) != null) {
                            dungeonPlayer.messageManager().chat(new LanguageFile().getLanguage().getString("messages.dungeon.cancelled"));
                            creationSystemLevel.remove(player.getUniqueId());
                            data.clear();
                            return false;
                        } else {
                            dungeonPlayer.messageManager().chat(new LanguageFile().getLanguage().getString("messages.dungeon.no-progress-found"));
                            return true;
                        }
                    } else {
                        dungeonEntity.EntityChatManager().send(new LanguageFile().getLanguage().getString("messages.in-game-command"));
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
                        dungeonEntity.EntityChatManager().send(new LanguageFile().getLanguage().getString("messages.dungeon.deleted").replace("#name", strings[1]));
                    } else {
                        dungeonEntity.EntityChatManager().send(new LanguageFile().getLanguage().getString("messages.dungeon.unknow-dungeon").replace("#name", strings[1]));
                    }
                }else if (strings[0].equalsIgnoreCase("genus-tools")) {
                    if (commandSender instanceof Player) {
                        String tool_name = strings[1];
                        Player player = ((Player) commandSender).getPlayer();
                        if (get().getConfig().getString("settings.skill-tools." + tool_name) != null) {
                            ItemStack a = new ItemStack(Material.getMaterial(get().getConfig().getString("settings.skill-tools." + tool_name + ".material")), 1);
                            ItemMeta itemMeta = a.getItemMeta();
                            itemMeta.setDisplayName(parseHexColors(get().getConfig().getString("settings.skill-tools." + tool_name + ".display")));
                            a.setItemMeta(itemMeta);
                            player.getInventory().addItem(a);
                            dungeonEntity.EntityChatManager().send(new LanguageFile().getLanguage().getString("messages.genus.added-skill-tool").replace("#name", strings[1]));
                        } else {
                            dungeonEntity.EntityChatManager().send(new LanguageFile().getLanguage().getString("messages.genus.unknow-tool").replace("#name", strings[1]));
                        }
                    } else {
                        dungeonEntity.EntityChatManager().send(new LanguageFile().getLanguage().getString("messages.in-game-command"));
                    }
                }else if (strings[0].equalsIgnoreCase("hook")) {
                    if(strings[1].equalsIgnoreCase("Vault")) {
                        if (!setupEconomy() ) {
                            dungeonEntity.EntityChatManager().send(parseHexColors(String.format(new LanguageFile().getLanguage().getString("plugin.supports.vault.not-found"), TEXT_PREFIX)));
                            setVault(false);
                        } else {
                            dungeonEntity.EntityChatManager().send(parseHexColors(new LanguageFile().getLanguage().getString("plugin.supports.vault.found")));
                            setVault(true);
                        }
                    } else {
                        help(commandSender);
                    }
                }else if (strings[0].equalsIgnoreCase("unload")) {
                    if(strings[1].equalsIgnoreCase("Vault")) {
                        if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
                            dungeonEntity.EntityChatManager().send(parseHexColors(String.format(new LanguageFile().getLanguage().getString("plugin.supports.vault.unload"), TEXT_PREFIX)));
                            setVault(false);
                        } else {
                            dungeonEntity.EntityChatManager().send(parseHexColors(String.format(new LanguageFile().getLanguage().getString("plugin.supports.vault.not-found"), TEXT_PREFIX)));
                        }
                    } else {
                        help(commandSender);
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
                            dungeonEntity.EntityChatManager().send(new LanguageFile().getLanguage().getString("messages.dungeon.unknow-dungeon").replace("#name", strings[1]));

                        }
                    } else {
                        dungeonEntity.EntityChatManager().send(new LanguageFile().getLanguage().getString("messages.in-game-command"));

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
                                dungeonEntity.EntityChatManager().send(new LanguageFile().getLanguage().getString("messages.update.spawn").replace("#value", strings[3]).replace("#name", strings[2]));
                            } else {
                                dungeonEntity.EntityChatManager().send(new LanguageFile().getLanguage().getString("messages.dungeon.unknow-dungeon").replace("#name", strings[2]));
                            }
                        } else {
                            dungeonEntity.EntityChatManager().send(new LanguageFile().getLanguage().getString("messages.in-game-command"));
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
                                dungeonEntity.EntityChatManager().send(new LanguageFile().getLanguage().getString("messages.update.point").replace("#value", strings[3]).replace("#name", strings[2]));
                            } else {
                                dungeonEntity.EntityChatManager().send(new LanguageFile().getLanguage().getString("messages.dungeon.unknow-dungeon").replace("#name", strings[2]));
                            }
                        } else {
                            dungeonEntity.EntityChatManager().send(new LanguageFile().getLanguage().getString("messages.dungeon.canceled.must-be-number"));
                        }
                    } else if (strings[1].equalsIgnoreCase("level")) {
                        String zindan = strings[2];
                        if (isNumeric(strings[3])) {
                            Dungeon dungeon = new Dungeon(zindan);
                            if(dungeon.exists()) {
                                DatabaseManager databaseManager = new DatabaseManager(get());
                                databaseManager.setLevel(strings[2], Integer.parseInt(strings[3]));
                                dungeonEntity.EntityChatManager().send(new LanguageFile().getLanguage().getString("messages.update.level").replace("#value", strings[3]).replace("#name", strings[2]));
                            } else {
                                dungeonEntity.EntityChatManager().send(new LanguageFile().getLanguage().getString("messages.dungeon.unknow-dungeon").replace("#name", strings[2]));
                            }
                        } else {
                            dungeonEntity.EntityChatManager().send(new LanguageFile().getLanguage().getString("messages.dungeon.canceled.must-be-number"));
                        }
                    }else if (strings[1].equalsIgnoreCase("next-level")) {
                        String zindan = strings[2];
                        if (isNumeric(strings[3])) {
                            Dungeon dungeon = new Dungeon(zindan);
                            if(dungeon.exists()) {
                                DatabaseManager databaseManager = new DatabaseManager(get());
                                databaseManager.setNextLevel(strings[2], Integer.parseInt(strings[3]));
                                dungeonEntity.EntityChatManager().send(new LanguageFile().getLanguage().getString("messages.update.next-level").replace("#value", strings[3]).replace("#name", strings[2]));
                            } else {
                                dungeonEntity.EntityChatManager().send(new LanguageFile().getLanguage().getString("messages.dungeon.unknow-dungeon").replace("#name", strings[2]));
                            }
                        } else {
                            dungeonEntity.EntityChatManager().send(new LanguageFile().getLanguage().getString("messages.dungeon.canceled.must-be-number"));
                        }
                    }else if (strings[1].equalsIgnoreCase("play-time")) {
                        String zindan = strings[2];
                        if (isNumeric(strings[3])) {
                            Dungeon dungeon = new Dungeon(zindan);
                            if(dungeon.exists()) {
                                DatabaseManager databaseManager = new DatabaseManager(get());
                                databaseManager.setPlaytime(strings[2], Integer.parseInt(strings[3]));
                                dungeonEntity.EntityChatManager().send(new LanguageFile().getLanguage().getString("messages.update.play-time").replace("#value", strings[3]).replace("#name", strings[2]));
                            } else {
                                dungeonEntity.EntityChatManager().send(new LanguageFile().getLanguage().getString("messages.dungeon.unknow-dungeon").replace("#name", strings[2]));
                            }
                        } else {
                            dungeonEntity.EntityChatManager().send(new LanguageFile().getLanguage().getString("messages.dungeon.canceled.must-be-number"));
                        }
                    }else if (strings[1].equalsIgnoreCase("start-time")) {
                        String zindan = strings[2];
                        if (isNumeric(strings[3])) {
                            Dungeon dungeon = new Dungeon(zindan);
                            if(dungeon.exists()) {
                                DatabaseManager databaseManager = new DatabaseManager(get());
                                databaseManager.setStarttime(strings[2], Integer.parseInt(strings[3]));
                                dungeonEntity.EntityChatManager().send(new LanguageFile().getLanguage().getString("messages.update.start-time").replace("#value", strings[3]).replace("#name", strings[2]));
                            } else {
                                dungeonEntity.EntityChatManager().send(new LanguageFile().getLanguage().getString("messages.dungeon.unknow-dungeon").replace("#name", strings[2]));
                            }
                        } else {
                            dungeonEntity.EntityChatManager().send(new LanguageFile().getLanguage().getString("messages.dungeon.canceled.must-be-number"));
                        }
                    }else if (strings[1].equalsIgnoreCase("category")) {
                        String zindan = strings[2];
                        Dungeon dungeon = new Dungeon(zindan);
                        if(dungeon.exists()) {
                            DatabaseManager databaseManager = new DatabaseManager(get());
                            databaseManager.setCategory(strings[2], strings[3]);
                            dungeonEntity.EntityChatManager().send(new LanguageFile().getLanguage().getString("messages.update.next-level").replace("#value", strings[3]).replace("#name", strings[2]));
                        } else {
                            dungeonEntity.EntityChatManager().send(new LanguageFile().getLanguage().getString("messages.dungeon.unknow-dungeon").replace("#name", strings[2]));
                        }
                    }else if (strings[1].equalsIgnoreCase("name")) {
                        String zindan = strings[2];
                        Dungeon dungeon = new Dungeon(zindan);
                        if(dungeon.exists()) {
                            DatabaseManager databaseManager = new DatabaseManager(get());
                            databaseManager.setName(strings[2], strings[3]);
                            dungeonEntity.EntityChatManager().send(new LanguageFile().getLanguage().getString("messages.update.name").replace("#value", strings[3]).replace("#name", strings[2]));
                        } else {
                            dungeonEntity.EntityChatManager().send(new LanguageFile().getLanguage().getString("messages.dungeon.unknow-dungeon").replace("#name", strings[2]));
                        }
                    }else if (strings[1].equalsIgnoreCase("boss")) {
                        String zindan = strings[2];
                        Dungeon dungeon = new Dungeon(zindan);
                        if(dungeon.exists()) {
                            DatabaseManager databaseManager = new DatabaseManager(get());
                            databaseManager.setBossID(strings[2], strings[3]);
                            dungeonEntity.EntityChatManager().send(new LanguageFile().getLanguage().getString("messages.update.boss").replace("#value", strings[3]).replace("#name", strings[2]));
                        } else {
                            dungeonEntity.EntityChatManager().send(new LanguageFile().getLanguage().getString("messages.dungeon.unknow-dungeon").replace("#name", strings[2]));
                        }
                    } else {
                        help(commandSender);
                    }
                } else if (strings[0].equalsIgnoreCase("point")) {
                    Player player = Bukkit.getPlayer(strings[2]);
                    if(player != null) {
                        if(isNumeric(strings[3])) {
                            PlayerMethods playerMethods = new PlayerMethods();
                            DungeonPlayer dungeonPlayer = new DungeonPlayer(player);
                            int a = Integer.parseInt(strings[3]);
                            if (strings[1].equalsIgnoreCase("give")) {
                                playerMethods.updatePoint(player, a);
                                dungeonEntity.EntityChatManager().send(new LanguageFile().getLanguage().getString("messages.point.admin.gived").replace("#player", player.getName()).replace("#amount", String.valueOf(a)));
                                dungeonPlayer.messageManager().title(new LanguageFile().getLanguage().getString("messages.point.player.gived.title").replace("#player", player.getName()).replace("#amount", String.valueOf(a)),new LanguageFile().getLanguage().getString("messages.point.player.gived.subtitle").replace("#player", player.getName()).replace("#amount", String.valueOf(a)));
                            } else if (strings[1].equalsIgnoreCase("take")) {
                                playerMethods.removePoint(player, a);
                                dungeonEntity.EntityChatManager().send(new LanguageFile().getLanguage().getString("messages.point.admin.taked").replace("#player", player.getName()).replace("#amount", String.valueOf(a)));
                                dungeonPlayer.messageManager().title(new LanguageFile().getLanguage().getString("messages.point.player.taked.title").replace("#player", player.getName()).replace("#amount", String.valueOf(a)),new LanguageFile().getLanguage().getString("messages.point.player.taked.subtitle").replace("#player", player.getName()).replace("#amount", String.valueOf(a)));
                            } else if (strings[1].equalsIgnoreCase("set")) {
                                playerMethods.setPoint(player, a);
                                dungeonEntity.EntityChatManager().send(new LanguageFile().getLanguage().getString("messages.point.admin.set").replace("#player", player.getName()).replace("#amount", String.valueOf(a)));
                                dungeonPlayer.messageManager().title(new LanguageFile().getLanguage().getString("messages.point.player.set.title").replace("#player", player.getName()).replace("#amount", String.valueOf(a)),new LanguageFile().getLanguage().getString("messages.point.player.set.subtitle").replace("#player", player.getName()).replace("#amount", String.valueOf(a)));
                            } else if (strings[1].equalsIgnoreCase("reset")) {
                                playerMethods.resetPoint(player);
                                dungeonEntity.EntityChatManager().send(new LanguageFile().getLanguage().getString("messages.point.admin.reset").replace("#player", player.getName()).replace("#amount", String.valueOf(a)));
                                dungeonPlayer.messageManager().title(new LanguageFile().getLanguage().getString("messages.point.player.reset.title").replace("#player", player.getName()).replace("#amount", String.valueOf(a)),new LanguageFile().getLanguage().getString("messages.point.player.reset.subtitle").replace("#player", player.getName()).replace("#amount", String.valueOf(a)));
                            } else {
                                help(commandSender);
                            }
                        } else {
                            dungeonEntity.EntityChatManager().send(new LanguageFile().getLanguage().getString("messages.dungeon.canceled.must-be-number"));
                        }
                    } else {
                        dungeonEntity.EntityChatManager().send(new LanguageFile().getLanguage().getString("messages.error.player-not-online"));
                    }
                } else {
                    help(commandSender);
                }
            } else {
                help(commandSender);
            }
        } else {
            dungeonEntity.EntityChatManager().send(new LanguageFile().getLanguage().getString("messages.no-permission"));
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
            commands.add("point");
            commands.add("hook");
            commands.add("unload");
            commands.add("create");
            commands.add("challange");
            commands.add("genus-tools");
            commands.add("genus-gui");
            commands.add("test");
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
            if (args[0].equalsIgnoreCase("genus-tools")) {
                commands.add("GHOST");
                StringUtil.copyPartialMatches(args[1], commands, completions);
            }
            if (args[0].equalsIgnoreCase("hook")) {
                commands.add("Vault");
                StringUtil.copyPartialMatches(args[1], commands, completions);
            }
            if (args[0].equalsIgnoreCase("point")) {
                commands.add("give");
                commands.add("take");
                commands.add("set");
                commands.add("reset");
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
            if (args[0].equalsIgnoreCase("point")) {
                for(Player player : Bukkit.getOnlinePlayers()) {
                    commands.add(player.getName());
                }
                StringUtil.copyPartialMatches(args[2], commands, completions);
            }
            if (args[0].equalsIgnoreCase("update")) {
                DatabaseManager databaseManager = new DatabaseManager(get());
                for(int i = 0; i<databaseManager.getAllDungeons().size(); i++) {
                    commands.add(databaseManager.getAllDungeons().get(i));
                }
                StringUtil.copyPartialMatches(args[2], commands, completions);
            }
        } else if (args.length == 4) {
            if (args[0].equalsIgnoreCase("point")) {
                commands.add("1");
                commands.add("10");
                commands.add("100");
                commands.add("1000");
                commands.add("10000");
                StringUtil.copyPartialMatches(args[3], commands, completions);
            }
        }
        Collections.sort(completions);
        return completions;
    }

}
