package com.magnesify.magnesifydungeons.dungeon;

import com.magnesify.magnesifydungeons.boss.MagnesifyBoss;
import com.magnesify.magnesifydungeons.dungeon.entitys.DungeonConsole;
import com.magnesify.magnesifydungeons.dungeon.entitys.DungeonPlayer;
import com.magnesify.magnesifydungeons.languages.LanguageFile;
import com.magnesify.magnesifydungeons.modules.Defaults;
import com.magnesify.magnesifydungeons.modules.managers.DatabaseManager;
import com.magnesify.magnesifydungeons.modules.managers.DungeonContentManager;
import com.magnesify.magnesifydungeons.storage.PlayerMethods;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.magnesify.magnesifydungeons.MagnesifyDungeons.get;

public class TriggerType {
    private HashMap<String, Integer> countdownTime = new HashMap<>();
    public static HashMap<UUID, Boolean> inGameHashMap = new HashMap<>();
    public static HashMap<UUID, Integer> level = new HashMap<>();

    public Player player;

    public TriggerType(Player player) {
        this.player = player;
    }

    public void status(String name, boolean bool) {
        DatabaseManager databaseManager = new DatabaseManager(get());
        databaseManager.TriggerTypeDungeons().setStatus(name, bool);
    }

    public boolean getStatus(String name) {
        DatabaseManager databaseManager = new DatabaseManager(get());
        return databaseManager.TriggerTypeDungeons().getStatus(name);
    }

    public int point(String name) {
        DatabaseManager databaseManager = new DatabaseManager(get());
        return databaseManager.TriggerTypeDungeons().getPoint(name);
    }

    public String currentPlayer(String name) {
        DatabaseManager databaseManager = new DatabaseManager(get());
        return databaseManager.TriggerTypeDungeons().getCurrentPlayer(name);
    }

    public void updateCurrentPlayer(String name, String player) {
        DatabaseManager databaseManager = new DatabaseManager(get());
        databaseManager.TriggerTypeDungeons().setCurrentPlayer(name, player);
    }

    public boolean join(String dungeon) {
        PlayerMethods playerMethods = new PlayerMethods(player);
        if (!playerMethods.inDungeon(player)) {
            playerMethods.updateLastDungeon(player, dungeon);
            playerMethods.updateLastBoss(player, parameters(dungeon).boss());
            playerMethods.updateDungeonStatus(player, true);
            inGameHashMap.put(player.getUniqueId(), true);
            playerMethods.setDone(player, false);
            events().wait(player, dungeon);
            DungeonContentManager dungeonContentManager = new DungeonContentManager();
            dungeonContentManager.SetupDungeonChests(dungeon);
            updateCurrentPlayer(dungeon, player.getName());
            status(dungeon, false);
            return true;
        } else {
            return false;
        }
    }

    private final Map<UUID, Integer> countdowns = new HashMap<>();

    public void sendspawn(Player player) {
        countdowns.put(player.getUniqueId(), get().getConfig().getInt("settings.minimal-options.teleport-delay-for-sending-spawn"));
        new BukkitRunnable() {
            @Override
            public void run() {
                UUID playerId = player.getUniqueId();
                if (countdowns.containsKey(playerId)) {
                    int remainingTime = countdowns.get(playerId);
                    if (remainingTime > 0) {
                        countdowns.put(playerId, remainingTime - 1); // Geri sayım süresini azaltma
                    } else {
                        countdowns.remove(playerId);
                        this.cancel();
                        Defaults defaults = new Defaults();
                        DungeonConsole dungeonConsole = new DungeonConsole();
                        if(defaults.MainSpawn().world() != null) {
                            if (Bukkit.getWorld(defaults.MainSpawn().world()) != null) {
                                Location loc = new Location(Bukkit.getWorld(defaults.MainSpawn().world()), defaults.MainSpawn().x(), defaults.MainSpawn().y(), defaults.MainSpawn().z(), (float) defaults.MainSpawn().yaw(), (float) defaults.MainSpawn().pitch());
                                player.teleport(loc);
                            } else {
                                dungeonConsole.ConsoleOutputManager().write( new LanguageFile().getLanguage().getString("plugin.spawn-not-exists"));
                            }
                        } else {
                            dungeonConsole.ConsoleOutputManager().write( new LanguageFile().getLanguage().getString("plugin.spawn-not-exists"));
                        }
                    }
                } else {
                    this.cancel();
                }
            }
        }.runTaskTimer(get(), 0, 20);
    }

    public void leave(Player player, String dungeon) {
        PlayerMethods playerMethods = new PlayerMethods(player);
        DatabaseManager databaseManager = new DatabaseManager(get());
        databaseManager.TriggerTypeDungeons().setAvailable(dungeon, true);
        status(dungeon, true);
        playerMethods.updateDungeonStatus(player, false);
        playerMethods.setDone(player, true);
        events().stop(player);
        sendspawn(player);
    }

    public Parameters parameters(String dungeon) {
        return new Parameters(dungeon);
    }

    public static class Parameters {
        private DatabaseManager databaseManager = new DatabaseManager(get());

        public String name;

        public Parameters(String dungeon) {
            this.name = dungeon;
        }

        public String name() {
            return databaseManager.TriggerTypeDungeons().getName(name);
        }
        public String boss() {
            return databaseManager.TriggerTypeDungeons().getBoss(name);
        }
        public String category() {
            return databaseManager.TriggerTypeDungeons().getCategory(name);
        }
        public boolean status() {
            return databaseManager.TriggerTypeDungeons().getStatus(name);
        }
        public int next() {
            return databaseManager.TriggerTypeDungeons().getNextLevel(name);
        }
        public int point() {
            return databaseManager.TriggerTypeDungeons().getPoint(name);
        }
        public int level() {
            return databaseManager.TriggerTypeDungeons().getLevel(name);
        }
        public int play() {
            return databaseManager.TriggerTypeDungeons().getPlayTime(name);
        }
        int start() {
            return databaseManager.TriggerTypeDungeons().getStartTime(name);
        }
    }

    public Events events() {
        return new Events();
    }


    public static Location location(String name) {
        DatabaseManager databaseManager = new DatabaseManager(get());
        return databaseManager.TriggerTypeDungeons().getLocation(name);
    }

    public static void SpawnFirstLevelMob(String dungeon, Location location, Player player) {
        String boss = new DatabaseManager(get()).TriggerTypeDungeons().getCheckpointBoss(dungeon, 1);
        MagnesifyBoss magnesifyBoss = new MagnesifyBoss(boss);
        if(magnesifyBoss.exists()) {
            magnesifyBoss.spawn(location, player);
            new PlayerMethods().updateLastBoss(player, new DatabaseManager(get()).boss().getMGID(boss));
        }
    }

    public static Location bosspoints(String name, int lvl) {
        DatabaseManager databaseManager = new DatabaseManager(get());
        return databaseManager.TriggerTypeDungeons().getBosspointsLocation(name, lvl);
    }

    public class Events {

        private final Map<UUID, Integer> countdowns = new HashMap<>();

        public Events(){}

        public void stop(Player player) {
            countdowns.remove(player.getUniqueId());
        }

        public void wait(Player player, String dungeon) {
            countdowns.put(player.getUniqueId(), parameters(dungeon).start());
            DungeonPlayer dungeonPlayer = new DungeonPlayer(player);
            new BukkitRunnable() {
                @Override
                public void run() {
                    UUID playerId = player.getUniqueId();
                    if (countdowns.containsKey(playerId)) {
                        int remainingTime = countdowns.get(playerId);
                        if (remainingTime > 0) {
                            countdowns.put(playerId, remainingTime - 1); // Geri sayım süresini azaltma
                            dungeonPlayer.messageManager().actionbar(new LanguageFile().getLanguage().getString("messages.dungeon.entering").replace("#custom_commands[cancel]", get().getConfig().getString("settings.custom-commands.leave")).replace("#countdown", String.valueOf(countdowns.get(playerId))));
                            player.playSound(player.getLocation(), Sound.valueOf(get().getConfig().getString("settings.sounds.entering")), 3.0F, 0.5F);
                        } else {
                            player.teleport(location(parameters(dungeon).name()));
                            level.put(playerId, 1);
                            countdowns.remove(playerId);
                            this.cancel();
                            start(player, parameters(dungeon).boss(), dungeon);
                        }
                    } else {
                        this.cancel();
                    }
                }
            }.runTaskTimer(get(), 0, 20);
        }

        public void start(Player player, String boss, String dungeon) {
            countdowns.put(player.getUniqueId(), parameters(dungeon).start());
            DungeonPlayer dungeonPlayer = new DungeonPlayer(player);
            new BukkitRunnable() {
                @Override
                public void run() {
                    UUID playerId = player.getUniqueId();
                    if (countdowns.containsKey(playerId)) {
                        int remainingTime = countdowns.get(playerId);
                        if (remainingTime > 0) {
                            countdowns.put(playerId, remainingTime - 1); // Geri sayım süresini azaltma
                            dungeonPlayer.messageManager().actionbar(new LanguageFile().getLanguage().getString("messages.dungeon.starting").replace("#custom_commands[leave]", get().getConfig().getString("settings.custom-commands.leave")).replace("#countdown", String.valueOf(countdowns.get(playerId))));
                            player.playSound(player.getLocation(), Sound.valueOf(get().getConfig().getString("settings.sounds.starting")), 3.0F, 0.5F);
                        } else {
                            countdowns.remove(playerId);
                            this.cancel();
                            SpawnFirstLevelMob(dungeon, bosspoints(dungeon, 1), player);
                            play(player, parameters(dungeon).name(), dungeon);
                        }
                    } else {
                        this.cancel();
                    }
                }
            }.runTaskTimer(get(), 0, 20);
        }

        public void play(Player player, String name, String dungeon) {
            countdownTime.put(name, parameters(dungeon).play());
            countdowns.put(player.getUniqueId(), parameters(dungeon).play());
            inGameHashMap.put(player.getUniqueId(), false);
            DungeonPlayer dungeonPlayer = new DungeonPlayer(player);
            new BukkitRunnable() {
                @Override
                public void run() {
                    UUID playerId = player.getUniqueId();
                    if(!dungeonPlayer.done()) {
                        if (countdowns.containsKey(playerId)) {
                            int remainingTime = countdowns.get(playerId);
                            int countTime = countdownTime.get(name);
                            if (remainingTime > 0) {
                                countdownTime.put(name, countTime-1);
                                countdowns.put(playerId, remainingTime - 1);
                                dungeonPlayer.messageManager().actionbar(new LanguageFile().getLanguage().getString("messages.dungeon.playing").replace("#custom_commands[leave]", get().getConfig().getString("settings.custom-commands.leave")).replace("#countdown", String.valueOf(countdowns.get(playerId))));
                                for(String warn : get().getConfig().getConfigurationSection("settings.countdown-warnings").getKeys(false)) {
                                    int warn_at = get().getConfig().getInt("settings.countdown-warnings." + warn + ".warn-at");
                                    if(remainingTime == warn_at) {
                                        dungeonPlayer.messageManager().title(new LanguageFile().getLanguage().getString("messages.countdown-warn.title"), new LanguageFile().getLanguage().getString("messages.countdown-warn.subtitle").replace("#remain", String.valueOf(remainingTime)));
                                        return;
                                    }
                                }
                            } else {
                                leave(player, dungeon);
                                PlayerMethods playerMethods = new PlayerMethods(player);
                                playerMethods.updateDungeonStatus(player, false);
                                String last_dungeon= get().getPlayers().getLastBoss(player);
                                MagnesifyBoss magnesifyBoss = new MagnesifyBoss();
                                magnesifyBoss.killBoss(last_dungeon);
                                dungeonPlayer.messageManager().title(new LanguageFile().getLanguage().getString("messages.status.lose.title"), new LanguageFile().getLanguage().getString("messages.status.lose.time-subtitle"));
                                get().getPlayers().updateDungeonStatus(player, false);
                                countdowns.remove(playerId);
                                countdownTime.remove(name);
                                this.cancel();

                            }
                        } else {
                            this.cancel();
                        }
                    } else {
                        this.cancel();
                    }
                }
            }.runTaskTimer(get(), 0, 20);
        }
    }

}
