package com.magnesify.magnesifydungeons.events;

import com.magnesify.magnesifydungeons.MagnesifyDungeons;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import static com.magnesify.magnesifydungeons.MagnesifyDungeons.get;

public class CustomCommands implements Listener {
    public CustomCommands(MagnesifyDungeons magnesifyDungeons) {}

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        String[] command= event.getMessage().split(" ");
        if (command[0].equalsIgnoreCase("/" + get().getConfig().getString("settings.custom-commands.leave"))) {
            event.getPlayer().chat("/leave");
            event.setCancelled(true);
        }
        if (command[0].equalsIgnoreCase("/" + get().getConfig().getString("settings.custom-commands.join"))) {
            event.getPlayer().chat("/join " + command[1]);
            event.setCancelled(true);
        }
        if (command[0].equalsIgnoreCase("/" + get().getConfig().getString("settings.custom-commands.market"))) {
            event.getPlayer().chat("/dungeonmarket");
            event.setCancelled(true);
        }
        if (command[0].equalsIgnoreCase("/" + get().getConfig().getString("settings.custom-commands.profile"))) {
            event.getPlayer().chat("/dungeonprofile");
            event.setCancelled(true);
        }
        if (command[0].equalsIgnoreCase("/" + get().getConfig().getString("settings.custom-commands.stats"))) {
            event.getPlayer().chat("/stats");
            event.setCancelled(true);
        }
        if (command[0].equalsIgnoreCase("/" + get().getConfig().getString("settings.custom-commands.challanges"))) {
            event.getPlayer().chat("/challanges");
            event.setCancelled(true);
        }
        if (command[0].equalsIgnoreCase("/" + get().getConfig().getString("settings.custom-commands.dungeons"))) {
            event.getPlayer().chat("/dungeons");
            event.setCancelled(true);
        }
    }
}
