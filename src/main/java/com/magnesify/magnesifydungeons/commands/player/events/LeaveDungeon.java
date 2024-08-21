package com.magnesify.magnesifydungeons.commands.player.events;

import com.magnesify.magnesifydungeons.MagnesifyDungeons;
import com.magnesify.magnesifydungeons.dungeon.Dungeon;
import com.magnesify.magnesifydungeons.dungeon.TriggerType;
import com.magnesify.magnesifydungeons.dungeon.entitys.DungeonEntity;
import com.magnesify.magnesifydungeons.dungeon.entitys.DungeonPlayer;
import com.magnesify.magnesifydungeons.dungeon.types.challange.Challange;
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
                TriggerType triggerType = new TriggerType(player);
                Challange challange = new Challange(dungeonName);
                if(dungeonPlayer.inDungeon()) {
                    dungeon.status(true);
                    dungeonPlayer.leave(dungeon);
                    dungeon.events().stop(player);
                    triggerType.leave(player, dungeonName);
                    dungeonPlayer.leaveChallange(challange);
                    dungeonPlayer.messageManager().chat(new LanguageFile().getLanguage().getString("messages.dungeon.leave-success"));
                } else {
                    dungeonPlayer.messageManager().chat(new LanguageFile().getLanguage().getString("messages.dungeon.unknow-dungeon"));
                }
            } else {
                dungeonPlayer.messageManager().chat(new LanguageFile().getLanguage().getString("messages.dungeon.not-in-dungeon"));
            }
        } else {
            dungeonEntity.EntityChatManager().send(new LanguageFile().getLanguage().getString("messages.in-game-command"));

        }
        return false;
    }
}
