package com.magnesify.magnesifydungeons.boss;

import com.magnesify.magnesifydungeons.files.Boss;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static com.magnesify.magnesifydungeons.MagnesifyDungeons.get;
import static com.magnesify.magnesifydungeons.dungeon.entitys.DungeonPlayer.parseHexColors;

public class MagnesifyBoss {

    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static String generateRandomString() {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 3; i++) {
            if (i > 0) {
                sb.append("-");
            }
            for (int j = 0; j < 2; j++) {
                sb.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
            }
        }

        return sb.toString();
    }

    private String name, id;
    private Boss boss;

    public MagnesifyBoss(Boss boss, String name, String id) {
        this.name = name;
        this.id = id;
        this.boss = boss;
    }

    public MagnesifyBoss(String name) {
        this.name = name;
    }

    public boolean exists() {
        Boss boss = new Boss();
        return boss.get().getString("boss." + name) != null;
    }

    public List<String> drops() {
        Boss boss = new Boss();
        return boss.get().getStringList("boss." + name + ".drops");
    }

    public boolean create() {
        Boss boss = new Boss();
        if(boss.get().getString("boss." + name) == null) {
            List<String> enchants = new ArrayList<>();
            List<String> drops = new ArrayList<>();
            drops.add("GOLDEN_APPLE:16");
            enchants.add("protection:1");
            boss.get().set("boss." + name + ".name", name);
            boss.get().set("boss." + name + ".id", id);
            boss.get().set("boss." + name + ".mgid", "Empty");
            boss.get().set("boss." + name + ".uuid", "Empty");
            boss.get().set("boss." + name + ".items.helmet.material", "DIAMOND_HELMET");
            boss.get().set("boss." + name + ".items.helmet.enchants", enchants);
            boss.get().set("boss." + name + ".items.chestplate.material", "DIAMOND_CHESTPLATE");
            boss.get().set("boss." + name + ".items.chestplate.enchants", enchants);
            boss.get().set("boss." + name + ".items.leggings.material", "DIAMOND_LEGGINGS");
            boss.get().set("boss." + name + ".items.leggings.enchants", enchants);
            boss.get().set("boss." + name + ".items.boots.material", "DIAMOND_BOOTS");
            boss.get().set("boss." + name + ".items.boots.enchants", enchants);
            boss.get().set("boss." + name + ".items.weapon.material", "DIAMOND_SWORD");
            boss.get().set("boss." + name + ".items.weapon.enchants", enchants);
            boss.get().set("boss." + name + ".generics.damage", 5.0);
            boss.get().set("boss." + name + ".generics.attack-knockback", 5.0);
            boss.get().set("boss." + name + ".generics.max-health", 20.0);
            boss.get().set("boss." + name + ".drops", drops);
            boss.get().set("boss." + name + ".type", "ZOMBIE");
            boss.get().set("boss." + name + ".display-name", ("&c" + name));
            boss.save();
            return true;
        }
        return false;
    }

    public String name() {
        Boss boss = new Boss();
        return boss.get().getString("boss." + name + ".name");
    }

    public double health() {
        Boss boss = new Boss();
        return boss.get().getDouble("boss." + name + ".generics.max-health");
    }

    public void killBoss() {
        Boss boss = new Boss();
        UUID uuid = UUID.fromString(boss.get().getString("boss." + name + ".uuid"));
        Entity entity = Bukkit.getEntity(uuid);
        if (entity.hasMetadata("name")) {
            String metadataValue = entity.getMetadata("name").get(0).asString();
            if(boss.get().getString("boss." + name + ".mgid").equalsIgnoreCase(metadataValue)) {
                entity.remove();
            }
        }
    }

    public void spawn(Location location, Player player) {
        Boss boss = new Boss();
        switch (boss.get().getString("boss." + name + ".type")) {
            case "ZOMBIE":
                Zombie zombie = (Zombie) location.getWorld().spawnEntity(location, EntityType.ZOMBIE);
                zombie.setCustomName(parseHexColors(boss.get().getString("boss." + name + ".display-name")));
                zombie.setCustomNameVisible(true);
                String randStr = generateRandomString();
                boss.get().set("boss." + name + ".mgid", randStr);
                boss.get().set("boss." + name + ".uuid", zombie.getUniqueId().toString());
                boss.save();
                String last_dungeon = get().getPlayers().getLastDungeon(player);
                get().getPlayers().updateLastBoss(player, randStr);
                zombie.setMetadata("name", new FixedMetadataValue(get(), randStr));
                zombie.setMetadata("dungeon", new FixedMetadataValue(get(), last_dungeon));
                zombie.setMetadata("boss", new FixedMetadataValue(get(), name));
                zombie.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(boss.get().getDouble("boss." + name + ".generics.max-health"));
                zombie.setHealth(boss.get().getDouble("boss." + name + ".generics.max-health"));
                zombie.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(boss.get().getDouble("boss." + name + ".generics.damage"));
                zombie.getAttribute(Attribute.GENERIC_ATTACK_KNOCKBACK).setBaseValue(boss.get().getDouble("boss." + name + ".generics.attack-knockback"));

                ItemStack helmet = new ItemStack(Material.getMaterial(boss.get().getString("boss." + name + ".items.helmet.material")));
                ItemMeta helmetmeta = helmet.getItemMeta();
                for(String a : boss.get().getStringList("boss." + name + ".items.helmet.enchants")) {
                    String[] split = a.split(":");
                    NamespacedKey key = new NamespacedKey("minecraft", split[0]);
                    helmetmeta.addEnchant(Enchantment.getByKey(key), Integer.parseInt(split[1]), true);
                }
                helmet.setItemMeta(helmetmeta);

                ItemStack chestplate = new ItemStack(Material.getMaterial(boss.get().getString("boss." + name + ".items.chestplate.material")));
                ItemMeta chestplatemeta = chestplate.getItemMeta();
                for(String a : boss.get().getStringList("boss." + name + ".items.chestplate.enchants")) {
                    String[] split = a.split(":");
                    chestplatemeta.addEnchant(Enchantment.getByName(split[0]), Integer.parseInt(split[1]), true);
                }
                chestplate.setItemMeta(chestplatemeta);

                ItemStack leggings = new ItemStack(Material.getMaterial(boss.get().getString("boss." + name + ".items.leggings.material")));
                ItemMeta leggingsmeta = leggings.getItemMeta();
                for(String a : boss.get().getStringList("boss." + name + ".items.leggings.enchants")) {
                    String[] split = a.split(":");
                    leggingsmeta.addEnchant(Enchantment.getByName(split[0]), Integer.parseInt(split[1]), true);
                }
                leggings.setItemMeta(leggingsmeta);

                ItemStack boots = new ItemStack(Material.getMaterial(boss.get().getString("boss." + name + ".items.boots.material")));
                ItemMeta bootsmeta = boots.getItemMeta();
                for(String a : boss.get().getStringList("boss." + name + ".items.boots.enchants")) {
                    String[] split = a.split(":");
                    bootsmeta.addEnchant(Enchantment.getByName(split[0]), Integer.parseInt(split[1]), true);
                }
                boots.setItemMeta(bootsmeta);


                ItemStack weapon = new ItemStack(Material.getMaterial(boss.get().getString("boss." + name + ".items.weapon.material")));
                ItemMeta weaponmeta = weapon.getItemMeta();
                for(String a : boss.get().getStringList("boss." + name + ".items.weapon.enchants")) {
                    String[] split = a.split(":");
                    weaponmeta.addEnchant(Enchantment.getByName(split[0]), Integer.parseInt(split[1]), true);
                }

                weapon.setItemMeta(weaponmeta);

                zombie.getEquipment().setHelmet(helmet);
                zombie.getEquipment().setChestplate(chestplate);
                zombie.getEquipment().setLeggings(leggings);
                zombie.getEquipment().setBoots(boots);
                zombie.getEquipment().setItemInMainHand(weapon);
                break;
        }
    }

}
