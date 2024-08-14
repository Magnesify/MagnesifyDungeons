package com.magnesify.magnesifydungeons.modules.managers;

import com.magnesify.magnesifydungeons.MagnesifyDungeons;
import com.magnesify.magnesifydungeons.dungeon.Dungeon;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DatabaseManager {
    private final MagnesifyDungeons plugin;
    private HikariDataSource dataSource;

    public DatabaseManager(MagnesifyDungeons plugin) {
        this.plugin = plugin;
    }

    public void load() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:sqlite:" + plugin.getDataFolder() + "/magnesify.db");
        config.setMaximumPoolSize(10);
        config.setIdleTimeout(30000);
        config.setConnectionTimeout(30000);
        config.setMaxLifetime(1800000);
        dataSource = new HikariDataSource(config);
    }

    public void initialize() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:sqlite:" + plugin.getDataFolder() + "/magnesify.db");
        config.setMaximumPoolSize(10);
        config.setIdleTimeout(30000);
        config.setConnectionTimeout(30000);
        config.setMaxLifetime(1800000);

        dataSource = new HikariDataSource(config);

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS dungeons (name TEXT, available BOOLEAN, current_player TEXT, category TEXT, current_level INTEGER DEFAULT 0,world TEXT, x DOUBLE, y DOUBLE, z DOUBLE, yaw FLOAT, pitch FLOAT, boss_id TEXT, play_time INTEGER DEFAULT 3, start_time INTEGER DEFAULT 5, point INTEGER DEFAULT 0, next_level INTEGER DEFAULT 0, type TEXT, PRIMARY KEY(name, category))");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS trigger_type_dungeons (name TEXT, available BOOLEAN, current_player TEXT, category TEXT, current_level INTEGER DEFAULT 0,world TEXT, x DOUBLE, y DOUBLE, z DOUBLE, yaw FLOAT, pitch FLOAT, boss_id TEXT, play_time INTEGER DEFAULT 3, start_time INTEGER DEFAULT 5, point INTEGER DEFAULT 0, next_level INTEGER DEFAULT 0, type TEXT,checkpoint_amount INTEGER DEFAULT 1, enable TEXT DEFAULT 'Hayır', boss_world TEXT, boss_x DOUBLE, boss_y DOUBLE, boss_z DOUBLE, boss_yaw FLOAT, boss_pitch FLOAT, PRIMARY KEY(name, category))");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS boss (name TEXT, id TEXT, mgid TEXT, uuid TEXT,helmet TEXT,chestplate TEXT,leggings TEXT,boots TEXT,weapon TEXT,damage DOUBLE DEFAULT 0.0, knockback DOUBLE DEFAULT 0.0,max_health DOUBLE DEFAULT 0.0, drops TEXT DEFAULT 'GOLDEN_APPLE', type TEXT DEFAULT 'ZOMBIE', display TEXT DEFAULT '&cMagnesify', PRIMARY KEY(name, mgid))");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS stats (name TEXT,uuid TEXT, kill INTEGER DEFAULT 0, death INTEGER DEFAULT 0, win INTEGER DEFAULT 0, lose INTEGER DEFAULT 0, played_match INTEGER DEFAULT 0, PRIMARY KEY(name, uuid))");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS checkpoints (connected_dungeon TEXT, checkpoint_queue INTEGER,world TEXT, x DOUBLE, y DOUBLE, z DOUBLE, yaw FLOAT, pitch FLOAT ,boss TEXT, PRIMARY KEY(connected_dungeon, checkpoint_queue))");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS bosspoints (connected_dungeon TEXT, checkpoint_queue INTEGER,world TEXT, x DOUBLE, y DOUBLE, z DOUBLE, yaw FLOAT, pitch FLOAT , PRIMARY KEY(connected_dungeon, checkpoint_queue))");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS dungeon_chests (connected_dungeon TEXT, id INTEGER,world TEXT, x DOUBLE, y DOUBLE, z DOUBLE, yaw FLOAT, pitch FLOAT, PRIMARY KEY(connected_dungeon, id))");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<String> getChallangeNames() {
        load();
        List<String> names = new ArrayList<>();
        try (Connection conn = getConnection()) {
            if (conn != null) {
                String sql = "SELECT name FROM dungeons WHERE name LIKE 'challange_%'";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    ResultSet rs = pstmt.executeQuery();
                    while (rs.next()) {
                        String name = rs.getString("name");
                        names.add(name);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return names;
    }

    public Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("DataSource is not initialized");
        }
        return dataSource.getConnection();
    }

    public CompletableFuture<Boolean> CreateNewChest(String dungeon_name, Location location, int queue) {
        load();
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement("INSERT INTO dungeon_chests (connected_dungeon, world, x, y, z, yaw, pitch,id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {
                statement.setString(1, dungeon_name);
                statement.setString(2, location.getWorld().getName());
                statement.setDouble(3, location.getX());
                statement.setDouble(4, location.getY());
                statement.setDouble(5, location.getZ());
                statement.setFloat(6, location.getYaw());
                statement.setFloat(7, location.getPitch());
                statement.setInt(8, queue);
                return statement.executeUpdate() > 0;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        });
    }

    public CompletableFuture<Boolean> CreateNewCheckpoint(String dungeon_name, int queue, Location location, String boss) {
        load();
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement("INSERT INTO checkpoints (connected_dungeon, checkpoint_queue, world, x, y, z, yaw, pitch, boss) VALUES (?, ?, ?, ?, ?, ?, ?, ?,?)")) {
                statement.setString(1, dungeon_name);
                statement.setInt(2, queue);
                statement.setString(3, location.getWorld().getName());
                statement.setDouble(4, location.getX());
                statement.setDouble(5, location.getY());
                statement.setDouble(6, location.getZ());
                statement.setFloat(7, location.getYaw());
                statement.setFloat(8, location.getPitch());
                statement.setString(9, boss);
                return statement.executeUpdate() > 0;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        });
    }

    public Location getChestLocationByID(int id, String dungeon) {
        load();
        String query = "SELECT * FROM dungeon_chests WHERE id = ? AND connected_dungeon = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, id);
            pstmt.setString(2, dungeon);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String world = rs.getString("world");
                    double x = rs.getDouble("x");
                    double y = rs.getDouble("y");
                    double z = rs.getDouble("z");
                    float yaw = rs.getFloat("yaw");
                    float pitch = rs.getFloat("pitch");
                    return new Location(Bukkit.getWorld(world) == null ? null : Bukkit.getWorld(world), x, y, z, yaw, pitch);
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int getChestSize(String dungeon) {
        load();
        String query = "SELECT COUNT(*) FROM dungeon_chests WHERE connected_dungeon LIKE ?";
        try (Connection connection = getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setString(1, dungeon + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                } else {
                    return 0;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public CompletableFuture<Boolean> CreateNewBosspoints(String dungeon_name, int queue, Location location) {
        load();
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement("INSERT INTO bosspoints (connected_dungeon, checkpoint_queue, world, x, y, z, yaw, pitch) VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {
                statement.setString(1, dungeon_name);
                statement.setInt(2, queue);
                statement.setString(3, location.getWorld().getName());
                statement.setDouble(4, location.getX());
                statement.setDouble(5, location.getY());
                statement.setDouble(6, location.getZ());
                statement.setFloat(7, location.getYaw());
                statement.setFloat(8, location.getPitch());
                return statement.executeUpdate() > 0;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        });
    }

    public CompletableFuture<Boolean> CreateNewDungeon(String dungeon_name, String category, String boss, int level, int Playtime, int Start, Location location) {
        load();
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement("INSERT INTO dungeons (name, available, current_player, category, current_level,next_level,world, x, y, z, yaw, pitch, boss_id, play_time, start_time, point, type) VALUES (?, ?, ?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?, ?, ?, ?,?)")) {
                statement.setString(1, dungeon_name);
                statement.setBoolean(2, true);
                statement.setString(3, "Yok");
                statement.setString(4, category);
                statement.setInt(5, level);
                statement.setInt(6, level+1);
                statement.setString(7, location.getWorld().getName());
                statement.setDouble(8, location.getX());
                statement.setDouble(9, location.getY());
                statement.setDouble(10, location.getZ());
                statement.setFloat(11, location.getYaw());
                statement.setFloat(12, location.getPitch());
                statement.setString(13, boss);
                statement.setInt(14, Playtime);
                statement.setInt(15, Start);
                statement.setInt(16, 0);
                statement.setString(17, "Normal");
                return statement.executeUpdate() > 0;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        });
    }


    public CompletableFuture<Boolean> CreateNewTriggerTypeDungeon(String dungeon_name, String category, String boss, int level, int Playtime, int Start, Location location, int total_checpoints) {
        load();
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement("INSERT INTO trigger_type_dungeons (name, available, current_player, category, current_level,next_level,world, x, y, z, yaw, pitch, boss_id, play_time, start_time, point, type, checkpoint_amount,enable,boss_world, boss_x, boss_y, boss_z, boss_yaw, boss_pitch) VALUES (?, ?, ?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?, ?, ?, ?,?,?,?,?,?,?,?,?,?)")) {
                statement.setString(1, dungeon_name);
                statement.setBoolean(2, true);
                statement.setString(3, "Yok");
                statement.setString(4, category);
                statement.setInt(5, level);
                statement.setInt(6, level+1);
                statement.setString(7, location.getWorld().getName());
                statement.setDouble(8, location.getX());
                statement.setDouble(9, location.getY());
                statement.setDouble(10, location.getZ());
                statement.setFloat(11, location.getYaw());
                statement.setFloat(12, location.getPitch());
                statement.setString(13, boss);
                statement.setInt(14, Playtime);
                statement.setInt(15, Start);
                statement.setInt(16, 0);
                statement.setString(17, "Trigger");
                statement.setInt(18, total_checpoints);
                statement.setString(19, "Hayır");
                statement.setString(20, location.getWorld().getName());
                statement.setDouble(21, location.getX());
                statement.setDouble(22, location.getY());
                statement.setDouble(23, location.getZ());
                statement.setFloat(24, location.getYaw());
                statement.setFloat(25, location.getPitch());
                return statement.executeUpdate() > 0;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        });
    }


    public CompletableFuture<Boolean> CreateNewStats(Player player) {
        load();
        if(!isPlayerExists(player.getName())) {
            return CompletableFuture.supplyAsync(() -> {
                try (Connection connection = getConnection();
                     PreparedStatement statement = connection.prepareStatement("INSERT INTO stats (name, uuid, kill, death, played_match,win,lose) VALUES (?, ?, ?, ?, ?, ?, ?)")) {
                    statement.setString(1, player.getName());
                    statement.setString(2, player.getUniqueId().toString());
                    statement.setInt(3, 0);
                    statement.setInt(4, 0);
                    statement.setInt(5, 0);
                    statement.setInt(6, 0);
                    statement.setInt(7, 0);
                    return statement.executeUpdate() > 0;
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return false;
            });
        } else {
            return CompletableFuture.supplyAsync(() -> {
                return false;
            });
        }
    }

    public void deleteBosspointRecordsContainingParameter(String name)  {
        load();
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            Class.forName("org.sqlite.JDBC");
            conn = getConnection();
            String sql = "DELETE FROM bosspoints WHERE connected_dungeon LIKE ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "%"+name+"%");
            int rowsAffected = pstmt.executeUpdate();
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC sürücüsü bulunamadı: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("SQL hata: " + e.getMessage());
            try {
                throw e;
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("Kapatma hatası: " + e.getMessage());
            }
        }
    }

    public void deleteCheckpointRecordsContainingParameter(String name)  {
        load();
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            Class.forName("org.sqlite.JDBC");
            conn = getConnection();
            String sql = "DELETE FROM checkpoints WHERE connected_dungeon LIKE ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "%"+name+"%");
            int rowsAffected = pstmt.executeUpdate();
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC sürücüsü bulunamadı: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("SQL hata: " + e.getMessage());
            try {
                throw e;
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("Kapatma hatası: " + e.getMessage());
            }
        }
    }



    public CompletableFuture<Boolean> CreateTestStats(String player, String uuid) {
        load();
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement("INSERT INTO stats (name, uuid, kill, death, played_match,win,lose) VALUES (?, ?, ?, ?, ?, ?, ?)")) {
                statement.setString(1, player);
                statement.setString(2, uuid);
                statement.setInt(3, 0);
                statement.setInt(4, 0);
                statement.setInt(5, 0);
                statement.setInt(6, 0);
                statement.setInt(7, 0);
                return statement.executeUpdate() > 0;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        });
    }

    public CompletableFuture<Boolean> CreateNewBoss(String name, String id, String mgid, String uuid, String helmet, String chestplate, String leggings, String boots, String weapn,String generics, String drops, String type, String display) {
        load();
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement("INSERT INTO boss (name, id, mgid, uuid,helmet,chestplate,leggings,boots,weapon,damage, knockback,max_health, drops, type, display) VALUES (?, ?, ?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?, ?, ?)")) {
                String[] split_generics = generics.split(":");
                statement.setString(1, name);
                statement.setString(2, id);
                statement.setString(3, mgid);
                statement.setString(4, uuid);
                statement.setString(5, helmet);
                statement.setString(6, chestplate);
                statement.setString(7, leggings);
                statement.setString(8, boots);
                statement.setString(9, weapn);
                statement.setString(10, split_generics[0]); // damage
                statement.setString(11, split_generics[1]); // knockback
                statement.setString(12, split_generics[2]); // max_health
                statement.setString(13, drops);
                statement.setString(14, type);
                statement.setString(15, display);
                return statement.executeUpdate() > 0;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        });
    }


    public int getChestAmount() {
        load();
        int i = 0;
        try {
            Connection conn = getConnection();
            Statement stmt = conn.createStatement();
            String query = "SELECT name FROM dungeon_chests";
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {i++;}
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.getMessage());
        }
        return i;
    }

    public List<String> getAllDungeons() {
        load();
        List<String> dung = new ArrayList<>();
        try {
            Connection conn = getConnection();
            Statement stmt = conn.createStatement();
            String query = "SELECT name FROM dungeons";
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                String name = rs.getString("name");
                dung.add(name);
            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.getMessage());
        }
        return dung;
    }


    public Stats stats() {
        return new Stats();
    }

    public class Stats {
        public Sort sort() {
            return new Sort();
        }
        public class Sort {

            public List<Player> getSortedPlayersByKill() {
                load();
                List<Player> players = new ArrayList<>();
                try {
                    Connection conn = getConnection();
                    Statement stmt = conn.createStatement();
                    String query = "SELECT * FROM stats ORDER BY kill DESC";
                    ResultSet rs = stmt.executeQuery(query);
                    while (rs.next()) {
                        String name = rs.getString("name");  // 'name' sütun adını değiştirebilirsiniz
                        int kill = rs.getInt("kill");         // 'kill' sütun adını değiştirebilirsiniz
                        int death = rs.getInt("death");         // 'kill' sütun adını değiştirebilirsiniz
                        int played_match = rs.getInt("played_match");         // 'kill' sütun adını değiştirebilirsiniz
                        int win = rs.getInt("win");         // 'kill' sütun adını değiştirebilirsiniz
                        int lose = rs.getInt("lose");         // 'kill' sütun adını değiştirebilirsiniz
                        players.add(new Player(name, kill, win, lose, death, played_match));
                    }
                    rs.close();
                    stmt.close();
                    conn.close();
                } catch (SQLException e) {
                    System.out.println("SQL Exception: " + e.getMessage());
                }
                return players;
            }

            public class Player {
                private String name;
                private int kill,death,win,lose,match;

                public Player(String name, int kill, int win, int lose, int death, int match) {
                    this.name = name;
                    this.kill = kill;
                    this.lose = lose;
                    this.win = win;
                    this.match = match;
                    this.death = death;
                }

                public String getName() {
                    return name;
                }

                public int getKill() {
                    return kill;
                }

                public int getDeath() {
                    return death;
                }

                public int getLose() {
                    return lose;
                }

                public int getMatch() {
                    return match;
                }

                public int getWin() {
                    return win;
                }
            }
        }

        public int getKill(String dungeon) {
            load();
            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT kill FROM stats WHERE uuid = ?")) {
                statement.setString(1, dungeon);

                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    int point = resultSet.getInt("kill");
                    return point;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return 0;
        }



        public CompletableFuture<Boolean> setKill(String dungeon, int bool) {
            load();
            return CompletableFuture.supplyAsync(() -> {
                try (Connection connection = getConnection();
                     PreparedStatement statement = connection.prepareStatement("UPDATE stats SET kill = ? WHERE uuid = ?")) {
                    statement.setInt(1, getKill(dungeon)+bool);
                    statement.setString(2, dungeon);
                    return statement.executeUpdate() > 0;
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return false;
            });
        }

        public int getDeath(String dungeon) {
            load();
            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT death FROM stats WHERE uuid = ?")) {
                statement.setString(1, dungeon);

                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    int point = resultSet.getInt("death");
                    return point;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return 0;
        }



        public CompletableFuture<Boolean> setDeath(String dungeon, int bool) {
            load();
            return CompletableFuture.supplyAsync(() -> {
                try (Connection connection = getConnection();
                     PreparedStatement statement = connection.prepareStatement("UPDATE stats SET death = ? WHERE uuid = ?")) {
                    statement.setInt(1, getDeath(dungeon)+bool);
                    statement.setString(2, dungeon);
                    return statement.executeUpdate() > 0;
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return false;
            });
        }

        public int getMatch(String dungeon) {
            load();
            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT played_match FROM stats WHERE uuid = ?")) {
                statement.setString(1, dungeon);

                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    int point = resultSet.getInt("played_match");
                    return point;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return 0;
        }



        public CompletableFuture<Boolean> setMatch(String dungeon, int bool) {
            load();
            return CompletableFuture.supplyAsync(() -> {
                try (Connection connection = getConnection();
                     PreparedStatement statement = connection.prepareStatement("UPDATE stats SET played_match = ? WHERE uuid = ?")) {
                    statement.setInt(1, getMatch(dungeon)+bool);
                    statement.setString(2, dungeon);
                    return statement.executeUpdate() > 0;
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return false;
            });
        }

        public int getWin(String dungeon) {
            load();
            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT win FROM stats WHERE uuid = ?")) {
                statement.setString(1, dungeon);

                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    int point = resultSet.getInt("win");
                    return point;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return 0;
        }



        public CompletableFuture<Boolean> setWin(String dungeon, int bool) {
            load();
            return CompletableFuture.supplyAsync(() -> {
                try (Connection connection = getConnection();
                     PreparedStatement statement = connection.prepareStatement("UPDATE stats SET win = ? WHERE uuid = ?")) {
                    statement.setInt(1, getWin(dungeon)+bool);
                    statement.setString(2, dungeon);
                    return statement.executeUpdate() > 0;
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return false;
            });
        }


        public int getLose(String dungeon) {
            load();
            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT lose FROM stats WHERE uuid = ?")) {
                statement.setString(1, dungeon);

                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    int point = resultSet.getInt("lose");
                    return point;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return 0;
        }



        public CompletableFuture<Boolean> setLose(String dungeon, int bool) {
            load();
            return CompletableFuture.supplyAsync(() -> {
                try (Connection connection = getConnection();
                     PreparedStatement statement = connection.prepareStatement("UPDATE stats SET lose = ? WHERE uuid = ?")) {
                    statement.setInt(1, getLose(dungeon)+bool);
                    statement.setString(2, dungeon);
                    return statement.executeUpdate() > 0;
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return false;
            });
        }

        public String getName(String dungeon) {
            load();
            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT name FROM stats WHERE uuid = ?")) {
                statement.setString(1, dungeon);

                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    String point = resultSet.getString("name");
                    return point;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return "Bilinmiyor";
        }



        public CompletableFuture<Boolean> setName(String dungeon, String bool) {
            load();
            return CompletableFuture.supplyAsync(() -> {
                try (Connection connection = getConnection();
                     PreparedStatement statement = connection.prepareStatement("UPDATE stats SET name = ? WHERE uuid = ?")) {
                    statement.setString(1, bool);
                    statement.setString(2, dungeon);
                    return statement.executeUpdate() > 0;
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return false;
            });
        }
    }

    public Boss boss() {
        return new Boss();
    }


    public class Boss {

        public List<String> getAllBoss() {
            load();
            List<String> dung = new ArrayList<>();
            try {
                Connection conn = getConnection();
                Statement stmt = conn.createStatement();
                String query = "SELECT name FROM boss";
                ResultSet rs = stmt.executeQuery(query);
                while (rs.next()) {
                    String name = rs.getString("name");
                    dung.add(name);
                }
                rs.close();
                stmt.close();
                conn.close();
            } catch (SQLException e) {
                System.out.println("SQL Exception: " + e.getMessage());
            }
            return dung;
        }

        public CompletableFuture<Boolean> delete(String dungeon) {
            load();
            return CompletableFuture.supplyAsync(() -> {
                try (Connection connection = getConnection();
                     PreparedStatement statement = connection.prepareStatement("DELETE FROM boss WHERE name = ?")) {
                    statement.setString(1, dungeon);
                    return statement.executeUpdate() > 0;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            });
        }

        public String getName(String dungeon) {
            load();
            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT name FROM boss WHERE name = ?")) {
                statement.setString(1, dungeon);

                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    String point = resultSet.getString("name");
                    return point;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return "Yok";
        }

        public String getUUID(String dungeon) {
            load();
            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT uuid FROM boss WHERE name = ?")) {
                statement.setString(1, dungeon);

                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    String point = resultSet.getString("uuid");
                    return point;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return "Yok";
        }

        public String getMGID(String dungeon) {
            load();
            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT mgid FROM boss WHERE name = ?")) {
                statement.setString(1, dungeon);

                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    String point = resultSet.getString("mgid");
                    return point;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return "Yok";
        }

        public String getType(String dungeon) {
            load();
            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT type FROM boss WHERE name = ?")) {
                statement.setString(1, dungeon);

                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    String point = resultSet.getString("type");
                    return point;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return "Yok";
        }

        public String getDisplay(String dungeon) {
            load();
            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT display FROM boss WHERE name = ?")) {
                statement.setString(1, dungeon);

                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    String point = resultSet.getString("display");
                    return point;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return "Yok";
        }

        public String getHelmetItem(String dungeon) {
            load();
            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT helmet FROM boss WHERE name = ?")) {
                statement.setString(1, dungeon);

                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    String point = resultSet.getString("helmet");
                    String[] spl = point.split("/");
                    return spl[0];
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return "Yok";
        }

        public List<String> getHelmetEnchants(String dungeon) {
            load();
            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT helmet FROM boss WHERE name = ?")) {
                statement.setString(1, dungeon);

                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    String point = resultSet.getString("helmet");
                    String[] spl = point.split("/");
                    String[] spl1 = spl[1].split(",");
                    List<String> list = new ArrayList<>();
                    for(int i = 0;i<spl1.length;i++) {
                        list.add(spl1[i]);

                    }
                    return list;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }

        public List<String> getChestplateEnchants(String dungeon) {
            load();
            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT chestplate FROM boss WHERE name = ?")) {
                statement.setString(1, dungeon);

                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    String point = resultSet.getString("chestplate");
                    String[] spl = point.split("/");
                    String[] spl1 = spl[1].split(",");
                    List<String> list = new ArrayList<>();
                    for(int i = 0;i<spl1.length;i++) {
                        list.add(spl1[i]);

                    }
                    return list;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }

        public List<String> getLeggingsEnchant(String dungeon) {
            load();
            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT leggings FROM boss WHERE name = ?")) {
                statement.setString(1, dungeon);

                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    String point = resultSet.getString("leggings");
                    String[] spl = point.split("/");
                    String[] spl1 = spl[1].split(",");
                    List<String> list = new ArrayList<>();
                    for(int i = 0;i<spl1.length;i++) {
                        list.add(spl1[i]);

                    }
                    return list;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }

        public List<String> getBootsEnchant(String dungeon) {
            load();
            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT boots FROM boss WHERE name = ?")) {
                statement.setString(1, dungeon);

                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    String point = resultSet.getString("boots");
                    String[] spl = point.split("/");
                    String[] spl1 = spl[1].split(",");
                    List<String> list = new ArrayList<>();
                    for(int i = 0;i<spl1.length;i++) {
                        list.add(spl1[i]);

                    }
                    return list;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }

        public String getChestplateItem(String dungeon) {
            load();
            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT chestplate FROM boss WHERE name = ?")) {
                statement.setString(1, dungeon);

                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    String point = resultSet.getString("chestplate");
                    String[] spl = point.split("/");
                    return spl[0];
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return "Yok";
        }

        public List<String> getWeaponEnchant(String dungeon) {
            load();
            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT weapon FROM boss WHERE name = ?")) {
                statement.setString(1, dungeon);

                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    String point = resultSet.getString("weapon");
                    String[] spl = point.split("/");
                    String[] spl1 = spl[1].split(",");
                    List<String> list = new ArrayList<>();
                    for(int i = 0;i<spl1.length;i++) {
                        list.add(spl1[i]);

                    }
                    return list;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }

        public List<String> getDrops(String dungeon) {
            load();
            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT drops FROM boss WHERE name = ?")) {
                statement.setString(1, dungeon);

                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    String point = resultSet.getString("drops");
                    String[] spl = point.split("/");
                    List<String> list = new ArrayList<>();
                    for(int i = 0;i<spl.length;i++) {
                        list.add(spl[i]);

                    }
                    return list;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }
        public String getLeggingsItem(String dungeon) {
            load();
            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT leggings FROM boss WHERE name = ?")) {
                statement.setString(1, dungeon);

                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    String point = resultSet.getString("leggings");
                    String[] spl = point.split("/");
                    return spl[0];
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return "Yok";
        }

        public String getBootsItem(String dungeon) {
            load();
            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT boots FROM boss WHERE name = ?")) {
                statement.setString(1, dungeon);

                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    String point = resultSet.getString("boots");
                    String[] spl = point.split("/");
                    return spl[0];
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return "Yok";
        }

        public String getWeaponsItem(String dungeon) {
            load();
            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT weapon FROM boss WHERE name = ?")) {
                statement.setString(1, dungeon);

                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    String point = resultSet.getString("weapon");
                    String[] spl = point.split("/");
                    return spl[0];
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return "Yok";
        }

        public double getHealth(String dungeon) {
            load();
            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT max_health FROM boss WHERE name = ?")) {
                statement.setString(1, dungeon);

                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    double point = resultSet.getDouble("max_health");
                    return point;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return 0;
        }

        public double getKnockback(String dungeon) {
            load();
            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT knockback FROM boss WHERE name = ?")) {
                statement.setString(1, dungeon);

                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    double point = resultSet.getDouble("knockback");
                    return point;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return 0;
        }

        public double getAttack(String dungeon) {
            load();
            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT damage FROM boss WHERE name = ?")) {
                statement.setString(1, dungeon);

                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    double point = resultSet.getDouble("damage");
                    return point;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return 0;
        }



        public CompletableFuture<Boolean> setMGID(String dungeon, String bool) {
            load();
            return CompletableFuture.supplyAsync(() -> {
                try (Connection connection = getConnection();
                     PreparedStatement statement = connection.prepareStatement("UPDATE boss SET mgid = ? WHERE name = ?")) {
                    statement.setString(1, bool);
                    statement.setString(2, dungeon);
                    return statement.executeUpdate() > 0;
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return false;
            });
        }


        public CompletableFuture<Boolean> setType(String dungeon, String bool) {
            load();
            return CompletableFuture.supplyAsync(() -> {
                try (Connection connection = getConnection();
                     PreparedStatement statement = connection.prepareStatement("UPDATE boss SET type = ? WHERE name = ?")) {
                    statement.setString(1, bool);
                    statement.setString(2, dungeon);
                    return statement.executeUpdate() > 0;
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return false;
            });
        }




        public CompletableFuture<Boolean> setUUID(String dungeon, String bool) {
            load();
            return CompletableFuture.supplyAsync(() -> {
                try (Connection connection = getConnection();
                     PreparedStatement statement = connection.prepareStatement("UPDATE boss SET uuid = ? WHERE name = ?")) {
                    statement.setString(1, bool);
                    statement.setString(2, dungeon);
                    return statement.executeUpdate() > 0;
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return false;
            });
        }

    }

    public boolean isPlayerExists(String dungeon) {
        load();
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT 1 FROM stats WHERE name = ?")) {
            statement.setString(1, dungeon);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isDungeonExists(String dungeon) {
        load();
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT 1 FROM dungeons WHERE name = ?")) {
            statement.setString(1, dungeon);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public CompletableFuture<Boolean> deleteDungeon(String dungeon) {
        load();
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement("DELETE FROM dungeons WHERE name = ?")) {
                statement.setString(1, dungeon);
                return statement.executeUpdate() > 0;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        });
    }




    public CompletableFuture<Boolean> isDungeonAvailable(String dungeon) {
        load();
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT 1 FROM dungeons WHERE name = ?")) {
                statement.setString(1, dungeon);
                
                ResultSet resultSet = statement.executeQuery();
                return resultSet.next();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        });
    }


    public Boolean isBossAvailable(String dungeon) {
        load();
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT 1 FROM boss WHERE name = ?")) {
            statement.setString(1, dungeon);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    public Dungeon getDungeon(String dungeon) {
        load();
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM dungeons WHERE name = ?")) {
            statement.setString(1, dungeon);
            
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return new Dungeon(dungeon);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    };


    public int getPoint(String dungeon) {
        load();
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT point FROM dungeons WHERE name = ?")) {
            statement.setString(1, dungeon);
            
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int point = resultSet.getInt("point");
                return point;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }


    public String getType(String dungeon) {
        load();
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT type FROM dungeons WHERE name = ?")) {
            statement.setString(1, dungeon);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String point = resultSet.getString("type");
                return point;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Unknow";
    }




    public String getCurrentPlayer(String dungeon) {
        load();
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT current_player FROM dungeons WHERE name = ?")) {
            statement.setString(1, dungeon);
            
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String point = resultSet.getString("current_player");
                return point;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Yok";
    }

    public boolean getStatus(String dungeon) {
        load();
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT available FROM dungeons WHERE name = ?")) {
            statement.setString(1, dungeon);
            
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                boolean point = resultSet.getBoolean("available");
                return point;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getName(String dungeon) {
        load();
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT name FROM dungeons WHERE name = ?")) {
            statement.setString(1, dungeon);
            
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String point = resultSet.getString("name");
                return point;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Yok";
    }


    public String getCategory(String dungeon) {
        load();
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT category FROM dungeons WHERE name = ?")) {
            statement.setString(1, dungeon);
            
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String point = resultSet.getString("category");
                return point;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Yok";
    }



    public String getBoss(String dungeon) {
        load();
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT boss_id FROM dungeons WHERE name = ?")) {
            statement.setString(1, dungeon);
            
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String point = resultSet.getString("boss_id");
                return point;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Yok";
    }




    public int getLevel(String dungeon) {
        load();
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT current_level FROM dungeons WHERE name = ?")) {
            statement.setString(1, dungeon);
            
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int point = resultSet.getInt("current_level");
                return point;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }


    public int getNextLevel(String dungeon) {
        load();
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT next_level FROM dungeons WHERE name = ?")) {
            statement.setString(1, dungeon);
            
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int point = resultSet.getInt("next_level");
                return point;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getPlayTime(String dungeon) {
        load();
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT play_time FROM dungeons WHERE name = ?")) {
            statement.setString(1, dungeon);
            
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int point = resultSet.getInt("play_time");
                return point;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getStartTime(String dungeon) {
        load();
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT start_time FROM dungeons WHERE name = ?")) {
            statement.setString(1, dungeon);
            
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int point = resultSet.getInt("start_time");
                return point;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public Location getChestLocation(String dungeon) {
        load();
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM dungeon_chests WHERE name = ?")) {
            statement.setString(1, dungeon);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String world = resultSet.getString("world");
                double x = resultSet.getDouble("x");
                double y = resultSet.getDouble("y");
                double z = resultSet.getDouble("z");
                float yaw = resultSet.getFloat("yaw");
                float pitch = resultSet.getFloat("pitch");
                return new Location(Bukkit.getWorld(world) == null ? null : Bukkit.getWorld(world), x, y, z, yaw, pitch);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public Location getLocation(String dungeon) {
        load();
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM dungeons WHERE name = ?")) {
            statement.setString(1, dungeon);
            
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String world = resultSet.getString("world");
                double x = resultSet.getDouble("x");
                double y = resultSet.getDouble("y");
                double z = resultSet.getDouble("z");
                float yaw = resultSet.getFloat("yaw");
                float pitch = resultSet.getFloat("pitch");
                return new Location(Bukkit.getWorld(world) == null ? null : Bukkit.getWorld(world), x, y, z, yaw, pitch);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public CompletableFuture<Boolean> setStatus(String dungeon, boolean bool) {
        load();
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement("UPDATE dungeons SET available = ? WHERE name = ?")) {
                statement.setBoolean(1, bool);
                statement.setString(2, dungeon);
                return statement.executeUpdate() > 0;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        });
    }



    public CompletableFuture<Boolean> setCurrentPlayer(String dungeon, String bool) {
        load();
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement("UPDATE dungeons SET current_player = ? WHERE name = ?")) {
                statement.setString(1, bool);
                statement.setString(2, dungeon);
                return statement.executeUpdate() > 0;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        });
    }

    public CompletableFuture<Boolean> setPoint(String dungeon, int bool) {
        load();
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement("UPDATE dungeons SET point = ? WHERE name = ?")) {
                statement.setInt(1, bool);
                statement.setString(2, dungeon);
                return statement.executeUpdate() > 0;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        });
    }

    public CompletableFuture<Boolean> setNextLevel(String dungeon, int bool) {
        load();
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement("UPDATE dungeons SET next_level = ? WHERE name = ?")) {
                statement.setInt(1, bool);
                statement.setString(2, dungeon);
                return statement.executeUpdate() > 0;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        });
    }

    public CompletableFuture<Boolean> setName(String dungeon, String bool) {
        load();
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement("UPDATE dungeons SET name = ? WHERE name = ?")) {
                statement.setString(1, bool);
                statement.setString(2, dungeon);
                return statement.executeUpdate() > 0;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        });
    }

    public CompletableFuture<Boolean> setCategory(String dungeon, String bool) {
        load();
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement("UPDATE dungeons SET category = ? WHERE name = ?")) {
                statement.setString(1, bool);
                statement.setString(2, dungeon);
                return statement.executeUpdate() > 0;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        });
    }

    public CompletableFuture<Boolean> setBossID(String dungeon, String bool) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement("UPDATE dungeons SET boss_id = ? WHERE name = ?")) {
                statement.setString(1, bool);
                statement.setString(2, dungeon);
                return statement.executeUpdate() > 0;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        });
    }

    public CompletableFuture<Boolean> setLevel(String dungeon, int bool) {
        load();
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement("UPDATE dungeons SET level = ? WHERE name = ?")) {
                statement.setInt(1, bool);
                statement.setString(2, dungeon);
                return statement.executeUpdate() > 0;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        });
    }

    public CompletableFuture<Boolean> setPlaytime(String dungeon, int bool) {
        load();
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement("UPDATE dungeons SET play_time = ? WHERE name = ?")) {
                statement.setInt(1, bool);
                statement.setString(2, dungeon);
                return statement.executeUpdate() > 0;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        });
    }

    public CompletableFuture<Boolean> setStarttime(String dungeon, int bool) {
        load();
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement("UPDATE dungeons SET start_time = ? WHERE name = ?")) {
                statement.setInt(1, bool);
                statement.setString(2, dungeon);
                return statement.executeUpdate() > 0;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        });
    }

    public CompletableFuture<Boolean> setSpawn(String dungeon, Location location) {
        load();
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement("UPDATE dungeons SET world = ?, x = ?, y= ?, z = ?, yaw = ?, pitch = ? WHERE name = ?")) {
                statement.setString(1, location.getWorld().getName());
                statement.setDouble(2, location.getX());
                statement.setDouble(3, location.getY());
                statement.setDouble(4, location.getZ());
                statement.setFloat(5, location.getYaw());
                statement.setFloat(6, location.getPitch());
                statement.setString(7, dungeon);
                return statement.executeUpdate() > 0;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        });
    }

    public TriggerTypeDungeons TriggerTypeDungeons() {
        return new TriggerTypeDungeons();
    }

    public class TriggerTypeDungeons {

        public boolean isDungeonExists(String dungeon) {
            load();
            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT 1 FROM trigger_type_dungeons WHERE name = ?")) {
                statement.setString(1, dungeon);
                ResultSet resultSet = statement.executeQuery();
                return resultSet.next();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        }

        public CompletableFuture<Boolean> deleteDungeon(String dungeon) {
            load();
            return CompletableFuture.supplyAsync(() -> {
                try (Connection connection = getConnection();
                     PreparedStatement statement = connection.prepareStatement("DELETE FROM trigger_type_dungeons WHERE name = ?")) {
                    statement.setString(1, dungeon);
                    return statement.executeUpdate() > 0;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            });
        }




        public boolean isDungeonAvailable(String dungeon) {
            load();
            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT 1 FROM trigger_type_dungeons WHERE name = ?")) {
                statement.setString(1, dungeon);

                ResultSet resultSet = statement.executeQuery();
                return resultSet.next();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        }

        public int getPoint(String dungeon) {
            load();
            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT point FROM trigger_type_dungeons WHERE name = ?")) {
                statement.setString(1, dungeon);

                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    int point = resultSet.getInt("point");
                    return point;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return 0;
        }


        public String getType(String dungeon) {
            load();
            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT type FROM trigger_type_dungeons WHERE name = ?")) {
                statement.setString(1, dungeon);

                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    String point = resultSet.getString("type");
                    return point;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return "Normal";
        }

        public boolean getAvailable(String dungeon) {
            load();
            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT available FROM trigger_type_dungeons WHERE name = ?")) {
                statement.setString(1, dungeon);

                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    boolean point = resultSet.getBoolean("available");
                    return point;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        }




        public String getCurrentPlayer(String dungeon) {
            load();
            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT current_player FROM trigger_type_dungeons WHERE name = ?")) {
                statement.setString(1, dungeon);

                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    String point = resultSet.getString("current_player");
                    return point;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return "Yok";
        }

        public boolean getStatus(String dungeon) {
            load();
            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT available FROM trigger_type_dungeons WHERE name = ?")) {
                statement.setString(1, dungeon);

                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    boolean point = resultSet.getBoolean("available");
                    return point;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        }

        public String getName(String dungeon) {
            load();
            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT name FROM trigger_type_dungeons WHERE name = ?")) {
                statement.setString(1, dungeon);

                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    String point = resultSet.getString("name");
                    return point;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return "Yok";
        }


        public String getCategory(String dungeon) {
            load();
            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT category FROM trigger_type_dungeons WHERE name = ?")) {
                statement.setString(1, dungeon);

                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    String point = resultSet.getString("category");
                    return point;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return "Yok";
        }



        public String getBoss(String dungeon) {
            load();
            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT boss_id FROM trigger_type_dungeons WHERE name = ?")) {
                statement.setString(1, dungeon);

                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    String point = resultSet.getString("boss_id");
                    return point;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return "Yok";
        }



        public String getEnable(String dungeon) {
            load();
            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT enable FROM trigger_type_dungeons WHERE name = ?")) {
                statement.setString(1, dungeon);

                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    String point = resultSet.getString("enable");
                    return point;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return "Yok";
        }

        public boolean isEnable(String dungeon) {
            return !getEnable(dungeon).equalsIgnoreCase("Hayır");
        }

        public int getLevel(String dungeon) {
            load();
            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT current_level FROM trigger_type_dungeons WHERE name = ?")) {
                statement.setString(1, dungeon);

                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    int point = resultSet.getInt("current_level");
                    return point;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return 0;
        }

        public int getTotalCheckpoints(String dungeon) {
            load();
            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT checkpoint_amount FROM trigger_type_dungeons WHERE name = ?")) {
                statement.setString(1, dungeon);

                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    int point = resultSet.getInt("checkpoint_amount");
                    return point;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return 0;
        }


        public int getNextLevel(String dungeon) {
            load();
            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT next_level FROM trigger_type_dungeons WHERE name = ?")) {
                statement.setString(1, dungeon);

                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    int point = resultSet.getInt("next_level");
                    return point;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return 0;
        }


        public List<String> getAllDungeons() {
            load();
            List<String> dung = new ArrayList<>();
            try {
                Connection conn = getConnection();
                Statement stmt = conn.createStatement();
                String query = "SELECT name FROM trigger_type_dungeons";
                ResultSet rs = stmt.executeQuery(query);
                while (rs.next()) {
                    String name = rs.getString("name");
                    dung.add(name);
                }
                rs.close();
                stmt.close();
                conn.close();
            } catch (SQLException e) {
                System.out.println("SQL Exception: " + e.getMessage());
            }
            return dung;
        }

        public int getPlayTime(String dungeon) {
            load();
            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT play_time FROM trigger_type_dungeons WHERE name = ?")) {
                statement.setString(1, dungeon);

                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    int point = resultSet.getInt("play_time");
                    return point;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return 0;
        }

        public int getStartTime(String dungeon) {
            load();
            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT start_time FROM trigger_type_dungeons WHERE name = ?")) {
                statement.setString(1, dungeon);

                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    int point = resultSet.getInt("start_time");
                    return point;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return 0;
        }


        public Location getLocation(String dungeon) {
            load();
            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT * FROM trigger_type_dungeons WHERE name = ?")) {
                statement.setString(1, dungeon);

                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    String world = resultSet.getString("world");
                    double x = resultSet.getDouble("x");
                    double y = resultSet.getDouble("y");
                    double z = resultSet.getDouble("z");
                    float yaw = resultSet.getFloat("yaw");
                    float pitch = resultSet.getFloat("pitch");
                    return new Location(Bukkit.getWorld(world) == null ? null : Bukkit.getWorld(world), x, y, z, yaw, pitch);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }

        public Location getBossLocation(String dungeon) {
            load();
            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT * FROM trigger_type_dungeons WHERE name = ?")) {
                statement.setString(1, dungeon);

                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    String world = resultSet.getString("boss_world");
                    double x = resultSet.getDouble("boss_x");
                    double y = resultSet.getDouble("boss_y");
                    double z = resultSet.getDouble("boss_z");
                    float yaw = resultSet.getFloat("boss_yaw");
                    float pitch = resultSet.getFloat("boss_pitch");
                    return new Location(Bukkit.getWorld(world) == null ? null : Bukkit.getWorld(world), x, y, z, yaw, pitch);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }

        public Location getCheckpointLocation(String dungeon, int level) {
            load();
            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT * FROM checkpoints WHERE connected_dungeon = ? AND checkpoint_queue = ?")) {
                statement.setString(1, dungeon);
                statement.setInt(2, level);

                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    String world = resultSet.getString("world");
                    double x = resultSet.getDouble("x");
                    double y = resultSet.getDouble("y");
                    double z = resultSet.getDouble("z");
                    float yaw = resultSet.getFloat("yaw");
                    float pitch = resultSet.getFloat("pitch");
                    return new Location(Bukkit.getWorld(world) == null ? null : Bukkit.getWorld(world), x, y, z, yaw, pitch);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }

        public String getCheckpointBoss(String dungeon, int level) {
            load();
            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT * FROM checkpoints WHERE connected_dungeon = ? AND checkpoint_queue = ?")) {
                statement.setString(1, dungeon);
                statement.setInt(2, level);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    String world = resultSet.getString("boss");
                    return world;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }

        public Location getBosspointsLocation(String dungeon, int level) {
            load();
            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT * FROM bosspoints WHERE connected_dungeon = ? AND checkpoint_queue = ?")) {
                statement.setString(1, dungeon);
                statement.setInt(2, level);

                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    String world = resultSet.getString("world");
                    double x = resultSet.getDouble("x");
                    double y = resultSet.getDouble("y");
                    double z = resultSet.getDouble("z");
                    float yaw = resultSet.getFloat("yaw");
                    float pitch = resultSet.getFloat("pitch");
                    return new Location(Bukkit.getWorld(world) == null ? null : Bukkit.getWorld(world), x, y, z, yaw, pitch);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }

        public CompletableFuture<Boolean> setStatus(String dungeon, boolean bool) {
            load();
            return CompletableFuture.supplyAsync(() -> {
                try (Connection connection = getConnection();
                     PreparedStatement statement = connection.prepareStatement("UPDATE trigger_type_dungeons SET available = ? WHERE name = ?")) {
                    statement.setBoolean(1, bool);
                    statement.setString(2, dungeon);
                    return statement.executeUpdate() > 0;
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return false;
            });
        }



        public CompletableFuture<Boolean> setCurrentPlayer(String dungeon, String bool) {
            load();
            return CompletableFuture.supplyAsync(() -> {
                try (Connection connection = getConnection();
                     PreparedStatement statement = connection.prepareStatement("UPDATE trigger_type_dungeons SET current_player = ? WHERE name = ?")) {
                    statement.setString(1, bool);
                    statement.setString(2, dungeon);
                    return statement.executeUpdate() > 0;
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return false;
            });
        }

        public CompletableFuture<Boolean> setPoint(String dungeon, int bool) {
            load();
            return CompletableFuture.supplyAsync(() -> {
                try (Connection connection = getConnection();
                     PreparedStatement statement = connection.prepareStatement("UPDATE trigger_type_dungeons SET point = ? WHERE name = ?")) {
                    statement.setInt(1, bool);
                    statement.setString(2, dungeon);
                    return statement.executeUpdate() > 0;
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return false;
            });
        }

        public CompletableFuture<Boolean> setAvailable(String dungeon, boolean bool) {
            load();
            return CompletableFuture.supplyAsync(() -> {
                try (Connection connection = getConnection();
                     PreparedStatement statement = connection.prepareStatement("UPDATE trigger_type_dungeons SET available = ? WHERE name = ?")) {
                    statement.setBoolean(1, bool);
                    statement.setString(2, dungeon);
                    return statement.executeUpdate() > 0;
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return false;
            });
        }

        public CompletableFuture<Boolean> setTotalCheckpoints(String dungeon, int bool) {
            load();
            return CompletableFuture.supplyAsync(() -> {
                try (Connection connection = getConnection();
                     PreparedStatement statement = connection.prepareStatement("UPDATE trigger_type_dungeons SET checkpoint_amount = ? WHERE name = ?")) {
                    statement.setInt(1, bool);
                    statement.setString(2, dungeon);
                    return statement.executeUpdate() > 0;
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return false;
            });
        }

        public CompletableFuture<Boolean> setNextLevel(String dungeon, int bool) {
            load();
            return CompletableFuture.supplyAsync(() -> {
                try (Connection connection = getConnection();
                     PreparedStatement statement = connection.prepareStatement("UPDATE trigger_type_dungeons SET next_level = ? WHERE name = ?")) {
                    statement.setInt(1, bool);
                    statement.setString(2, dungeon);
                    return statement.executeUpdate() > 0;
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return false;
            });
        }

        public CompletableFuture<Boolean> setName(String dungeon, String bool) {
            load();
            return CompletableFuture.supplyAsync(() -> {
                try (Connection connection = getConnection();
                     PreparedStatement statement = connection.prepareStatement("UPDATE trigger_type_dungeons SET name = ? WHERE name = ?")) {
                    statement.setString(1, bool);
                    statement.setString(2, dungeon);
                    return statement.executeUpdate() > 0;
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return false;
            });
        }

        public CompletableFuture<Boolean> setEnable(String dungeon, String bool) {
            load();
            return CompletableFuture.supplyAsync(() -> {
                try (Connection connection = getConnection();
                     PreparedStatement statement = connection.prepareStatement("UPDATE trigger_type_dungeons SET enable = ? WHERE name = ?")) {
                    statement.setString(1, bool);
                    statement.setString(2, dungeon);
                    return statement.executeUpdate() > 0;
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return false;
            });
        }

        public CompletableFuture<Boolean> setCategory(String dungeon, String bool) {
            load();
            return CompletableFuture.supplyAsync(() -> {
                try (Connection connection = getConnection();
                     PreparedStatement statement = connection.prepareStatement("UPDATE trigger_type_dungeons SET category = ? WHERE name = ?")) {
                    statement.setString(1, bool);
                    statement.setString(2, dungeon);
                    return statement.executeUpdate() > 0;
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return false;
            });
        }

        public CompletableFuture<Boolean> setBossID(String dungeon, String bool) {
            return CompletableFuture.supplyAsync(() -> {
                try (Connection connection = getConnection();
                     PreparedStatement statement = connection.prepareStatement("UPDATE trigger_type_dungeons SET boss_id = ? WHERE name = ?")) {
                    statement.setString(1, bool);
                    statement.setString(2, dungeon);
                    return statement.executeUpdate() > 0;
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return false;
            });
        }

        public CompletableFuture<Boolean> setLevel(String dungeon, int bool) {
            load();
            return CompletableFuture.supplyAsync(() -> {
                try (Connection connection = getConnection();
                     PreparedStatement statement = connection.prepareStatement("UPDATE trigger_type_dungeons SET level = ? WHERE name = ?")) {
                    statement.setInt(1, bool);
                    statement.setString(2, dungeon);
                    return statement.executeUpdate() > 0;
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return false;
            });
        }

        public CompletableFuture<Boolean> setPlaytime(String dungeon, int bool) {
            load();
            return CompletableFuture.supplyAsync(() -> {
                try (Connection connection = getConnection();
                     PreparedStatement statement = connection.prepareStatement("UPDATE trigger_type_dungeons SET play_time = ? WHERE name = ?")) {
                    statement.setInt(1, bool);
                    statement.setString(2, dungeon);
                    return statement.executeUpdate() > 0;
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return false;
            });
        }

        public CompletableFuture<Boolean> setStarttime(String dungeon, int bool) {
            load();
            return CompletableFuture.supplyAsync(() -> {
                try (Connection connection = getConnection();
                     PreparedStatement statement = connection.prepareStatement("UPDATE trigger_type_dungeons SET start_time = ? WHERE name = ?")) {
                    statement.setInt(1, bool);
                    statement.setString(2, dungeon);
                    return statement.executeUpdate() > 0;
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return false;
            });
        }

        public CompletableFuture<Boolean> setSpawn(String dungeon, Location location) {
            load();
            return CompletableFuture.supplyAsync(() -> {
                try (Connection connection = getConnection();
                     PreparedStatement statement = connection.prepareStatement("UPDATE trigger_type_dungeons SET world = ?, x = ?, y= ?, z = ?, yaw = ?, pitch = ? WHERE name = ?")) {
                    statement.setString(1, location.getWorld().getName());
                    statement.setDouble(2, location.getX());
                    statement.setDouble(3, location.getY());
                    statement.setDouble(4, location.getZ());
                    statement.setFloat(5, location.getYaw());
                    statement.setFloat(6, location.getPitch());
                    statement.setString(7, dungeon);
                    return statement.executeUpdate() > 0;
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return false;
            });
        }

        public CompletableFuture<Boolean> setBossSpawn(String dungeon, Location location) {
            load();
            return CompletableFuture.supplyAsync(() -> {
                try (Connection connection = getConnection();
                     PreparedStatement statement = connection.prepareStatement("UPDATE trigger_type_dungeons SET boss_world = ?, boss_x = ?, boss_y= ?, boss_z = ?, boss_yaw = ?, boss_pitch = ? WHERE name = ?")) {
                    statement.setString(1, location.getWorld().getName());
                    statement.setDouble(2, location.getX());
                    statement.setDouble(3, location.getY());
                    statement.setDouble(4, location.getZ());
                    statement.setFloat(5, location.getYaw());
                    statement.setFloat(6, location.getPitch());
                    statement.setString(7, dungeon);
                    return statement.executeUpdate() > 0;
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return false;
            });
        }
    }

}