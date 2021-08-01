package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityTypes

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
class AdyWitch : AdyRaider(EntityTypes.WITCH) {

    init {
        registerMeta(at(11700 to 17, 11500 to 16, 11400 to 15, 11300 to 12), "isDrinkingPotion", false)
    }

    fun isDrinkingPotion(): Boolean {
        return getMetadata("isDrinkingPotion")
    }

    fun setDrinkingPotion(drinkingPotion: Boolean) {
        setMetadata("isDrinkingPotion", drinkingPotion)
    }
}