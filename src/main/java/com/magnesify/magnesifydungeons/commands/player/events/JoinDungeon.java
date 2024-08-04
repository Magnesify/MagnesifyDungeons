package com.magnesify.magnesifydungeons.commands.player.events;

import com.magnesify.magnesifydungeons.MagnesifyDungeons;
import com.magnesify.magnesifydungeons.boss.MagnesifyBoss;
import com.magnesify.magnesifydungeons.dungeon.Dungeon;
import com.magnesify.magnesifydungeons.dungeon.entitys.DungeonEntity;
import com.magnesify.magnesifydungeons.dungeon.entitys.DungeonPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static com.magnesify.magnesifydungeons.MagnesifyDungeons.get;

public class JoinDungeon implements CommandExecutor {
    public JoinDungeon(MagnesifyDungeons magnesifyDungeons) {}

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        DungeonEntity dungeonEntity = new DungeonEntity(commandSender);
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            DungeonPlayer dungeonPlayer = new DungeonPlayer(player);
            if (strings.length == 0) {
                for(String messages : get().getConfig().getStringList("settings.messages.helps.player.join")) {
                    dungeonPlayer.messageManager().chat(messages);
                }
            } else if (strings.length == 1) {
                Dungeon dungeon = new Dungeon(strings[0]);
                if(!dungeonPlayer.inDungeon()) {
                    if(dungeon.exists()) {
                        if(dungeon.parameters().status()) {
                            MagnesifyBoss magnesifyBoss = new MagnesifyBoss(dungeon.parameters().boss());
                            if(magnesifyBoss.exists()) {
                                if(dungeonPlayer.isEnteredFirstTime(strings[0])) {
                                    dungeonPlayer.CreateNewDungeonPlayData(strings[0]);
                                    dungeonPlayer.join(dungeon);
                                    dungeon.events().wait(player, dungeon);
                                    dungeon.updateCurrentPlayer(player.getName());
                                    dungeon.status(false);
                                    for(String messages : get().getConfig().getStringList("settings.messages.events.joined")) {
                                        dungeonPlayer.messageManager().chat(messages.replace("#boss_name", magnesifyBoss.name()).replace("#boss_health", String.valueOf(magnesifyBoss.health())).replace("#next_level", String.valueOf(dungeon.parameters().next())).replace("#category", dungeon.parameters().category()).replace("#playtime", String.valueOf(dungeon.parameters().play())));
                                    }
                                } else {
                                    if(dungeonPlayer.getCurrentLevelForDungeon(strings[0]) == dungeon.parameters().level()) {
                                        dungeonPlayer.join(dungeon);
                                        dungeon.events().wait(player, dungeon);
                                        dungeon.updateCurrentPlayer(player.getName());
                                        dungeon.status(false);
                                        for(String messages : get().getConfig().getStringList("settings.messages.events.joined")) {
                                            dungeonPlayer.messageManager().chat(messages.replace("#boss_name", magnesifyBoss.name()).replace("#boss_health", String.valueOf(magnesifyBoss.health())).replace("#next_level", String.valueOf(dungeon.parameters().next())).replace("#category", dungeon.parameters().category()).replace("#playtime", String.valueOf(dungeon.parameters().play())));
                                        }
                                    } else {
                                        dungeonPlayer.messageManager().chat(get().getConfig().getString("settings.messages.dungeon.not-next-level-dungeon").replace("#name", strings[0]));
                                    }
                                }
                            } else {
                                dungeonPlayer.messageManager().chat(get().getConfig().getString("settings.messages.dungeon.boss-not-exists").replace("#name", strings[0]));
                            }
                        } else {
                            dungeonPlayer.messageManager().chat(get().getConfig().getString("settings.messages.dungeon.full").replace("#countdown", String.valueOf(dungeon.countdown())).replace("#name", strings[0]));
                        }
                    } else {
                        dungeonPlayer.messageManager().chat(get().getConfig().getString("settings.messages.dungeon.unknow-dungeon").replace("#name", strings[0]));
                    }
                } else {
                    dungeonPlayer.messageManager().chat(get().getConfig().getString("settings.messages.dungeon.already-in-dungeon").replace("#name", strings[0]));
                }
            } else {
                for(String messages : get().getConfig().getStringList("settings.messages.helps.player.join")) {
                    dungeonPlayer.messageManager().chat(messages);
                }
            }
        } else {
            dungeonEntity.EntityChatManager().send(get().getConfig().getString("settings.messages.in-game-command"));

        }
        return false;
    }
}
