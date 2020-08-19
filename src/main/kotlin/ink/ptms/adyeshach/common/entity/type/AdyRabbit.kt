package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityTypes
import ink.ptms.adyeshach.java.BukkitUtils
import org.bukkit.entity.Rabbit

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
class AdyRabbit() : AdyEntityAgeable(EntityTypes.RABBIT) {

    init {
        /*
        1.16,1.15
        16 ->Type
        1.14
        15 ->Type
        1.13,1.12,1.11,1.10
        13 ->Type
        1.9
        12 ->Type
         */
        registerMeta(at(11500 to 16, 11400 to 15, 11000 to 13, 10900 to 12), "type", Rabbit.Type.BLACK.ordinal)
    }

    fun setType(value: Rabbit.Type) {
        setMetadata("type", value.ordinal)
    }

    fun getType(): Rabbit.Type {
        return BukkitUtils.valueRabbitType()[getMetadata("type")]
    }
}