package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityTypes
import taboolib.module.nms.MinecraftVersion

/**
 * @author sky
 * @since 2020-08-04 18:28
 */
abstract class AdyEntityAgeable(entityTypes: EntityTypes) : AdyMob(entityTypes) {

    init {
        /**
         * 1.15 -> 15
         * 1.14 -> 14
         * 1.10 -> 12
         * 1.9 -> 11
         */
//        natural(at(11700 to 16, 11500 to 15, 11400 to 14, 11100 to 12, 10900 to 11), "isBaby", false)
    }

    open fun setBaby(value: Boolean) {
        if (MinecraftVersion.major == 0) {
            //Negative = Child
            setMetadata("isBaby", (if (value) -1 else 0).toByte())
        } else {
            setMetadata("isBaby", value)
        }
    }

    open fun isBaby(): Boolean {
        return getMetadata("isBaby")
    }
}