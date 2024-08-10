package com.magnesify.magnesifydungeons.dungeon.types.trigger;

import org.bukkit.Location;

public class TriggerVector {

    public static boolean isInTriggerLocation(Location playerLocation, Location centerLocation) {
        double xDiff = Math.abs(playerLocation.getX() - centerLocation.getX());
        double yDiff = Math.abs(playerLocation.getY() - centerLocation.getY());
        double zDiff = Math.abs(playerLocation.getZ() - centerLocation.getZ());
        return xDiff <= 1.0 && yDiff <= 1.0 && zDiff <= 1.0;
    }
}
