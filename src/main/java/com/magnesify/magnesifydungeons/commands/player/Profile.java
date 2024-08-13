package com.magnesify.magnesifydungeons.commands.player;

import com.magnesify.magnesifydungeons.MagnesifyDungeons;
import com.magnesify.magnesifydungeons.commands.player.profile.IAProfileGuiLoader;
import com.magnesify.magnesifydungeons.commands.player.profile.ProfileGuiLoader;
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
                    Bukkit.getConsoleSender().sendMessage(parseHexColors("<#4b8eff>[Magnesify Dungeons] &f'settings.profile.custom-gui-texture' ayarlanmış durumda ancak ItemsAdder sunucuda bulunmuyor..."));
                    ProfileGuiLoader.openInventory(player);
                }
            } else {
                ProfileGuiLoader.openInventory(player);
            }
        }
        return false;
    }
}
