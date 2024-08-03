package com.magnesify.magnesifydungeons.modules;

import com.magnesify.magnesifydungeons.MagnesifyDungeons;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private final MagnesifyDungeons plugin;
    private Connection connection;
    private HikariDataSource dataSource;

    public DatabaseManager(MagnesifyDungeons plugin) {
        this.plugin = plugin;
    }

    public void initialize() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:sqlite:" + plugin.getDataFolder() + "/datas/magnesify.db");
        config.setMaximumPoolSize(10);
        config.setIdleTimeout(30000);
        config.setConnectionTimeout(30000);
        config.setMaxLifetime(1800000);

        dataSource = new HikariDataSource(config);

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS dungeons (name TEXT, available BOOLEAN, current_player TEXT, category TEXT, current_level INTEGER DEFAULT 0,world TEXT, x DOUBLE, y DOUBLE, z DOUBLE, yaw FLOAT, pitch FLOAT, boss_id TEXT, play_time INTEGER DEFAULT 3, start_time INTEGER DEFAULT 5, point INTEGER DEFAULT 0, next_level INTEGER DEFAULT 0, PRIMARY KEY(name, category))");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS boss (name TEXT, id TEXT, mgid TEXT, uuid TEXT,helmet TEXT,chestplate TEXT,leggings TEXT,boots TEXT,weapon TEXT,damage DOUBLE DEFAULT 0.0, knockback DOUBLE DEFAULT 0.0,max_health DOUBLE DEFAULT 0.0, drops TEXT DEFAULT 'GOLDEN_APPLE', type TEXT DEFAULT 'ZOMBIE', display TEXT DEFAULT '&cMagnesify', PRIMARY KEY(name, mgid))");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS players (name TEXT, point TEXT,kill INTEGER DEFAULT 0, death INTEGER DEFAULT 0, last_dungeon TEXT, last_boss TEXT, dungeons TEXT, in_dungeon BOOLEAN, done BOOLEAN, PRIMARY KEY(name))");
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
}