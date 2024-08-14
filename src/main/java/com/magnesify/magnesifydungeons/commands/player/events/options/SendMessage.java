package com.magnesify.magnesifydungeons.commands.player.events.options;

import com.magnesify.magnesifydungeons.MagnesifyDungeons;
import com.magnesify.magnesifydungeons.files.Options;
import com.magnesify.magnesifydungeons.storage.PlayerMethods;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class SendMessage implements Listener {
    public SendMessage(MagnesifyDungeons magnesifyDungeons) {}

    @EventHandler
    public void message(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        Options options = new Options();
        if(options.get().getBoolean("options.players-can-use-chat-while-in-dungeon")) {
            if(!player.hasPermission("mgd.bypass.chat_block")) {
                event.setCancelled(true);
            }
        }
    }

}
