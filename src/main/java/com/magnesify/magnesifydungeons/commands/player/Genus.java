package com.magnesify.magnesifydungeons.commands.player;

import com.magnesify.magnesifydungeons.MagnesifyDungeons;
import com.magnesify.magnesifydungeons.dungeon.entitys.DungeonPlayer;
import com.magnesify.magnesifydungeons.genus.gui.GenusGuiLoader;
import com.magnesify.magnesifydungeons.genus.gui.IAGenusGuiLoader;
import com.magnesify.magnesifydungeons.languages.LanguageFile;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static com.magnesify.magnesifydungeons.MagnesifyDungeons.get;
import static com.magnesify.magnesifydungeons.dungeon.entitys.DungeonPlayer.parseHexColors;

public class Genus implements CommandExecutor {
    public Genus(MagnesifyDungeons magnesifyDungeons) {}

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            boolean textc = get().getConfig().isSet("settings.genus.custom-gui-texture");
            if(strings.length == 0) {

                if(textc) {
                    if(Bukkit.getPluginManager().getPlugin("ItemsAdder") != null) {
                        IAGenusGuiLoader.openInventory(player);
                    } else {
                        Bukkit.getConsoleSender().sendMessage(parseHexColors(new LanguageFile().getLanguage().getString("plugin.error.ia.genus")));
                        GenusGuiLoader.openInventory(player);
                    }
                } else {
                    GenusGuiLoader.openInventory(player);
                }
            } else if (strings.length == 1) {
                if(strings[0].equalsIgnoreCase("return-human")) {
                    player.setMaxHealth(20.0);
                    player.setWalkSpeed(0.4f);
                    new DungeonPlayer(player).messageManager().actionbar((new LanguageFile().getLanguage().getString("messages.genus.returned-human")));
                } else {
                    if(textc) {
                        if(Bukkit.getPluginManager().getPlugin("ItemsAdder") != null) {
                            IAGenusGuiLoader.openInventory(player);
                        } else {
                            Bukkit.getConsoleSender().sendMessage(parseHexColors(new LanguageFile().getLanguage().getString("plugin.error.ia.genus")));
                            GenusGuiLoader.openInventory(player);
                        }
                    } else {
                        GenusGuiLoader.openInventory(player);
                    }
                }
            } else {
                if(textc) {
                    if(Bukkit.getPluginManager().getPlugin("ItemsAdder") != null) {
                        IAGenusGuiLoader.openInventory(player);
                    } else {
                        Bukkit.getConsoleSender().sendMessage(parseHexColors(new LanguageFile().getLanguage().getString("plugin.error.ia.genus")));
                        GenusGuiLoader.openInventory(player);
                    }
                } else {
                    GenusGuiLoader.openInventory(player);
                }
            }
        }
        return false;
    }
}
