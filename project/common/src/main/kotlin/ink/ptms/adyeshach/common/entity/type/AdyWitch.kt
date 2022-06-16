package ink.ptms.adyeshach.common.entity.type

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
abstract class AdyWitch : AdyRaider() {

    open fun isDrinkingPotion(): Boolean {
        return getMetadata("isDrinkingPotion")
    }

    open fun setDrinkingPotion(drinkingPotion: Boolean) {
        setMetadata("isDrinkingPotion", drinkingPotion)
    }
}