package com.magnesify.magnesifydungeons.genus;

import com.magnesify.magnesifydungeons.dungeon.entitys.DungeonPlayer;
import com.magnesify.magnesifydungeons.files.GenusFile;
import com.magnesify.magnesifydungeons.files.JsonStorage;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.JSONObject;

import java.util.*;

import static com.magnesify.magnesifydungeons.MagnesifyDungeons.get;

public class DungeonGenus {

    private Player player;

    public HashMap<UUID, Integer> gerisayin = new HashMap<>();
    public Map<UUID, Integer> ghost = new HashMap<>();



    public JsonStorage load() {
        JsonStorage stats = new JsonStorage(get().getDataFolder() + "/caches/genus.json");
        return stats;
    }

    public DungeonGenus(Player player) {
        this.player = player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public List<String> genus() {
        GenusFile genusFile = new GenusFile();
        List<String> list = new ArrayList<>();
        for(String keys : genusFile.getGenusConfig().getConfigurationSection("dungeon-genus").getKeys(false)) {
            list.add(keys);
        }
        return list;

    }

    public String getGenus() {
        return (String) load().getValue(player.getName() +"." + player.getUniqueId().toString() + ".genus");
    }

    public boolean isGenusSet() {
        return load().getValue(player.getName() +"." + player.getUniqueId().toString() + ".genus") !=null;
    }

    public void setGenusSkills() {
        GenusFile genusFile = new GenusFile();
        boolean health = genusFile.getGenusConfig().isSet("dungeon-genus." + getGenus() + ".health-bar");
        boolean walk = genusFile.getGenusConfig().isSet("dungeon-genus." + getGenus() + ".walk-speed");
        boolean potion = genusFile.getGenusConfig().isSet("dungeon-genus." + getGenus() + ".potion-effects");
        if(health) {
            player.setMaxHealth(genusFile.getGenusConfig().getDouble("dungeon-genus." + getGenus() + ".health-bar"));
        }
        if(walk) {
            player.setWalkSpeed((float) genusFile.getGenusConfig().getDouble("dungeon-genus." + getGenus() + ".walk-speed"));
        }
        if(potion) {
            for(String effects : genusFile.getGenusConfig().getStringList("dungeon-genus." + getGenus() + ".potion-effects")) {
                String[] spl = effects.split(":");
                String effet = spl[0];
                int power = Integer.parseInt(spl[1]);
                PotionEffect speedEffect = new PotionEffect(PotionEffectType.getByName(effet), Integer.MAX_VALUE, power);
                player.addPotionEffect(speedEffect);
            }
        }
    }

    public Skills skills() {
        return new Skills();
    }

    public class Skills {

        public Skills(){}

        public void waitChange(Player player, int wait, int a) {
            gerisayin.put(player.getUniqueId(), wait);
            change(player,a );
            DungeonPlayer dungeonPlayer = new DungeonPlayer(player);
            UUID playerId = player.getUniqueId();
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (gerisayin.containsKey(playerId)) {
                        int remainingTime = gerisayin.get(playerId);
                        if (remainingTime > 0) {
                            gerisayin.put(playerId, remainingTime - 1);
                            dungeonPlayer.messageManager().actionbar("&a&l" + gerisayin.get(playerId) + " &fsaniye sonra tekrardan hayalet özelliğini kullanabilirsin.");
                        } else {
                            gerisayin.remove(playerId);
                            this.cancel();
                        }
                    } else {
                        this.cancel();
                    }
                }
            }.runTaskTimer(get(), 0, 20);
        }

        public void change(Player player, int wait) {
            ghost.put(player.getUniqueId(), wait);
            new BukkitRunnable() {
                @Override
                public void run() {
                    UUID playerId = player.getUniqueId();
                    if (ghost.containsKey(playerId)) {
                        int remainingTime = ghost.get(playerId);
                        if (remainingTime > 0) {
                            if(player.getGameMode() != GameMode.SPECTATOR) {
                                player.setGameMode(GameMode.SPECTATOR);
                            }
                            ghost.put(playerId, remainingTime - 1);
                        } else {
                            player.setGameMode(GameMode.SURVIVAL);
                            ghost.remove(playerId);
                            this.cancel();
                        }
                    } else {
                        this.cancel();
                    }
                }
            }.runTaskTimer(get(), 0, 20);
        }

        public void Ghost(Player player) {
            GenusFile genusFile = new GenusFile();
            DungeonPlayer dungeonPlayer = new DungeonPlayer(player);
            boolean ghost = genusFile.getGenusConfig().getBoolean("dungeon-genus." + getGenus() + ".skills.ghost.enable");
            if(ghost) {
                if(gerisayin.containsKey(player.getUniqueId()) || gerisayin.containsKey(player.getUniqueId()) || gerisayin.containsKey(player.getUniqueId())) {
                    dungeonPlayer.messageManager().title("&c&lHATA", "&fHayalet moduna tekrar geçmek için " + gerisayin.get(player.getUniqueId()) + " saniye beklemen gerek.");
                } else {
                    waitChange(player, genusFile.getGenusConfig().getInt("dungeon-genus." + getGenus() + ".skills.ghost.delay"), genusFile.getGenusConfig().getInt("dungeon-genus." + getGenus() + ".skills.ghost.countdown"));
                }
            } else {
                dungeonPlayer.messageManager().title("&c&lHATA", "&fTürün bu özelliğe sahip değil !");
            }
        }

        public void MineBoost(Player player, BlockBreakEvent event) {
            Block block = event.getBlock();
            GenusFile genusFile = new GenusFile();
            DungeonPlayer dungeonPlayer = new DungeonPlayer(player);
            boolean ghost = genusFile.getGenusConfig().getBoolean("dungeon-genus." + getGenus() + ".skills.mine-boost.enable");
            if(ghost) {
                for(String blocks : genusFile.getGenusConfig().getConfigurationSection("dungeon-genus." + getGenus() + ".skills.mine-boost.blocks").getKeys(false)) {
                    if(block.getType() == Material.getMaterial(genusFile.getGenusConfig().getString("dungeon-genus." + getGenus() + ".skills.mine-boost.blocks." + blocks + ".block"))) {
                        ItemStack[] drops = event.getBlock().getDrops().toArray(new ItemStack[0]);
                        for (ItemStack drop : drops) {
                            drop.setAmount(drop.getAmount() * genusFile.getGenusConfig().getInt("dungeon-genus." + getGenus() + ".skills.mine-boost.blocks." + blocks + ".multiple"));
                        }
                        event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), drops[0]);
                        event.setDropItems(false);
                        return;
                    }
                }
            } else {
                dungeonPlayer.messageManager().title("&c&lHATA", "&fTürün bu özelliğe sahip değil !");
            }
        }

    }

    public boolean setGenus(String genus) {
        if(genus().contains(genus)) {
            JSONObject players_config = new JSONObject();
            players_config.put(player.getName() +"." + player.getUniqueId().toString() + ".genus", genus);
            load().writeData(players_config);
            setGenusSkills();
            return true;
        } else {
            return false;
        }
    }


}
