package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityThrowable

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
abstract class AdyThrownTrident : AdyEntity(), EntityThrowable {

    open fun setLoyaltyLevel(loyaltyLevel: Int) {
        setMetadata("loyaltyLevel", loyaltyLevel)
    }

    open fun getLoyaltyLevel(): Int {
        return getMetadata("loyaltyLevel")
    }

    open fun isHasEnchantmentGlint(): Boolean {
        return getMetadata("hasEnchantmentGlint")
    }

    open fun setHasEnchantmentGlint(hasEnchantmentGlint: Boolean) {
        setMetadata("hasEnchantmentGlint", hasEnchantmentGlint)
    }
}