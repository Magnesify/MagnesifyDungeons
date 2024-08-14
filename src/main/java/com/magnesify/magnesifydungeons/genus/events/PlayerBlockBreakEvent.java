package com.magnesify.magnesifydungeons.genus.events;

import com.magnesify.magnesifydungeons.MagnesifyDungeons;
import com.magnesify.magnesifydungeons.genus.DungeonGenus;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class PlayerBlockBreakEvent implements Listener {
    public PlayerBlockBreakEvent(MagnesifyDungeons magnesifyDungeons) {}

    @EventHandler
    public void BreakEvent(BlockBreakEvent event) {
        DungeonGenus genus = new DungeonGenus(event.getPlayer());
        if(genus.isGenusSet()) {
            genus.skills().MineBoost(genus.getPlayer(), event);
        }
    }
}
