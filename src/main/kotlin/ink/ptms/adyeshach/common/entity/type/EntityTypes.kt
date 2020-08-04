package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityBase
import ink.ptms.adyeshach.common.entity.type.impl.*
import ink.ptms.adyeshach.api.nms.NMS
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
            0,
            EntitySize(2.0, 0.5),
            AdyAreaEffectCloud::class.java
    ),

    ARMOR_STAND(
            EntityType.ARMOR_STAND,
            0,
            EntitySize(0.5, 1.975),
            AdyAreaEffectCloud::class.java
    ),

    ARROW(
            EntityType.ARROW,
            0,
            EntitySize(0.5, 0.5),
            AdyArrow::class.java
    ),

    BAT(
            EntityType.BAT,
            0,
            EntitySize(0.5, 0.9),
            AdyBat::class.java
    ),

    BEE(
            EntityType.BEE,
            4,
            EntitySize(0.7, 0.6),
            AdyBee::class.java
    ),

    BLAZE(
            EntityType.BLAZE,
            0,
            EntitySize(0.6, 1.8),
            AdyBlaze::class.java
    ),

    BOAT(
            EntityType.BOAT,
            6,
            EntitySize(1.375, 0.5625),
            AdyBoat::class.java
    ),

    CAT(
            EntityType.CAT,
            0,
            EntitySize(0.6, 0.7),
            AdyCat::class.java
    ),

    CHICKEN(
            EntityType.CHICKEN,
            0,
            EntitySize(0.4, 0.7),
            AdyChicken::class.java
    ),

    CAVE_SPIDER(
            EntityType.CAVE_SPIDER,
            8,
            EntitySize(0.7, 0.5),
            AdyCaveSpider::class.java
    ),

    COD(
            EntityType.COD,
            0,
            EntitySize(0.5, 0.3),
            AdyCod::class.java
    ),

    COW(
            EntityType.COW,
            0,
            EntitySize(0.9, 1.4),
            AdyCow::class.java
    ),

    CREEPER(
            EntityType.CREEPER,
            0,
            EntitySize(0.6, 1.7),
            AdyCreeper::class.java
    ),

    DOLPHIN(
            EntityType.DOLPHIN,
            0,
            EntitySize(0.9, 0.6),
            AdyDolphin::class.java
    ),

    DONKEY(
            EntityType.DONKEY,
            0,
            EntitySize(1.5, 1.39648),
            AdyDonkey::class.java
    ),

    DRAGON_FIREBALL(
            EntityType.DRAGON_FIREBALL,
            0,
            EntitySize(1.0, 1.0),
            AdyDragonFireball::class.java
    ),

    PLAYER(
            EntityType.PLAYER,
            0,
            EntitySize(1.8, 0.6),
            AdyHuman::class.java
    ),

    VILLAGER(
            EntityType.VILLAGER,
            0,
            EntitySize(1.95, 0.6),
            AdyVillager::class.java
    );

    fun getEntityTypeNMS(): Any {
        return NMS.INSTANCE.getEntityTypeNMS(this)
    }
}