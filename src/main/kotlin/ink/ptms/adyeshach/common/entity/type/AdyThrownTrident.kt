package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityThrowable
import ink.ptms.adyeshach.common.entity.EntityTypes

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
class AdyThrownTrident : AdyEntity(EntityTypes.THROWN_TRIDENT), EntityThrowable {

    init {
        registerMeta(at(11400 to 10, 11300 to 10), "loyaltyLevel", 0)
        registerMeta(at(11500 to 11), "hasEnchantmentGlint", false)
    }

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