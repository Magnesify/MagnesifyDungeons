package com.magnesify.magnesifydungeons.genus;

import com.magnesify.magnesifydungeons.files.GenusFile;
import com.magnesify.magnesifydungeons.files.JsonStorage;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.magnesify.magnesifydungeons.MagnesifyDungeons.get;

public class DungeonGenus {

    private Player player;

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
        boolean potion = genusFile.getGenusConfig().isSet("dungeon-genus." + getGenus() + ".potion-effects");
        if(health) {
            player.setMaxHealth(genusFile.getGenusConfig().getDouble("dungeon-genus." + getGenus() + ".health-bar"));
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
