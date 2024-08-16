package com.magnesify.magnesifydungeons.commands.player.events;

import com.magnesify.magnesifydungeons.MagnesifyDungeons;
import com.magnesify.magnesifydungeons.dungeon.Dungeon;
import com.magnesify.magnesifydungeons.dungeon.entitys.DungeonEntity;
import com.magnesify.magnesifydungeons.dungeon.entitys.DungeonPlayer;
import com.magnesify.magnesifydungeons.languages.LanguageFile;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static com.magnesify.magnesifydungeons.MagnesifyDungeons.get;

public class LeaveDungeon implements CommandExecutor {
    public LeaveDungeon(MagnesifyDungeons magnesifyDungeons) {}

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        DungeonEntity dungeonEntity = new DungeonEntity(commandSender);
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            DungeonPlayer dungeonPlayer = new DungeonPlayer(player);
            if(dungeonPlayer.inDungeon()) {
                String dungeonName = get().getPlayers().getLastDungeon(player);
                Dungeon dungeon = new Dungeon(dungeonName);
                if(dungeonPlayer.inDungeon()) {
                    dungeon.status(true);
                    dungeonPlayer.leave(dungeon);
                    dungeon.events().stop(player);
                    dungeonPlayer.messageManager().chat(new LanguageFile().getLanguage(MagnesifyDungeons.locale).getString("messages.dungeon.leave-success"));
                } else {
                    dungeonPlayer.messageManager().chat(new LanguageFile().getLanguage(MagnesifyDungeons.locale).getString("messages.dungeon.unknow-dungeon"));
                }
            } else {
                dungeonPlayer.messageManager().chat(new LanguageFile().getLanguage(MagnesifyDungeons.locale).getString("messages.dungeon.not-in-dungeon"));
            }
        } else {
            dungeonEntity.EntityChatManager().send(new LanguageFile().getLanguage(MagnesifyDungeons.locale).getString("messages.in-game-command"));

        }
        return false;
    }
}
