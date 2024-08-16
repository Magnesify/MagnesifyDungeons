package com.magnesify.magnesifydungeons.modules.managers;

import com.magnesify.magnesifydungeons.MagnesifyDungeons;
import org.bukkit.entity.Player;

public class PlayerManager {

    private Player player;

    public PlayerManager(Player player) {
        this.player = player;
    }

    public boolean CreatePlayer() {
        DatabaseManager databaseManager = new DatabaseManager(MagnesifyDungeons.get());
        if(!databaseManager.users().isExists(player.getUniqueId().toString())) {
            databaseManager.users().create(player);
            return true;
        } else {
            return false;
        }
    }

}
