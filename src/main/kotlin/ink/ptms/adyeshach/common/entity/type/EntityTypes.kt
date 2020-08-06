package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.api.nms.NMS
import ink.ptms.adyeshach.common.entity.EntityBase
import ink.ptms.adyeshach.common.entity.type.impl.*
import org.bukkit.entity.EntityType

/**
 * @Author sky
 * @Since 2020-08-04 12:53
 */
enum class EntityTypes(
    val bukkitType: EntityType,
    val bukkitId: Int,
    val entitySize: EntitySize,
    val entityBase: Class<out EntityBase>,
) {

    AREA_EFFECT_CLOUD(
        EntityType.AREA_EFFECT_CLOUD,
        3,
        EntitySize(2.0, 0.5),
        AdyAreaEffectCloud::class.java
    ),

    ARMOR_STAND(
        EntityType.ARMOR_STAND,
        78,
        EntitySize(0.5, 1.975),
        AdyAreaEffectCloud::class.java
    ),

    ARROW(
        EntityType.ARROW,
        60,
        EntitySize(0.5, 0.5),
        AdyArrow::class.java
    ),

    BAT(
        EntityType.BAT,
        65,
        EntitySize(0.5, 0.9),
        AdyBat::class.java
    ),

    BEE(
        EntityType.BEE,
        -1,
        EntitySize(0.7, 0.6),
        AdyBee::class.java
    ),

    BLAZE(
        EntityType.BLAZE,
        61,
        EntitySize(0.6, 1.8),
        AdyBlaze::class.java
    ),

    BOAT(
        EntityType.BOAT,
        1,
        EntitySize(1.375, 0.5625),
        AdyBoat::class.java
    ),

    CAT(
        EntityType.CAT,
        98,
        EntitySize(0.6, 0.7),
        AdyCat::class.java
    ),

    CAVE_SPIDER(
        EntityType.CAVE_SPIDER,
        59,
        EntitySize(0.7, 0.5),
        AdyCaveSpider::class.java
    ),

    CHICKEN(
        EntityType.CHICKEN,
        93,
        EntitySize(0.4, 0.7),
        AdyChicken::class.java
    ),

    COD(
        EntityType.COD,
        -1,
        EntitySize(0.5, 0.3),
        AdyCod::class.java
    ),

    COW(
        EntityType.COW,
        92,
        EntitySize(0.9, 1.4),
        AdyCow::class.java
    ),

    CREEPER(
        EntityType.CREEPER,
        50,
        EntitySize(0.6, 1.7),
        AdyCreeper::class.java
    ),

    DOLPHIN(
        EntityType.DOLPHIN,
        -1,
        EntitySize(0.9, 0.6),
        AdyDolphin::class.java
    ),

    DONKEY(
        EntityType.DONKEY,
        31,
        EntitySize(1.5, 1.39648),
        AdyDonkey::class.java
    ),

    DRAGON_FIREBALL(
        EntityType.DRAGON_FIREBALL,
        93,
        EntitySize(1.0, 1.0),
        AdyDragonFireball::class.java
    ),

    DROWNED(
        EntityType.DROWNED,
        -1,
        EntitySize(0.6, 1.95),
        AdyDragonFireball::class.java
    ),

    ELDER_GUARDIAN(
        EntityType.ELDER_GUARDIAN,
        4,
        EntitySize(1.9975, 1.9975),
        AdyEntityLiving::class.java
    ),

    ENDER_CRYSTAL(
        EntityType.ENDER_CRYSTAL,
        51,
        EntitySize(2.0, 2.0),
        AdyEntityLiving::class.java
    ),

    ENDER_DRAGON(
        EntityType.ENDER_DRAGON,
        63,
        EntitySize(16.0, 8.0),
        AdyEntityLiving::class.java
    ),

    ENDER_MAN(
        EntityType.ENDERMAN,
        58,
        EntitySize(16.0, 8.0),
        AdyEntityLiving::class.java
    ),

    ENDER_MITE(
        EntityType.ENDERMITE,
        67,
        EntitySize(0.4, 0.3),
        AdyEntityLiving::class.java
    ),

    EVOKER(
        EntityType.EVOKER,
        -1,
        EntitySize(0.6, 1.95),
        AdyEntityLiving::class.java
    ),

    EVOKER_FANGS(
        EntityType.EVOKER_FANGS,
        -1,
        EntitySize(0.5, 0.8),
        AdyEntityLiving::class.java
    ),

    // 需要 [Spawn Experience Orb] 包
    EXPERIENCE_ORGB(
        EntityType.EXPERIENCE_ORB,
        -1,
        EntitySize(0.5, 0.5),
        AdyEntity::class.java
    ),

    EYE_OF_ENDER(
        EntityType.ENDER_SIGNAL,
        72,
        EntitySize(0.25, 0.25),
        AdyEntity::class.java
    ),

    FALLING_BLOCK(
        EntityType.FALLING_BLOCK,
        70,
        EntitySize(0.98, 0.98),
        AdyEntity::class.java
    ),

    FIREWORK_ROCKET(
        EntityType.FIREWORK,
        76,
        EntitySize(0.25, 0.25),
        AdyEntity::class.java
    ),

    FOX(
        EntityType.FOX,
        -1,
        EntitySize(0.6, 0.7),
        AdyEntityLiving::class.java
    ),

    GHAST(
        EntityType.GHAST,
        56,
        EntitySize(4.0, 4.0),
        AdyEntityLiving::class.java
    ),

    GIANT(
        EntityType.GIANT,
        53,
        EntitySize(3.6, 12.0),
        AdyEntityLiving::class.java
    ),

    GUARDIAN(
        EntityType.GUARDIAN,
        68,
        EntitySize(0.85, 0.85),
        AdyEntityLiving::class.java
    ),

    HOGLIN(
        EntityType.HOGLIN,
        -1,
        EntitySize(1.39648, 1.4),
        AdyEntityLiving::class.java
    ),

    HORSE(
        EntityType.HORSE,
        100,
        EntitySize(1.39648, 1.6),
        AdyEntityLiving::class.java
    ),


    HUSK(
        EntityType.HUSK,
        23,
        EntitySize(0.6, 1.95),
        AdyEntityLiving::class.java
    ),

    ILLUSIONER(
        EntityType.ILLUSIONER,
        -1,
        EntitySize(0.6, 1.95),
        AdyEntityLiving::class.java
    ),

    IRON_GOLEM(
        EntityType.IRON_GOLEM,
        99,
        EntitySize(1.4, 2.7),
        AdyEntityLiving::class.java
    ),

    ITEM(
        EntityType.DROPPED_ITEM,
        2,
        EntitySize(0.25, 0.25),
        AdyEntityLiving::class.java
    ),

    ITEM_FRAME(
        EntityType.ITEM_FRAME,
        71,
        EntitySize(0.75, 0.75),
        AdyEntityLiving::class.java
    ),

    FIREBALL(
        EntityType.FIREBALL,
        63,
        EntitySize(1.0, 1.0),
        AdyEntityLiving::class.java
    ),

    LEASH_KNOT(
        EntityType.LEASH_HITCH,
        77,
        EntitySize(0.375, 0.5),
        AdyEntityLiving::class.java
    ),

    LLAMA(
        EntityType.LLAMA,
        103,
        EntitySize(0.9, 1.87),
        AdyEntityLiving::class.java
    ),

    LLAMA_SPIT(
        EntityType.LLAMA_SPIT,
        68,
        EntitySize(0.25, 0.25),
        AdyEntityLiving::class.java
    ),

    MAGMA_CUBE(
        EntityType.MAGMA_CUBE,
        62,
        EntitySize(0.51000005, 0.51000005), // * size
        AdyEntityLiving::class.java
    ),

    MINECART(
        EntityType.MINECART,
        10,
        EntitySize(0.98, 0.7),
        AdyEntity::class.java
    ),

    MINECART_CHEST(
        EntityType.MINECART_CHEST,
        10,
        EntitySize(0.98, 0.7),
        AdyEntity::class.java
    ),

    MINECART_COMMAND(
        EntityType.MINECART_COMMAND,
        10,
        EntitySize(0.98, 0.7),
        AdyEntity::class.java
    ),

    MINECART_FURNACE(
        EntityType.MINECART_FURNACE,
        10,
        EntitySize(0.98, 0.7),
        AdyEntity::class.java
    ),

    MINECART_HOPPER(
        EntityType.MINECART_HOPPER,
        10,
        EntitySize(0.98, 0.7),
        AdyEntity::class.java
    ),


    MINECART_MOB_SPAWNER(
        EntityType.MINECART_MOB_SPAWNER,
        10,
        EntitySize(0.98, 0.7),
        AdyEntity::class.java
    ),

    MINECART_TNT(
        EntityType.MINECART_TNT,
        10,
        EntitySize(0.98, 0.7),
        AdyEntity::class.java
    ),

    MULE(
        EntityType.MULE,
        32,
        EntitySize(1.39648, 1.6),
        AdyEntityLiving::class.java
    ),

    MUSHROOM(
        EntityType.MUSHROOM_COW,
        96,
        EntitySize(0.9, 1.4),
        AdyEntityLiving::class.java
    ),

    OCELOT(
        EntityType.OCELOT,
        98,
        EntitySize(0.6, 0.7),
        AdyEntityLiving::class.java
    ),

    // 需要 [Spawn Paintings] 包
    PAINTING(
        EntityType.PAINTING,
        -1,
        EntitySize(0.0625, 0.0625), // varies in types
        AdyEntityLiving::class.java
    ),

    PANDA(
        EntityType.PANDA,
        -1,
        EntitySize(1.3, 1.25),
        AdyEntityLiving::class.java
    ),

    PARROT(
        EntityType.PARROT,
        105,
        EntitySize(0.5, 0.9),
        AdyEntityLiving::class.java
    ),

    PHANTOM(
        EntityType.PHANTOM,
        -1,
        EntitySize(0.9, 0.5),
        AdyEntityLiving::class.java
    ),

    PIG(
        EntityType.PIG,
        90,
        EntitySize(0.9, 0.9),
        AdyEntityLiving::class.java
    ),


    PIGLIN(
        EntityType.PIGLIN,
        -1,
        EntitySize(0.6, 1.95),
        AdyEntityLiving::class.java
    ),

    PIGLIN_BRUTE(
        EntityType.PIGLIN,
        -1,
        EntitySize(0.6, 1.95),
        AdyEntityLiving::class.java
    ),

    PILLAGER(
        EntityType.PILLAGER,
        -1,
        EntitySize(0.6, 1.95),
        AdyEntityLiving::class.java
    ),

    POLAR_BEAR(
        EntityType.POLAR_BEAR,
        102,
        // ver<1.16 -> 1.3,1.4
        EntitySize(1.4, 1.4),
        AdyEntityLiving::class.java
    ),

    PRIMED_TNT(
        EntityType.PRIMED_TNT,
        50,
        EntitySize(0.98, 0.98),
        AdyEntityLiving::class.java
    ),

    PUFFERFISH(
        EntityType.PUFFERFISH,
        -1,
        EntitySize(0.7, 0.7),
        AdyEntityLiving::class.java
    ),

    RABBIT(
        EntityType.RABBIT,
        101,
        EntitySize(0.4, 0.5),
        AdyEntityLiving::class.java
    ),

    RAVAGER(
        EntityType.RAVAGER,
        -1,
        EntitySize(1.95, 2.2),
        AdyEntityLiving::class.java
    ),

    SALMON(
        EntityType.SALMON,
        -1,
        EntitySize(0.7, 0.4),
        AdyFish::class.java
    ),

    SHEEP(
        EntityType.SHEEP,
        91,
        EntitySize(0.9, 1.3),
        AdyEntityAgeable::class.java
    ),

    SHULKER(
        EntityType.SHULKER,
        69,
        // SecondValue: 1.0-2.0 (depending on peek)
        EntitySize(1.0, 2.0),
        AdyEntityLiving::class.java
    ),

    SHULKER_BULLET(
        EntityType.SHULKER_BULLET,
        67,
        EntitySize(0.3125, 0.3125),
        AdyEntityLiving::class.java
    ),

    SILVERFISH(
        EntityType.SILVERFISH,
        60,
        EntitySize(0.4, 0.3),
        AdyEntityLiving::class.java
    ),

    SKELETON(
        EntityType.SKELETON,
        51,
        EntitySize(0.6, 1.99),
        AdyEntityLiving::class.java
    ),

    SKELETON_HORSE(
        EntityType.SKELETON_HORSE,
        28,
        EntitySize(1.396484, 1.6),
        AdyEntityLiving::class.java
    ),

    SLIME(
        EntityType.SLIME,
        55,
        EntitySize(0.51000005, 0.51000005),
        AdyEntityLiving::class.java
    ),

    SMALL_FIREBALL(
        EntityType.SMALL_FIREBALL,
        64,
        EntitySize(0.3125, 0.3125),
        AdyEntityLiving::class.java
    ),

    SNOW_GOLEM(
        EntityType.SNOWMAN,
        97,
        EntitySize(0.7, 1.9),
        AdyEntityLiving::class.java
    ),

    SNOWBALL(
        EntityType.SNOWBALL,
        61,
        EntitySize(0.25, 0.25),
        AdyEntityLiving::class.java
    ),

    SPECTRAL_ARROW(
        EntityType.SPECTRAL_ARROW,
        91,
        EntitySize(0.5, 0.5),
        AdyArrow::class.java
    ),

    SPIDER(
        EntityType.SPIDER,
        52,
        EntitySize(1.4, 0.9),
        AdySpider::class.java
    ),

    SQUID(
        EntityType.SQUID,
        94,
        EntitySize(0.8, 0.8),
        AdyEntityLiving::class.java
    ),

    STRAY(
        EntityType.STRAY,
        6,
        EntitySize(0.6, 1.99),
        AdyEntityLiving::class.java
    ),

    STRIDER(
        EntityType.STRIDER,
        6,
        EntitySize(0.9, 1.7),
        AdyEntityLiving::class.java
    ),

    THROWN_EGG(
        EntityType.EGG,
        62,
        EntitySize(0.25, 0.25),
        AdyEntity::class.java
    ),

    THROWN_ENDER_PEARL(
        EntityType.ENDER_PEARL,
        65,
        EntitySize(0.25, 0.25),
        AdyEntity::class.java
    ),

    THROWN_EXPERIENCE_BOTTLE(
        EntityType.THROWN_EXP_BOTTLE,
        75,
        EntitySize(0.25, 0.25),
        AdyEntity::class.java
    ),

    THROWN_POTION(
        EntityType.SPLASH_POTION,
        73,
        EntitySize(0.25, 0.25),
        AdyEntity::class.java
    ),

    THROWN_TRIDENT(
        EntityType.TRIDENT,
        73,
        EntitySize(0.5, 0.5),
        AdyEntity::class.java
    ),

    TRADER_LLAMA(
        EntityType.TRADER_LLAMA,
        -1,
        EntitySize(0.9, 1.87),
        AdyEntity::class.java
    ),

    TROPICAL_FISH(
        EntityType.TROPICAL_FISH,
        -1,
        EntitySize(0.5, 0.4),
        AdyFish::class.java
    ),

    TURTLE(
        EntityType.TURTLE,
        -1,
        EntitySize(1.2, 0.4),
        AdyEntityAgeable::class.java
    ),

    VEX(
        EntityType.VEX,
        -1,
        EntitySize(0.4, 0.8),
        AdyEntityLiving::class.java
    ),

    VILLAGER(
        EntityType.VILLAGER,
        120,
        EntitySize(1.95, 0.6),
        AdyVillager::class.java
    ),

    VINDICATOR(
        EntityType.VINDICATOR,
        -1,
        EntitySize(0.6, 1.95),
        AdyEntityLiving::class.java
    ),

    WANDERING_TRADER(
        EntityType.WANDERING_TRADER,
        -1,
        EntitySize(0.6, 1.95),
        AdyEntityLiving::class.java
    ),

    WITCH(
        EntityType.WITCH,
        -1,
        EntitySize(0.6, 1.95),
        AdyEntityLiving::class.java
    ),

    WITHER(
        EntityType.WITHER,
        64,
        EntitySize(0.9, 3.5),
        AdyEntityLiving::class.java
    ),

    WITHER_SKELETON(
        EntityType.WITHER_SKELETON,
        5,
        EntitySize(0.7, 2.4),
        AdyEntityLiving::class.java
    ),

    WITHER_SKULL(
        EntityType.WITHER_SKULL,
        66,
        EntitySize(0.3125, 0.3125),
        AdyAreaEffectCloud::class.java
    ),

    WOLF(
        EntityType.WOLF,
        95,
        EntitySize(0.6, 0.85),
        AdyEntityTameable::class.java
    ),

    ZOGLIN(
        EntityType.ZOGLIN,
        -1,
        EntitySize(1.39648, 1.4),
        AdyEntityLiving::class.java
    ),

    ZOMBIE(
        EntityType.ZOMBIE,
        54,
        EntitySize(0.6, 1.95),
        AdyEntityLiving::class.java
    ),

    ZOMBIE_HORSE(
        EntityType.ZOMBIE_HORSE,
        29,
        EntitySize(1.39648, 1.6),
        AdyEntityLiving::class.java
    ),

    ZOMBIE_VILLAGER(
        EntityType.ZOMBIE_VILLAGER,
        27,
        EntitySize(0.6, 1.95),
        AdyEntityLiving::class.java
    ),

    ZOMBIFIED_PIGLIN(
        EntityType.ZOMBIFIED_PIGLIN,
        -1,
        EntitySize(0.6, 1.95),
        AdyEntityLiving::class.java
    ),

    PLAYER(
        EntityType.PLAYER,
        0,
        EntitySize(0.6, 1.8),
        AdyHuman::class.java
    ),

    FISHING_HOOK(
        EntityType.FISHING_HOOK,
        90,
        EntitySize(0.25, 0.25),
        AdyEntity::class.java
    );

    fun getEntityTypeNMS(): Any {
        return NMS.INSTANCE.getEntityTypeNMS(this)
    }

}