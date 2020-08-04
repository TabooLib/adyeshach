package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityBase
import ink.ptms.adyeshach.common.entity.type.impl.AdyHuman
import ink.ptms.adyeshach.common.entity.type.impl.AdyHumanLike
import ink.ptms.adyeshach.nms.NMS
import org.bukkit.entity.EntityType

/**
 * @Author sky
 * @Since 2020-08-04 12:53
 */
enum class EntityTypes(
        val bukkitType: EntityType,
        val entitySize: EntitySize,
        val entityBase: Class<out EntityBase>
) {

    PLAYER(
            EntityType.PLAYER,
            EntitySize(1.8, 0.6),
            AdyHuman::class.java
    ),

    VILLAGER(
            EntityType.VILLAGER,
            EntitySize(1.95, 0.6),
            AdyHumanLike::class.java
    );

    fun getEntityTypeNMS(): Any {
        return NMS.INSTANCE.getEntityTypeNMS(this)
    }
}