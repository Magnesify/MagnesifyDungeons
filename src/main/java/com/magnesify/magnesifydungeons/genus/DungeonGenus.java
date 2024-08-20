package com.magnesify.magnesifydungeons.genus;

import com.magnesify.magnesifydungeons.dungeon.entitys.DungeonPlayer;
import com.magnesify.magnesifydungeons.files.GenusFile;
import com.magnesify.magnesifydungeons.languages.LanguageFile;
import com.magnesify.magnesifydungeons.modules.managers.DatabaseManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.magnesify.magnesifydungeons.MagnesifyDungeons.get;

public class DungeonGenus {

    private Player player;
    final Map<String, Long> lastUseMap = new HashMap<>();

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
        DatabaseManager databaseManager = new DatabaseManager(get());
        return databaseManager.users().getGenus(player.getUniqueId().toString());
    }

    public boolean isGenusSet() {
        DatabaseManager databaseManager = new DatabaseManager(get());
        if(databaseManager.users().getGenus(player.getUniqueId().toString()).equalsIgnoreCase("None")) {
            return false;
        } else {
            return true;
        }
    }

    public void setGenusSkills() {
        GenusFile genusFile = new GenusFile();
        boolean health = genusFile.getGenusConfig().isSet("dungeon-genus." + getGenus() + ".health-bar");
        boolean walk = genusFile.getGenusConfig().isSet("dungeon-genus." + getGenus() + ".walk-speed");
        boolean potion = genusFile.getGenusConfig().isSet("dungeon-genus." + getGenus() + ".potion-effects");
        if(genusFile.getGenusConfig().getString("dungeon-genus." + getGenus()) != null) {
            if(health) {
                player.setMaxHealth(genusFile.getGenusConfig().getDouble("dungeon-genus." + getGenus() + ".health-bar"));
            }
            if(walk) {
                player.setWalkSpeed((float) genusFile.getGenusConfig().getDouble("dungeon-genus." + getGenus() + ".walk-speed"));
            }
            if(player.getActivePotionEffects().size() > 0) {
                for(PotionEffect potionEffect : player.getActivePotionEffects()) {
                    player.removePotionEffect(potionEffect.getType());
                }
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
    }

    public Skills skills() {
        return new Skills();
    }

    public class Skills {

        public Skills(){}

        public void Ghost(Player player) {
            GenusFile genusFile = new GenusFile();
            DungeonPlayer dungeonPlayer = new DungeonPlayer(player);
            boolean ghost = genusFile.getGenusConfig().getBoolean("dungeon-genus." + getGenus() + ".skills.ghost.enable");
            int time = genusFile.getGenusConfig().getInt("dungeon-genus." + getGenus() + ".skills.ghost.delay");
            int countdown = genusFile.getGenusConfig().getInt("dungeon-genus." + getGenus() + ".skills.ghost.countdown");
            if(ghost) {
                long currentTime = System.currentTimeMillis();
                String playerName = player.getName();

                if (lastUseMap.containsKey(playerName)) {
                    long lastUseTime = lastUseMap.get(playerName);
                    if (currentTime - lastUseTime < time*1000) { // 30 saniye bekleme süresi
                        dungeonPlayer.messageManager().title(new LanguageFile().getLanguage().getString("messages.error.title"), new LanguageFile().getLanguage().getString("messages.error.wait").replace("#countdown", String.valueOf(currentTime - lastUseTime)));
                        return;
                    }
                }
                lastUseMap.put(playerName, currentTime);
                player.setGameMode(org.bukkit.GameMode.SPECTATOR);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (player.isOnline()) {
                            player.setGameMode(org.bukkit.GameMode.SURVIVAL);
                        }
                    }
                }.runTaskLater(get(), 20*countdown);
            } else {
                dungeonPlayer.messageManager().title(new LanguageFile().getLanguage().getString("messages.error.title"), new LanguageFile().getLanguage().getString("messages.error.not-your-genus-skill"));
            }
        }

        public void MoreDamage(EntityDamageByEntityEvent event) {
            GenusFile genusFile = new GenusFile();
            if (event.getDamager() instanceof Player) {
                Player player = (Player) event.getDamager();
                boolean ghost = genusFile.getGenusConfig().getBoolean("dungeon-genus." + getGenus() + ".skills.more-damage.enable");
                if (ghost) {
                    if (event.getEntity() instanceof LivingEntity) {
                        ItemStack item = player.getInventory().getItemInMainHand();
                        for(String items : genusFile.getGenusConfig().getStringList("dungeon-genus." + getGenus() + ".skills.more-damage.tools"))
                        if (item.getType() == Material.getMaterial(items, true)) {
                            if(genusFile.getGenusConfig().getString("dungeon-genus." + getGenus() + ".skills.more-damage.mode").equalsIgnoreCase("multiple")) {
                                event.setDamage(event.getDamage()*genusFile.getGenusConfig().getInt("dungeon-genus." + getGenus() + ".skills.more-damage.value"));
                            } else if (genusFile.getGenusConfig().getString("dungeon-genus." + getGenus() + ".skills.more-damage.mode").equalsIgnoreCase("plus")) {
                                event.setDamage(event.getDamage()+genusFile.getGenusConfig().getInt("dungeon-genus." + getGenus() + ".skills.more-damage.value"));
                            }
                        }
                    }
                }
            }
        }

        public void MineBoost(Player player, BlockBreakEvent event) {
            Block block = event.getBlock();
            GenusFile genusFile = new GenusFile();
            DungeonPlayer dungeonPlayer = new DungeonPlayer(player);
            boolean ghost = genusFile.getGenusConfig().getBoolean("dungeon-genus." + getGenus() + ".skills.mine-boost.enable");
            if(ghost) {
                for (String blocks : genusFile.getGenusConfig().getConfigurationSection("dungeon-genus." + getGenus() + ".skills.mine-boost.blocks").getKeys(false)) {
                    if (genusFile.getGenusConfig().getString("dungeon-genus." + getGenus() + ".skills.mine-boost.blocks." + blocks + ".mode").equalsIgnoreCase("multiple")) {
                        if (block.getType() == Material.getMaterial(genusFile.getGenusConfig().getString("dungeon-genus." + getGenus() + ".skills.mine-boost.blocks." + blocks + ".block"))) {
                            ItemStack[] drops = event.getBlock().getDrops().toArray(new ItemStack[0]);
                            for (ItemStack drop : drops) {
                                drop.setAmount(drop.getAmount() * genusFile.getGenusConfig().getInt("dungeon-genus." + getGenus() + ".skills.mine-boost.blocks." + blocks + ".multiple"));
                            }
                            event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), drops[0]);
                            return;
                        }
                    }
                    if (genusFile.getGenusConfig().getString("dungeon-genus." + getGenus() + ".skills.mine-boost.blocks." + blocks + ".mode").equalsIgnoreCase("drop_other")) {
                        if (block.getType() == Material.getMaterial(genusFile.getGenusConfig().getString("dungeon-genus." + getGenus() + ".skills.mine-boost.blocks." + blocks + ".block"))) {
                            ItemStack[] drops = event.getBlock().getDrops().toArray(new ItemStack[0]);
                            for (ItemStack drop : drops) {
                                drop.setAmount(drop.getAmount() * genusFile.getGenusConfig().getInt("dungeon-genus." + getGenus() + ".skills.mine-boost.blocks." + blocks + ".multiple"));
                            }
                            event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), drops[0]);

                            ItemStack itemStack = new ItemStack(Material.getMaterial(genusFile.getGenusConfig().getString("dungeon-genus." + getGenus() + ".skills.mine-boost.blocks." + blocks + ".item")), 1);
                            event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), itemStack);
                            return;
                        }
                    }
                }
            } else {
                dungeonPlayer.messageManager().title(new LanguageFile().getLanguage().getString("messages.error.title"), new LanguageFile().getLanguage().getString("messages.error.not-your-genus-skill"));
            }
        }

    }

    public boolean setGenus(String genus) {
        if(genus().contains(genus)) {
            DatabaseManager databaseManager = new DatabaseManager(get());
            databaseManager.users().setGenus(player.getUniqueId().toString(), genus);
            setGenusSkills();
            return true;
        } else {
            return false;
        }
    }


}
