package com.magnesify.magnesifydungeons.boss;

import com.magnesify.magnesifydungeons.dungeon.entitys.DungeonConsole;
import com.magnesify.magnesifydungeons.languages.LanguageFile;
import com.magnesify.magnesifydungeons.modules.managers.DatabaseManager;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import static com.magnesify.magnesifydungeons.MagnesifyDungeons.get;
import static com.magnesify.magnesifydungeons.dungeon.entitys.DungeonPlayer.parseHexColors;

public class MagnesifyBoss {

    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public MagnesifyBoss() {

    }

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

    public MagnesifyBoss(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public MagnesifyBoss(String name) {
        this.name = name;
    }

    public boolean exists() {
        DatabaseManager databaseManager = new DatabaseManager(get());
        return databaseManager.isBossAvailable(name);
    }

    public List<String> drops() {
        DatabaseManager databaseManager = new DatabaseManager(get());
        return databaseManager.boss().getDrops(name);
    }

    public boolean create() {
        DatabaseManager databaseManager = new DatabaseManager(get());
        if(!databaseManager.isBossAvailable(name)) {
            databaseManager.CreateNewBoss(name, id, "Empty", "Empty", "DIAMOND_HELMET/protection:1,mending:1", "DIAMOND_CHESTPLATE/protection:1,mending:1", "DIAMOND_LEGGINGS/protection:1,mending:1", "DIAMOND_BOOTS/protection:1,mending:1", "DIAMOND_SWORD/sharpness:1,mending:1", "20.0:5.0:40.0", "GOLDEN_APPLE:1/DIAMOND:1", "ZOMBIE", String.format("&c&l%s", name));
            return true;
        }
        return false;
    }

    public String name() {
        DatabaseManager databaseManager = new DatabaseManager(get());
        return databaseManager.boss().getName(name);
    }

    public double health() {
        DatabaseManager databaseManager = new DatabaseManager(get());
        return databaseManager.boss().getHealth(name);
    }

    public void killBoss(String id) {
        DatabaseManager databaseManager = new DatabaseManager(get());
        UUID uuid = UUID.fromString(databaseManager.boss().getUUIDByMGID(id));
        Entity entity = Bukkit.getEntity(uuid);
        if(entity != null) {
            if (entity.hasMetadata("name")) {
                String metadataValue = entity.getMetadata("name").get(0).asString();
                if(databaseManager.boss().getMGIDByUUID(uuid.toString()).equalsIgnoreCase(metadataValue)) {
                    entity.remove();
                }
            }
        }
    }

    public void spawn(Location location, Player player) {
        DatabaseManager databaseManager = new DatabaseManager(get());
        if(databaseManager.boss().isMythic(name)) {
            MythicMob mob = MythicBukkit.inst().getMobManager().getMythicMob(name).orElse(null);
            if(mob != null){
                ActiveMob activeMob = mob.spawn(BukkitAdapter.adapt(location),1);
                Entity entity = activeMob.getEntity().getBukkitEntity();
                String randStrPhantom = generateRandomString();
                databaseManager.boss().setMGID(name, randStrPhantom);
                databaseManager.boss().setUUID(name, entity.getUniqueId().toString());
                String last_dungeon = get().getPlayers().getLastDungeon(player);
                get().getPlayers().updateLastBoss(player, randStrPhantom);
                entity.setMetadata("name", new FixedMetadataValue(get(), randStrPhantom));
                entity.setMetadata("dungeon", new FixedMetadataValue(get(), last_dungeon));
                entity.setMetadata("boss", new FixedMetadataValue(get(), name));
            } else {
                new DungeonConsole().ConsoleOutputManager().write(String.format(new LanguageFile().getLanguage().getString("messages.boss.unknow-boss"), name));

            }
        } else {
            switch (databaseManager.boss().getType(name)) {
                case "MAGMA_CUBE":
                    MagmaCube magmaCube = (MagmaCube) location.getWorld().spawnEntity(location, EntityType.MAGMA_CUBE);
                    magmaCube.setCustomName(parseHexColors(databaseManager.boss().getDisplay(name)));
                    magmaCube.setCustomNameVisible(true);
                    magmaCube.setSize(Integer.MAX_VALUE);
                    String randStrPhantoms = generateRandomString();
                    databaseManager.boss().setMGID(name, randStrPhantoms);
                    databaseManager.boss().setUUID(name, magmaCube.getUniqueId().toString());
                    String last_dungeons = get().getPlayers().getLastDungeon(player);
                    get().getPlayers().updateLastBoss(player, randStrPhantoms);
                    magmaCube.setMetadata("name", new FixedMetadataValue(get(), randStrPhantoms));
                    magmaCube.setMetadata("dungeon", new FixedMetadataValue(get(), last_dungeons));
                    magmaCube.setMetadata("boss", new FixedMetadataValue(get(), name));
                    magmaCube.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(databaseManager.boss().getHealth(name));
                    magmaCube.setHealth(databaseManager.boss().getHealth(name));
                    magmaCube.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(databaseManager.boss().getAttack(name));
                    magmaCube.getAttribute(Attribute.GENERIC_ATTACK_KNOCKBACK).setBaseValue(databaseManager.boss().getKnockback(name));
                    break;
                case "WITHER_SKELETON":
                    WitherSkeleton witherSkeleton = (WitherSkeleton) location.getWorld().spawnEntity(location, EntityType.WITHER_SKELETON);
                    witherSkeleton.setCustomName(parseHexColors(databaseManager.boss().getDisplay(name)));
                    witherSkeleton.setCustomNameVisible(true);
                    String randStrPhantomswitherSkeleton = generateRandomString();
                    databaseManager.boss().setMGID(name, randStrPhantomswitherSkeleton);
                    databaseManager.boss().setUUID(name, witherSkeleton.getUniqueId().toString());
                    String last_dungeonss = get().getPlayers().getLastDungeon(player);
                    get().getPlayers().updateLastBoss(player, randStrPhantomswitherSkeleton);
                    witherSkeleton.setMetadata("name", new FixedMetadataValue(get(), randStrPhantomswitherSkeleton));
                    witherSkeleton.setMetadata("dungeon", new FixedMetadataValue(get(), last_dungeonss));
                    witherSkeleton.setMetadata("boss", new FixedMetadataValue(get(), name));
                    witherSkeleton.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(databaseManager.boss().getHealth(name));
                    witherSkeleton.setHealth(databaseManager.boss().getHealth(name));
                    witherSkeleton.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(databaseManager.boss().getAttack(name));
                    witherSkeleton.getAttribute(Attribute.GENERIC_ATTACK_KNOCKBACK).setBaseValue(databaseManager.boss().getKnockback(name));
                    ItemStack helmetwitherSkeleton = new ItemStack(Material.getMaterial(databaseManager.boss().getHelmetItem(name)));
                    ItemMeta helmetmetawitherSkeleton = helmetwitherSkeleton.getItemMeta();
                    for(String a : databaseManager.boss().getHelmetEnchants(name)) {
                        String[] split = a.split(":");
                        NamespacedKey key = new NamespacedKey("minecraft", split[0]);
                        helmetmetawitherSkeleton.addEnchant(Enchantment.getByKey(key), Integer.parseInt(split[1]), true);
                    }
                    helmetwitherSkeleton.setItemMeta(helmetmetawitherSkeleton);

                    ItemStack chestplatewitherSkeleton = new ItemStack(Material.getMaterial(databaseManager.boss().getChestplateItem(name)));
                    ItemMeta chestplatemetawitherSkeleton = chestplatewitherSkeleton.getItemMeta();
                    for(String a : databaseManager.boss().getChestplateEnchants(name)) {
                        String[] split = a.split(":");
                        NamespacedKey key = new NamespacedKey("minecraft", split[0]);
                        chestplatemetawitherSkeleton.addEnchant(Enchantment.getByKey(key), Integer.parseInt(split[1]), true);
                    }
                    chestplatewitherSkeleton.setItemMeta(chestplatemetawitherSkeleton);

                    ItemStack leggingswitherSkeleton = new ItemStack(Material.getMaterial(databaseManager.boss().getLeggingsItem(name)));
                    ItemMeta  leggingsmetawitherSkeleton = leggingswitherSkeleton.getItemMeta();
                    for(String a : databaseManager.boss().getLeggingsEnchant(name)) {
                        String[] split = a.split(":");
                        NamespacedKey key = new NamespacedKey("minecraft", split[0]);
                        leggingsmetawitherSkeleton.addEnchant(Enchantment.getByKey(key), Integer.parseInt(split[1]), true);
                    }
                    leggingswitherSkeleton.setItemMeta(leggingsmetawitherSkeleton);

                    ItemStack bootswitherSkeleton = new ItemStack(Material.getMaterial(databaseManager.boss().getBootsItem(name)));
                    ItemMeta bootsmetawitherSkeleton = bootswitherSkeleton.getItemMeta();
                    for(String a : databaseManager.boss().getBootsEnchant(name)) {
                        String[] split = a.split(":");
                        NamespacedKey key = new NamespacedKey("minecraft", split[0]);
                        bootsmetawitherSkeleton.addEnchant(Enchantment.getByKey(key), Integer.parseInt(split[1]), true);
                    }
                    bootswitherSkeleton.setItemMeta(bootsmetawitherSkeleton);


                    ItemStack weaponwitherSkeleton = new ItemStack(Material.getMaterial(databaseManager.boss().getWeaponsItem(name)));
                    ItemMeta weaponmetawitherSkeleton = weaponwitherSkeleton.getItemMeta();
                    for(String a : databaseManager.boss().getWeaponEnchant(name)) {
                        String[] split = a.split(":");
                        NamespacedKey key = new NamespacedKey("minecraft", split[0]);
                        weaponmetawitherSkeleton.addEnchant(Enchantment.getByKey(key), Integer.parseInt(split[1]), true);
                    }

                    weaponwitherSkeleton.setItemMeta(weaponmetawitherSkeleton);

                    witherSkeleton.getEquipment().setHelmet(helmetwitherSkeleton);
                    witherSkeleton.getEquipment().setChestplate(chestplatewitherSkeleton);
                    witherSkeleton.getEquipment().setLeggings(leggingswitherSkeleton);
                    witherSkeleton.getEquipment().setBoots(bootswitherSkeleton);
                    witherSkeleton.getEquipment().setItemInMainHand(weaponwitherSkeleton);
                    break;
                case "PHANTOM":
                    Phantom phantom = (Phantom) location.getWorld().spawnEntity(location, EntityType.PHANTOM);
                    phantom.setCustomName(parseHexColors(databaseManager.boss().getDisplay(name)));
                    phantom.setCustomNameVisible(true);
                    String randStrPhantom = generateRandomString();
                    databaseManager.boss().setMGID(name, randStrPhantom);
                    databaseManager.boss().setUUID(name, phantom.getUniqueId().toString());
                    String last_dungeon = get().getPlayers().getLastDungeon(player);
                    get().getPlayers().updateLastBoss(player, randStrPhantom);
                    phantom.setMetadata("name", new FixedMetadataValue(get(), randStrPhantom));
                    phantom.setMetadata("dungeon", new FixedMetadataValue(get(), last_dungeon));
                    phantom.setMetadata("boss", new FixedMetadataValue(get(), name));
                    phantom.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(databaseManager.boss().getHealth(name));
                    phantom.setHealth(databaseManager.boss().getHealth(name));
                    phantom.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(databaseManager.boss().getAttack(name));
                    phantom.getAttribute(Attribute.GENERIC_ATTACK_KNOCKBACK).setBaseValue(databaseManager.boss().getKnockback(name));
                    break;
                case "ZOMBIE":
                    Zombie zombie = (Zombie) location.getWorld().spawnEntity(location, EntityType.ZOMBIE);
                    zombie.setCustomName(parseHexColors(databaseManager.boss().getDisplay(name)));
                    zombie.setCustomNameVisible(true);
                    String randStr = generateRandomString();
                    databaseManager.boss().setMGID(name, randStr);
                    databaseManager.boss().setUUID(name, zombie.getUniqueId().toString());
                    String last_dungeonzombie = get().getPlayers().getLastDungeon(player);
                    get().getPlayers().updateLastBoss(player, randStr);
                    zombie.setMetadata("name", new FixedMetadataValue(get(), randStr));
                    zombie.setMetadata("dungeon", new FixedMetadataValue(get(), last_dungeonzombie));
                    zombie.setMetadata("boss", new FixedMetadataValue(get(), name));
                    zombie.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(databaseManager.boss().getHealth(name));
                    zombie.setHealth(databaseManager.boss().getHealth(name));
                    zombie.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(databaseManager.boss().getAttack(name));
                    zombie.getAttribute(Attribute.GENERIC_ATTACK_KNOCKBACK).setBaseValue(databaseManager.boss().getKnockback(name));

                    ItemStack helmet = new ItemStack(Material.getMaterial(databaseManager.boss().getHelmetItem(name)));
                    ItemMeta helmetmeta = helmet.getItemMeta();
                    for(String a : databaseManager.boss().getHelmetEnchants(name)) {
                        String[] split = a.split(":");
                        NamespacedKey key = new NamespacedKey("minecraft", split[0]);
                        helmetmeta.addEnchant(Enchantment.getByKey(key), Integer.parseInt(split[1]), true);
                    }
                    helmet.setItemMeta(helmetmeta);

                    ItemStack chestplate = new ItemStack(Material.getMaterial(databaseManager.boss().getChestplateItem(name)));
                    ItemMeta chestplatemeta = chestplate.getItemMeta();
                    for(String a : databaseManager.boss().getChestplateEnchants(name)) {
                        String[] split = a.split(":");
                        NamespacedKey key = new NamespacedKey("minecraft", split[0]);
                        chestplatemeta.addEnchant(Enchantment.getByKey(key), Integer.parseInt(split[1]), true);
                    }
                    chestplate.setItemMeta(chestplatemeta);

                    ItemStack leggings = new ItemStack(Material.getMaterial(databaseManager.boss().getLeggingsItem(name)));
                    ItemMeta leggingsmeta = leggings.getItemMeta();
                    for(String a : databaseManager.boss().getLeggingsEnchant(name)) {
                        String[] split = a.split(":");
                        NamespacedKey key = new NamespacedKey("minecraft", split[0]);
                        leggingsmeta.addEnchant(Enchantment.getByKey(key), Integer.parseInt(split[1]), true);
                    }
                    leggings.setItemMeta(leggingsmeta);

                    ItemStack boots = new ItemStack(Material.getMaterial(databaseManager.boss().getBootsItem(name)));
                    ItemMeta bootsmeta = boots.getItemMeta();
                    for(String a : databaseManager.boss().getBootsEnchant(name)) {
                        String[] split = a.split(":");
                        NamespacedKey key = new NamespacedKey("minecraft", split[0]);
                        bootsmeta.addEnchant(Enchantment.getByKey(key), Integer.parseInt(split[1]), true);
                    }
                    boots.setItemMeta(bootsmeta);


                    ItemStack weapon = new ItemStack(Material.getMaterial(databaseManager.boss().getWeaponsItem(name)));
                    ItemMeta weaponmeta = weapon.getItemMeta();
                    for(String a : databaseManager.boss().getWeaponEnchant(name)) {
                        String[] split = a.split(":");
                        NamespacedKey key = new NamespacedKey("minecraft", split[0]);
                        weaponmeta.addEnchant(Enchantment.getByKey(key), Integer.parseInt(split[1]), true);
                    }

                    weapon.setItemMeta(weaponmeta);

                    zombie.getEquipment().setHelmet(helmet);
                    zombie.getEquipment().setChestplate(chestplate);
                    zombie.getEquipment().setLeggings(leggings);
                    zombie.getEquipment().setBoots(boots);
                    zombie.getEquipment().setItemInMainHand(weapon);
                    break;
                case "SKELETON":
                    Skeleton piglin = (Skeleton) location.getWorld().spawnEntity(location, EntityType.SKELETON);
                    piglin.setCustomName(parseHexColors(databaseManager.boss().getDisplay(name)));
                    piglin.setCustomNameVisible(true);
                    String piglin_rstr = generateRandomString();
                    databaseManager.boss().setMGID(name, piglin_rstr);
                    databaseManager.boss().setUUID(name, piglin.getUniqueId().toString());
                    String last_dungeon_p = get().getPlayers().getLastDungeon(player);
                    get().getPlayers().updateLastBoss(player, piglin_rstr);
                    piglin.setMetadata("name", new FixedMetadataValue(get(), piglin_rstr));
                    piglin.setMetadata("dungeon", new FixedMetadataValue(get(), last_dungeon_p));
                    piglin.setMetadata("boss", new FixedMetadataValue(get(), name));
                    piglin.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(databaseManager.boss().getHealth(name));
                    piglin.setHealth(databaseManager.boss().getHealth(name));
                    piglin.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(databaseManager.boss().getAttack(name));
                    piglin.getAttribute(Attribute.GENERIC_ATTACK_KNOCKBACK).setBaseValue(databaseManager.boss().getKnockback(name));

                    ItemStack helmet_p = new ItemStack(Material.getMaterial(databaseManager.boss().getHelmetItem(name)));
                    ItemMeta helmet_pmeta = helmet_p.getItemMeta();
                    for(String a : databaseManager.boss().getHelmetEnchants(name)) {
                        String[] split = a.split(":");
                        NamespacedKey key = new NamespacedKey("minecraft", split[0]);
                        helmet_pmeta.addEnchant(Enchantment.getByKey(key), Integer.parseInt(split[1]), true);
                    }
                    helmet_p.setItemMeta(helmet_pmeta);

                    ItemStack chestplate_p = new ItemStack(Material.getMaterial(databaseManager.boss().getChestplateItem(name)));
                    ItemMeta chestplate_pmeta = chestplate_p.getItemMeta();
                    for(String a : databaseManager.boss().getChestplateEnchants(name)) {
                        String[] split = a.split(":");
                        NamespacedKey key = new NamespacedKey("minecraft", split[0]);
                        chestplate_pmeta.addEnchant(Enchantment.getByKey(key), Integer.parseInt(split[1]), true);
                    }
                    chestplate_p.setItemMeta(chestplate_pmeta);

                    ItemStack leggings_p = new ItemStack(Material.getMaterial(databaseManager.boss().getLeggingsItem(name)));
                    ItemMeta leggings_pmeta = leggings_p.getItemMeta();
                    for(String a : databaseManager.boss().getLeggingsEnchant(name)) {
                        String[] split = a.split(":");
                        NamespacedKey key = new NamespacedKey("minecraft", split[0]);
                        leggings_pmeta.addEnchant(Enchantment.getByKey(key), Integer.parseInt(split[1]), true);
                    }
                    leggings_p.setItemMeta(leggings_pmeta);

                    ItemStack boots_p = new ItemStack(Material.getMaterial(databaseManager.boss().getBootsItem(name)));
                    ItemMeta boots_pmeta = boots_p.getItemMeta();
                    for(String a : databaseManager.boss().getBootsEnchant(name)) {
                        String[] split = a.split(":");
                        NamespacedKey key = new NamespacedKey("minecraft", split[0]);
                        boots_pmeta.addEnchant(Enchantment.getByKey(key), Integer.parseInt(split[1]), true);
                    }
                    boots_p.setItemMeta(boots_pmeta);


                    ItemStack weaponp = new ItemStack(Material.getMaterial(databaseManager.boss().getWeaponsItem(name)));
                    ItemMeta weaponpmeta = weaponp.getItemMeta();
                    for(String a : databaseManager.boss().getWeaponEnchant(name)) {
                        String[] split = a.split(":");
                        NamespacedKey key = new NamespacedKey("minecraft", split[0]);
                        weaponpmeta.addEnchant(Enchantment.getByKey(key), Integer.parseInt(split[1]), true);
                    }

                    weaponp.setItemMeta(weaponpmeta);

                    piglin.getEquipment().setHelmet(helmet_p);
                    piglin.getEquipment().setChestplate(chestplate_p);
                    piglin.getEquipment().setLeggings(leggings_p);
                    piglin.getEquipment().setBoots(boots_p);
                    piglin.getEquipment().setItemInMainHand(weaponp);
                    break;
            }
        }
    }

}
