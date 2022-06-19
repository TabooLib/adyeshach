package ink.ptms.adyeshach.common.entity.type

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
interface AdyWitch : AdyRaider {

    fun isDrinkingPotion(): Boolean {
        return getMetadata("isDrinkingPotion")
    }

    fun setDrinkingPotion(drinkingPotion: Boolean) {
        setMetadata("isDrinkingPotion", drinkingPotion)
    }
}