package com.magnesify.magnesifydungeons.dungeon;

import com.magnesify.magnesifydungeons.boss.BossManager;
import com.magnesify.magnesifydungeons.boss.MagnesifyBoss;
import com.magnesify.magnesifydungeons.dungeon.entitys.DungeonPlayer;
import com.magnesify.magnesifydungeons.dungeon.modules.DungeonManagementHandler;
import com.magnesify.magnesifydungeons.files.Dungeons;
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
    private static Dungeons dungeons;
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
     * Zindan oluşturmak için
     */
    public Dungeon(Dungeons dungeonFile, String name, String category, int level, String boss_id, int StartTime, int PlayTime, Location location) {
        this.name = name;
        this.dungeons = dungeonFile;
        this.category = category;
        this.level = level;
        this.boss_id = boss_id;
        this.StartTime = StartTime;
        this.PlayTime = PlayTime;
        this.location = location;
    }


    @Override
    public boolean create() {
        if(dungeons.get().getString("dungeons." + name) == null) {
            dungeons.get().set("dungeons." + name + ".name", name);
            dungeons.get().set("dungeons." + name + ".available", true);
            dungeons.get().set("dungeons." + name + ".current-player", "None");
            dungeons.get().set("dungeons." + name + ".category", category);
            dungeons.get().set("dungeons." + name + ".current-level", level);
            dungeons.get().set("dungeons." + name + ".next-level", level+1);
            dungeons.get().set("dungeons." + name + ".point", 30);
            dungeons.get().set("dungeons." + name + ".boss-id", boss_id);
            dungeons.get().set("dungeons." + name + ".play-time", PlayTime);
            dungeons.get().set("dungeons." + name + ".start-time", StartTime);
            dungeons.get().set("dungeons." + name + ".location.world", location.getWorld().getName());
            dungeons.get().set("dungeons." + name + ".location.x", location.getX());
            dungeons.get().set("dungeons." + name + ".location.y", location.getY());
            dungeons.get().set("dungeons." + name + ".location.z", location.getZ());
            dungeons.get().set("dungeons." + name + ".location.yaw", location.getYaw());
            dungeons.get().set("dungeons." + name + ".location.pitch", location.getPitch());
            dungeons.save();
            return true;
        }
        return false;
    }

    @Override
    public boolean delete() {
        Dungeons dungeons = new Dungeons();
        dungeons.get().set("dungeons." + name, null);
        dungeons.get().getConfigurationSection("dungeons").getKeys(false).remove("dungeons." + name);
        dungeons.save();
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
        Dungeons dungeons = new Dungeons();
        dungeons.get().set("dungeons." + name + ".available", bool);
        dungeons.save();
    }

    public int countdown() {
        if(countdownTime.get(name) != null) {
            return countdownTime.get(name);
        }
        return 0;
    }


    public int point() {
        Dungeons dungeons = new Dungeons();;
        return dungeons.get().getInt("dungeons." + name + ".point");
    }

    public String currentPlayer() {
        Dungeons dungeons = new Dungeons();;
        return dungeons.get().getString("dungeons." + name + ".current-player");
    }

    public void updateCurrentPlayer(String player) {
        Dungeons dungeons = new Dungeons();;
        dungeons.get().set("dungeons." + name + ".current-player", player);
        dungeons.save();
    }

    public static Location location(String name) {
        Dungeons dungeons = new Dungeons();
        double yaw = dungeons.get().getDouble("dungeons." + name + ".location.yaw");
        double pitch = dungeons.get().getDouble("dungeons." + name + ".location.pitch");
        int x = dungeons.get().getInt("dungeons." + name + ".location.x");
        int y = dungeons.get().getInt("dungeons." + name + ".location.y");
        int z = dungeons.get().getInt("dungeons." + name + ".location.z");
        String world = dungeons.get().getString("dungeons." + name + ".location.world");
        if(Bukkit.getWorld(world) == null) return null;
        return new Location(Bukkit.getWorld(world), x, y, z, (float) yaw, (float) pitch);
    }

    public Parameters parameters() {
        return new Parameters();
    }

    @Override
    public boolean exists() {
        return dungeons.get().getString("dungeons." + name) != null;
    }

    public static List<String> list() {
        List<String> a = new ArrayList<>();
        Dungeons dungeons = new Dungeons();
        if(dungeons.get().getConfigurationSection("dungeons").getKeys(false).isEmpty()) return null;
        a.addAll(dungeons.get().getConfigurationSection("dungeons").getKeys(false));
        return a;
    }

    public static class Parameters {
        Dungeons dungeons_ = new Dungeons();
        public String name() {
            return dungeons_.get().getString("dungeons." + name + ".name");
        }
        public String boss() {
            return dungeons_.get().getString("dungeons." + name + ".boss-id");
        }
        public String category() {
            return dungeons_.get().getString("dungeons." + name + ".category");
        }
        public boolean status() {
            return dungeons_.get().getBoolean("dungeons." + name + ".available");
        }
        public int next() {
            return dungeons_.get().getInt("dungeons." + name + ".next-level");
        }
        public int level() {
            return dungeons_.get().getInt("dungeons." + name + ".current-level");
        }
        public int play() {
            return dungeons_.get().getInt("dungeons." + name + ".play-time");
        }
        int start() {
            return dungeons_.get().getInt("dungeons." + name + ".start-time");
        }
    }

    public static class Types {
        Dungeons dungeons_ = new Dungeons();
        void name(String new_data) {
            dungeons_.get().set("dungeons." + name + ".name", new_data);
            dungeons_.save();
        }

        void category(String new_data) {
            dungeons_.get().set("dungeons." + name + ".category", new_data);
            dungeons_.save();
        }

        void level(int new_data) {
            dungeons_.get().set("dungeons." + name + ".level", new_data);
            dungeons_.save();
        }

        void bossId(int new_data) {
            dungeons_.get().set("dungeons." + name + ".boss-id", new_data);
            dungeons_.save();
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
                            start(player, dungeon.parameters().name(), location(dungeon.parameters().name()), dungeon);
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
                                dungeonPlayer.leave(dungeon);
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
