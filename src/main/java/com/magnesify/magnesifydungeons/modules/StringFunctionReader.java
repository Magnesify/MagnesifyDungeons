package com.magnesify.magnesifydungeons.modules;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class StringFunctionReader {

    public static void RunFunction(Player forPlayer, String ft) {
        if (ft.startsWith("[command:console] ")) {
            String function = ft.substring(18).trim();
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), function.replace("<player>", forPlayer.getName()));
        } else if (ft.startsWith("[command:player] ")) {
            String function = ft.substring(17).trim();
            forPlayer.performCommand(function.replace("<player>", forPlayer.getName()));
        } else if (ft.startsWith("[chat] ")) {
            String function = ft.substring(7).trim();
            forPlayer.sendMessage(ChatColor.translateAlternateColorCodes('&', function.replace("<nl>", "\n")));
        } else if (ft.startsWith("[broadcast:chat] ")) {
            String function = ft.substring(17).trim();
            for(Player player : Bukkit.getOnlinePlayers()){
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', function.replace("<nl>", "\n").replace("<player>", forPlayer.getName())));
            }
        } else if (ft.startsWith("[broadcast:actionbar] ")) {
            String function = ft.substring(22).trim();
            for(Player player : Bukkit.getOnlinePlayers()){
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&',function.replace("<player>", forPlayer.getName()))));
            }
        } else if (ft.startsWith("[actionbar] ")) {
            String function = ft.substring(12).trim();
            forPlayer.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', function)));
        } else if (ft.startsWith("[sound] ")) {
            String function = ft.substring(8).trim();
            forPlayer.playSound(forPlayer.getLocation(), Sound.valueOf(function), 3.0F, 0.5F);
        } else if (ft.startsWith("[give] ")) {
            String function = ft.substring(7);
            String[] split = function.split(" ");
            forPlayer.getInventory().addItem(new ItemStack(Material.getMaterial(split[0]), Integer.parseInt(split[1])));
        }
    }
}
