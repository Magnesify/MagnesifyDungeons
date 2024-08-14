package com.magnesify.magnesifydungeons.dungeon.types.challange.gui;

import com.magnesify.magnesifydungeons.MagnesifyDungeons;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static com.magnesify.magnesifydungeons.MagnesifyDungeons.get;
import static com.magnesify.magnesifydungeons.dungeon.entitys.DungeonPlayer.parseHexColors;

public class ChallangeGuiOpen implements CommandExecutor {
    public ChallangeGuiOpen(MagnesifyDungeons magnesifyDungeons) {}

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(commandSender instanceof Player) {
            Player player = (Player) commandSender;
            boolean textc = get().getConfig().isSet("settings.challange.custom-gui-texture");
            if(textc) {
                if(Bukkit.getPluginManager().getPlugin("ItemsAdder") != null) {
                    IAChallangeGuiLoader.openInventory(player);
                } else {
                    Bukkit.getConsoleSender().sendMessage(parseHexColors("<#4b8eff>[Magnesify Dungeons] &f'settings.challange.custom-gui-texture' ayarlanmış durumda ancak ItemsAdder sunucuda bulunmuyor..."));
                    ChallangeGuiLoader.openInventory(player);
                }
            } else {
                ChallangeGuiLoader.openInventory(player);
            }
        }
        return false;
    }
}
