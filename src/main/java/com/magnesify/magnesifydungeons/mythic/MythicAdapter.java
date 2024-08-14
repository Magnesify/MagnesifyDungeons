package com.magnesify.magnesifydungeons.mythic;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.Optional;

public class MythicAdapter {

    public MythicAdapter(){}

    public boolean isMythicMob(Entity entity) {
        if(Bukkit.getPluginManager().getPlugin("MythicMobs") == null) {
            return false;
        } else {
            return MythicBukkit.inst().getMobManager().isMythicMob(entity);
        }
    }

    public void MythicMobDeath(EntityDeathEvent event) {
        Entity bukkitEntity = event.getEntity();
        if (isMythicMob(bukkitEntity)) {
            Optional<ActiveMob> optActiveMob = MythicBukkit.inst().getMobManager().getActiveMob(bukkitEntity.getUniqueId());
            optActiveMob.ifPresent(activeMob -> {
                if (activeMob.getEntity().hasMetadata("name")) {
                    String metadataValue = bukkitEntity.getMetadata("name").get(0).asString();
                }
            });
        }
    }
}
