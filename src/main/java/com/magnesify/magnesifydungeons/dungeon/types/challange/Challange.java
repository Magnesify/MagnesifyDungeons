package com.magnesify.magnesifydungeons.dungeon.types.challange;

import com.magnesify.magnesifydungeons.MagnesifyDungeons;
import com.magnesify.magnesifydungeons.boss.MagnesifyBoss;
import com.magnesify.magnesifydungeons.dungeon.entitys.DungeonPlayer;
import com.magnesify.magnesifydungeons.languages.LanguageFile;
import com.magnesify.magnesifydungeons.modules.managers.DatabaseManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.magnesify.magnesifydungeons.MagnesifyDungeons.get;

public class Challange {

    private static String name;
    private static String category;
    private static String boss_id;
    private static Location location;
    private static int level, PlayTime, StartTime;

    private HashMap<String, Integer> countdownTime = new HashMap<>();

    /**
     * Oluşmuş zindandan veri çekmek için
     */
    public Challange(String name) {
        this.name = name;
    }

    /**
     * Oluşmuş zindandan veri çekmek için
     */
    public Challange(String name, String category) {
        this.category = category;
        this.name = name;
    }

    /**
     * Zindan oluşturmak için
     */
    public Challange(String name, String category, int level, String boss_id, int StartTime, int PlayTime, Location location) {
        this.name = name;
        this.category = category;
        this.level = level;
        this.boss_id = boss_id;
        this.StartTime = StartTime;
        this.PlayTime = PlayTime;
        this.location = location;
    }


    public boolean create() {
        DatabaseManager databaseManager = new DatabaseManager(get());
        if(!databaseManager.isDungeonExists("challange_"+name)) {
            databaseManager.CreateNewDungeon("challange_" + name, category, boss_id, level, PlayTime, StartTime, location);
            return true;
        } else {
            return false;
        }
    }

    public boolean delete() {
        DatabaseManager databaseManager = new DatabaseManager(get());
        databaseManager.deleteDungeon("challange_"+name);
        return false;
    }

    public Types update() {
        return new Types();
    }

    public Events events() {
        return new Events();
    }

    public void status(boolean bool) {
        DatabaseManager databaseManager = new DatabaseManager(get());
        databaseManager.setStatus("challange_"+name, bool);
    }

    public boolean getStatus() {
        DatabaseManager databaseManager = new DatabaseManager(get());
        return databaseManager.getStatus("challange_"+name);
    }

    public int countdown() {
        if(countdownTime.get("challange_"+name) != null) {
            return countdownTime.get("challange_"+name);
        }
        return 0;
    }


    public int point() {
        DatabaseManager databaseManager = new DatabaseManager(get());
        return databaseManager.getPoint("challange_"+name);
    }

    public String currentPlayer() {
        DatabaseManager databaseManager = new DatabaseManager(get());
        return databaseManager.getCurrentPlayer("challange_"+name);
    }

    public void updateCurrentPlayer(String player) {
        DatabaseManager databaseManager = new DatabaseManager(get());
        databaseManager.setCurrentPlayer("challange_"+name, player);
    }

    public static Location location(String name) {
        DatabaseManager databaseManager = new DatabaseManager(get());
        return databaseManager.getLocation(name);
    }

    public Parameters parameters() {
        return new Parameters();
    }

    public boolean exists() {
        DatabaseManager databaseManager = new DatabaseManager(get());
        return databaseManager.isDungeonExists("challange_"+name);
    }

    public static class Parameters {
        DatabaseManager databaseManager = new DatabaseManager(get());
        public String name() {
            return databaseManager.getName("challange_"+name);
        }
        public String boss() {
            return databaseManager.getBoss("challange_"+name);
        }
        public String category() {
            return databaseManager.getCategory("challange_"+name);
        }
        public boolean status() {
            return databaseManager.getStatus("challange_"+name);
        }
        public int next() {
            return databaseManager.getNextLevel("challange_"+name);
        }
        public int point() {
            return databaseManager.getPoint("challange_"+name);
        }
        public int level() {
            return databaseManager.getLevel("challange_"+name);
        }
        public int play() {
            return databaseManager.getPlayTime("challange_"+name);
        }
        int start() {
            return databaseManager.getStartTime("challange_"+name);
        }
    }

    public static class Types {
        DatabaseManager databaseManager = new DatabaseManager(get());
        void name(String new_data) {
            databaseManager.setName("challange_"+name, new_data);
        }

        void category(String new_data) {
            databaseManager.setCategory("challange_"+name, new_data);
        }

        void level(int new_data) {
            databaseManager.setLevel("challange_"+name, new_data);
        }

        void bossId(String new_data) {
            databaseManager.setBossID("challange_"+name, new_data);
        }

    }

    public class Events {
        private final Map<UUID, Integer> countdowns = new HashMap<>();
        public Events(){}

        public void stop(Player player) {
            countdowns.remove(player.getUniqueId());
        }

        public void wait(Player player, Challange dungeon) {
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
                            dungeonPlayer.messageManager().actionbar(new LanguageFile().getLanguage(MagnesifyDungeons.locale).getString("messages.dungeon.entering").replace("#custom_commands[cancel]", get().getConfig().getString("settings.custom-commands.cancel")).replace("#countdown", String.valueOf(countdowns.get(playerId))));
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

        public void start(Player player, String boss, Location location, Challange dungeon) {
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
                            dungeonPlayer.messageManager().actionbar(new LanguageFile().getLanguage(MagnesifyDungeons.locale).getString("messages.dungeon.starting").replace("#custom_commands[leave]", get().getConfig().getString("settings.custom-commands.cancel")).replace("#countdown", String.valueOf(countdowns.get(playerId))));
                            player.playSound(player.getLocation(), Sound.valueOf(get().getConfig().getString("settings.sounds.starting")), 3.0F, 0.5F);
                        } else {
                            countdowns.remove(playerId);
                            this.cancel();
                            MagnesifyBoss magnesifyBoss = new MagnesifyBoss(boss);
                            if(magnesifyBoss.exists()) {
                                magnesifyBoss.spawn(location, player);
                            } else {
                                dungeonPlayer.leaveChallange(dungeon);
                                Bukkit.getConsoleSender().sendMessage(String.format(new LanguageFile().getLanguage("tr").getString("plugin.unknow-boss"),magnesifyBoss.name()));
                            }
                            play(player, parameters().name(), dungeon);

                        }
                    } else {
                        this.cancel();
                    }
                }
            }.runTaskTimer(get(), 0, 20);
        }

        public void play(Player player, String name, Challange dungeon) {
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
                                dungeonPlayer.messageManager().actionbar(new LanguageFile().getLanguage(MagnesifyDungeons.locale).getString("messages.dungeon.playing").replace("#custom_commands[leave]", get().getConfig().getString("settings.custom-commands.leave")).replace("#countdown", String.valueOf(countdowns.get(playerId))));
                            } else {
                                if(dungeon.getStatus()) {
                                    dungeonPlayer.leaveChallange(dungeon);
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
