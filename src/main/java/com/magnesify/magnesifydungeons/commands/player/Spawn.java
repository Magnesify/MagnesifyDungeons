package com.magnesify.magnesifydungeons.commands.player;

import com.magnesify.magnesifydungeons.MagnesifyDungeons;
import com.magnesify.magnesifydungeons.dungeon.entitys.DungeonConsole;
import com.magnesify.magnesifydungeons.languages.LanguageFile;
import com.magnesify.magnesifydungeons.modules.Defaults;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Spawn implements CommandExecutor {
    public Spawn(MagnesifyDungeons magnesifyDungeons) {}

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if (commandSender instanceof Player) {
            Player sender = (Player) commandSender;
            DungeonConsole dungeonConsole = new DungeonConsole();
            Defaults defaults = new Defaults();
            if(defaults.MainSpawn().world() != null) {
                if (Bukkit.getWorld(defaults.MainSpawn().world()) != null) {
                    Location loc = new Location(Bukkit.getWorld(defaults.MainSpawn().world()), defaults.MainSpawn().x(), defaults.MainSpawn().y(), defaults.MainSpawn().z(), (float) defaults.MainSpawn().yaw(), (float) defaults.MainSpawn().pitch());
                    sender.teleport(loc);
                } else {
                    dungeonConsole.ConsoleOutputManager().write(new LanguageFile().getLanguage().getString("plugin.spawn-not-exists"));
                }
            } else {
                dungeonConsole.ConsoleOutputManager().write(new LanguageFile().getLanguage().getString("plugin.spawn-not-exists"));
            }
        }
        return false;
    }

}
