package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityBase
import ink.ptms.adyeshach.common.entity.type.impl.*
import ink.ptms.adyeshach.nms.NMS
import org.bukkit.entity.EntityType

/**
 * @Author sky
 * @Since 2020-08-04 12:53
 */
enum class EntityTypes(
        val bukkitType: EntityType,
        val bukkitId: Int,
        val entitySize: EntitySize,
        val entityBase: Class<out EntityBase>
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

    BLAZE(
            EntityType.BLAZE,
            0,
            EntitySize(0.6, 1.8),
            AdyBat::class.java
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
            AdyHumanLike::class.java
    );

    fun getEntityTypeNMS(): Any {
        return NMS.INSTANCE.getEntityTypeNMS(this)
    }
}