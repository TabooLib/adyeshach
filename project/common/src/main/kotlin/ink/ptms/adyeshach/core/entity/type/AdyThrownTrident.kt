package ink.ptms.adyeshach.core.entity.type

import ink.ptms.adyeshach.core.entity.EntityThrowable

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
interface AdyThrownTrident : AdyEntity, EntityThrowable {

    fun setLoyaltyLevel(loyaltyLevel: Int) {
        setMetadata("loyaltyLevel", loyaltyLevel)
    }

    fun getLoyaltyLevel(): Int {
        return getMetadata("loyaltyLevel")
    }

    fun isHasEnchantmentGlint(): Boolean {
        return getMetadata("hasEnchantmentGlint")
    }

    fun setHasEnchantmentGlint(hasEnchantmentGlint: Boolean) {
        setMetadata("hasEnchantmentGlint", hasEnchantmentGlint)
    }
}