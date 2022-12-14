package ink.ptms.adyeshach.core.entity.type

import org.bukkit.entity.Rabbit

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
interface AdyRabbit : AdyEntityAgeable {

    fun setType(value: Rabbit.Type) {
        setMetadata("type", value.ordinal)
    }

    fun getType(): Rabbit.Type {
        return Rabbit.Type.values()[getMetadata("type")]
    }
}