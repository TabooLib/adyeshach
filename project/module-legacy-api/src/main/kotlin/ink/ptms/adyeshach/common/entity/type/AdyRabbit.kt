package ink.ptms.adyeshach.common.entity.type


import ink.ptms.adyeshach.common.entity.EntityTypes
import org.bukkit.entity.Rabbit

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
@Deprecated("Outdated but usable")
class AdyRabbit(v2: ink.ptms.adyeshach.core.entity.EntityInstance):  AdyEntityAgeable(EntityTypes.RABBIT, v2) {

    fun setType(value: Rabbit.Type) {
        setMetadata("type", value.ordinal)
    }

    fun getType(): Rabbit.Type {
        return Rabbit.Type.values()[getMetadata("type")]
    }
}