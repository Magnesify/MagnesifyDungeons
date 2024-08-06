package com.magnesify.magnesifydungeons.dungeon.types.trigger;

import org.bukkit.Location;

public class TriggerVector {

    private boolean isInTriggerLocation(Location playerLocation, Location centerLocation) {
        double xDiff = Math.abs(playerLocation.getX() - centerLocation.getX());
        double yDiff = Math.abs(playerLocation.getY() - centerLocation.getY());
        double zDiff = Math.abs(playerLocation.getZ() - centerLocation.getZ());
        return xDiff <= 1 && yDiff <= 1 && zDiff <= 1;
    }
}
