package com.magnesify.magnesifydungeons.modules;

import com.magnesify.magnesifydungeons.MagnesifyDungeons;
import com.magnesify.magnesifydungeons.dungeon.Dungeon;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.Bukkit;
import org.bukkit.Location;

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
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS dungeons (name TEXT, available BOOLEAN, current_player TEXT, category TEXT, current_level INTEGER DEFAULT 0,world TEXT, x DOUBLE, y DOUBLE, z DOUBLE, yaw FLOAT, pitch FLOAT, boss_id TEXT, play_time INTEGER DEFAULT 3, start_time INTEGER DEFAULT 5, point INTEGER DEFAULT 0, next_level INTEGER DEFAULT 0, PRIMARY KEY(name, category))");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS boss (name TEXT, id TEXT, mgid TEXT, uuid TEXT,helmet TEXT,chestplate TEXT,leggings TEXT,boots TEXT,weapon TEXT,damage DOUBLE DEFAULT 0.0, knockback DOUBLE DEFAULT 0.0,max_health DOUBLE DEFAULT 0.0, drops TEXT DEFAULT 'GOLDEN_APPLE', type TEXT DEFAULT 'ZOMBIE', display TEXT DEFAULT '&cMagnesify', PRIMARY KEY(name, mgid))");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("DataSource is not initialized");
        }
        return dataSource.getConnection();
    }

    public CompletableFuture<Boolean> CreateNewDungeon(String dungeon_name, String category, String boss, int level, int Playtime, int Start, Location location) {
        load();
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement("INSERT INTO dungeons (name, available, current_player, category, current_level,next_level,world, x, y, z, yaw, pitch, boss_id, play_time, start_time, point) VALUES (?, ?, ?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?, ?, ?, ?)")) {
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

    public Boss boss() {
        return new Boss();
    }


    public class Boss {

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
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM dungeons WHERE player = ?")) {
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
                 PreparedStatement statement = connection.prepareStatement("UPDATE dungeons SET current_player = ? WHERE player = ?")) {
                statement.setString(1, bool);
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
                 PreparedStatement statement = connection.prepareStatement("UPDATE dungeons SET name = ? WHERE player = ?")) {
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
                 PreparedStatement statement = connection.prepareStatement("UPDATE dungeons SET category = ? WHERE player = ?")) {
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
                 PreparedStatement statement = connection.prepareStatement("UPDATE dungeons SET boss_id = ? WHERE player = ?")) {
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
                 PreparedStatement statement = connection.prepareStatement("UPDATE dungeons SET level = ? WHERE player = ?")) {
                statement.setInt(1, bool);
                statement.setString(2, dungeon);
                return statement.executeUpdate() > 0;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        });
    }

}