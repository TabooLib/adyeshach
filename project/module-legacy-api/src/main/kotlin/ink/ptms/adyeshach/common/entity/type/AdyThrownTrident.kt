package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityThrowable
import ink.ptms.adyeshach.common.entity.EntityTypes

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
@Deprecated("Outdated but usable")
class AdyThrownTrident(v2: ink.ptms.adyeshach.core.entity.EntityInstance):  AdyEntity(EntityTypes.THROWN_TRIDENT, v2), EntityThrowable {

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