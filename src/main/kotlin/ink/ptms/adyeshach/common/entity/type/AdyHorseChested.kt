package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityTypes

/**
 * @Author sky
 * @Since 2020-08-04 18:28
 */
abstract class AdyHorseChested(entityTypes: EntityTypes) : AdyHorseBase(entityTypes) {

    init {
        /**
         * 1.15 -> 18
         * 1.14 -> 17
         * 1.11 -> 15
         */
        registerMeta(at(11700 to 19, 11500 to 18, 11400 to 17, 11100 to 15), "hasChest", false)
    }

    override fun setHasChest(value: Boolean) {
        setMetadata("hasChest", value)
    }

    override fun isHasChest(): Boolean {
        return getMetadata("hasChest")
    }
}