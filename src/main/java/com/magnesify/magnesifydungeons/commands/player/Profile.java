package com.magnesify.magnesifydungeons.commands.player;

import com.magnesify.magnesifydungeons.MagnesifyDungeons;
import com.magnesify.magnesifydungeons.commands.player.profile.IAProfileGuiLoader;
import com.magnesify.magnesifydungeons.commands.player.profile.ProfileGuiLoader;
import com.magnesify.magnesifydungeons.languages.LanguageFile;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static com.magnesify.magnesifydungeons.MagnesifyDungeons.get;
import static com.magnesify.magnesifydungeons.dungeon.entitys.DungeonPlayer.parseHexColors;

public class Profile implements CommandExecutor {
    public Profile(MagnesifyDungeons magnesifyDungeons) {}

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            boolean textc = get().getConfig().isSet("settings.profile.custom-gui-texture");
            if(textc) {
                if(Bukkit.getPluginManager().getPlugin("ItemsAdder") != null) {
                    IAProfileGuiLoader.openInventory(player);
                } else {
                    Bukkit.getConsoleSender().sendMessage(parseHexColors(new LanguageFile().getLanguage("tr").getString("plugin.error.ia.profile")));
                    ProfileGuiLoader.openInventory(player);
                }
            } else {
                ProfileGuiLoader.openInventory(player);
            }
        }
        return false;
    }
}
