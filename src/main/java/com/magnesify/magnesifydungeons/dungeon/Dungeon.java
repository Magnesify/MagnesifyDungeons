package com.magnesify.magnesifydungeons.dungeon;

import com.magnesify.magnesifydungeons.boss.MagnesifyBoss;
import com.magnesify.magnesifydungeons.dungeon.entitys.DungeonPlayer;
import com.magnesify.magnesifydungeons.dungeon.modules.DungeonManagementHandler;
import com.magnesify.magnesifydungeons.modules.managers.DatabaseManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

import static com.magnesify.magnesifydungeons.MagnesifyDungeons.get;
import static com.magnesify.magnesifydungeons.dungeon.entitys.DungeonPlayer.parseHexColors;

public class Dungeon implements DungeonManagementHandler {

    private static String name;
    private static String category;
    private static String boss_id;
    private static Location location;
    private static int level, PlayTime, StartTime;

    private HashMap<String, Integer> countdownTime = new HashMap<>();

    /**
     * Oluşmuş zindandan veri çekmek için
     */
    public Dungeon(String name) {
        this.name = name;
    }

    /**
     * Oluşmuş zindandan veri çekmek için
     */
    public Dungeon(String name, String category) {
        this.category = category;
        this.name = name;
    }

    /**
     * Zindan oluşturmak için
     */
    public Dungeon(String name, String category, int level, String boss_id, int StartTime, int PlayTime, Location location) {
        this.name = name;
        this.category = category;
        this.level = level;
        this.boss_id = boss_id;
        this.StartTime = StartTime;
        this.PlayTime = PlayTime;
        this.location = location;
    }


    @Override
    public boolean create() {
        DatabaseManager databaseManager = new DatabaseManager(get());
        if(!databaseManager.isDungeonExists(name)) {
            databaseManager.CreateNewDungeon(name, category, boss_id, level, PlayTime, StartTime, location);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean delete() {
        DatabaseManager databaseManager = new DatabaseManager(get());
        databaseManager.deleteDungeon(name);
        return false;
    }

    @Override
    public Types update() {
        return new Types();
    }

    @Override
    public Events events() {
        return new Events();
    }

    public void status(boolean bool) {
        DatabaseManager databaseManager = new DatabaseManager(get());
        databaseManager.setStatus(name, bool);
    }

    public boolean getStatus() {
        DatabaseManager databaseManager = new DatabaseManager(get());
        return databaseManager.getStatus(name);
    }

    public int countdown() {
        if(countdownTime.get(name) != null) {
            return countdownTime.get(name);
        }
        return 0;
    }


    public int point() {
        DatabaseManager databaseManager = new DatabaseManager(get());
        return databaseManager.getPoint(name);
    }

    public String currentPlayer() {
        DatabaseManager databaseManager = new DatabaseManager(get());
        return databaseManager.getCurrentPlayer(name);
    }

    public void updateCurrentPlayer(String player) {
        DatabaseManager databaseManager = new DatabaseManager(get());
        databaseManager.setCurrentPlayer(name, player);
    }

    public static Location location(String name) {
        DatabaseManager databaseManager = new DatabaseManager(get());
        return databaseManager.getLocation(name);
    }

    public Parameters parameters() {
        return new Parameters();
    }

    @Override
    public boolean exists() {
        DatabaseManager databaseManager = new DatabaseManager(get());
        return databaseManager.isDungeonExists(name);
    }

    public static class Parameters {
        DatabaseManager databaseManager = new DatabaseManager(get());
        public String name() {
            return databaseManager.getName(name);
        }
        public String boss() {
            return databaseManager.getBoss(name);
        }
        public String category() {
            return databaseManager.getCategory(name);
        }
        public boolean status() {
            return databaseManager.getStatus(name);
        }
        public int next() {
            return databaseManager.getNextLevel(name);
        }
        public int point() {
            return databaseManager.getPoint(name);
        }
        public int level() {
            return databaseManager.getLevel(name);
        }
        public int play() {
            return databaseManager.getPlayTime(name);
        }
        int start() {
            return databaseManager.getStartTime(name);
        }
    }

    public static class Types {
        DatabaseManager databaseManager = new DatabaseManager(get());
        void name(String new_data) {
            databaseManager.setName(name, new_data);
        }

        void category(String new_data) {
            databaseManager.setCategory(name, new_data);
        }

        void level(int new_data) {
            databaseManager.setLevel(name, new_data);
        }

        void bossId(String new_data) {
            databaseManager.setBossID(name, new_data);
        }

    }

    public class Events {
        private final Map<UUID, Integer> countdowns = new HashMap<>();
        public Events(){}

        public void stop(Player player) {
            countdowns.remove(player.getUniqueId());
        }

        public void wait(Player player, Dungeon dungeon) {
            countdowns.put(player.getUniqueId(), parameters().start());
            DungeonPlayer dungeonPlayer = new DungeonPlayer(player);
            new BukkitRunnable() {
                @Override
                public void run() {
                    UUID playerId = player.getUniqueId();
                    if (countdowns.containsKey(playerId)) {
                        int remainingTime = countdowns.get(playerId);
                        if (remainingTime > 0) {
                            countdowns.put(playerId, remainingTime - 1); // Geri sayım süresini azaltma
                            dungeonPlayer.messageManager().actionbar(get().getConfig().getString("settings.messages.dungeon.entering").replace("#custom_commands[cancel]", get().getConfig().getString("settings.custom-commands.cancel")).replace("#countdown", String.valueOf(countdowns.get(playerId))));
                            player.playSound(player.getLocation(), Sound.valueOf(get().getConfig().getString("settings.sounds.entering")), 3.0F, 0.5F);
                        } else {
                            player.teleport(location(parameters().name()));
                            countdowns.remove(playerId);
                            this.cancel();
                            start(player, dungeon.parameters().boss(), location(dungeon.parameters().name()), dungeon);
                        }
                    } else {
                        this.cancel();
                    }
                }
            }.runTaskTimer(get(), 0, 20);
        }

        public void start(Player player, String boss, Location location, Dungeon dungeon) {
            countdowns.put(player.getUniqueId(), parameters().start());
            DungeonPlayer dungeonPlayer = new DungeonPlayer(player);
            new BukkitRunnable() {
                @Override
                public void run() {
                    UUID playerId = player.getUniqueId();
                    if (countdowns.containsKey(playerId)) {
                        int remainingTime = countdowns.get(playerId);
                        if (remainingTime > 0) {
                            countdowns.put(playerId, remainingTime - 1); // Geri sayım süresini azaltma
                            dungeonPlayer.messageManager().actionbar(get().getConfig().getString("settings.messages.dungeon.starting").replace("#custom_commands[leave]", get().getConfig().getString("settings.custom-commands.cancel")).replace("#countdown", String.valueOf(countdowns.get(playerId))));
                            player.playSound(player.getLocation(), Sound.valueOf(get().getConfig().getString("settings.sounds.starting")), 3.0F, 0.5F);
                        } else {
                            countdowns.remove(playerId);
                            this.cancel();
                            MagnesifyBoss magnesifyBoss = new MagnesifyBoss(boss);
                            if(magnesifyBoss.exists()) {
                                magnesifyBoss.spawn(location, player);
                            } else {
                                dungeonPlayer.leave(dungeon);
                                Bukkit.getConsoleSender().sendMessage(parseHexColors("<#5794ff>[MagnesifyDungeons] &fBoss bulunamadı, zindan için belirtilen boss idsi geçersiz."));
                            }
                            play(player, parameters().name(), dungeon);

                        }
                    } else {
                        this.cancel();
                    }
                }
            }.runTaskTimer(get(), 0, 20);
        }

        public void play(Player player, String name, Dungeon dungeon) {
            countdownTime.put(name, parameters().play());
            countdowns.put(player.getUniqueId(), parameters().play());
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
                                dungeonPlayer.messageManager().actionbar(get().getConfig().getString("settings.messages.dungeon.playing").replace("#custom_commands[leave]", get().getConfig().getString("settings.custom-commands.leave")).replace("#countdown", String.valueOf(countdowns.get(playerId))));
                            } else {
                                if(dungeon.getStatus()) {
                                    dungeonPlayer.leave(dungeon);
                                }
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
