package com.magnesify.magnesifydungeons.commands.administrator;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public interface Arguments {

    long reload();
    void help(CommandSender sender);

    List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args);
}
