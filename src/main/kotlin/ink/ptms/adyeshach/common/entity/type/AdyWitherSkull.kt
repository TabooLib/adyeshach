package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityFireball
import ink.ptms.adyeshach.common.entity.EntityTypes

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
class AdyWitherSkull : AdyEntity(EntityTypes.WITHER_SKULL), EntityFireball {

    init {
        registerMeta(at(11700 to 8, 11400 to 7, 11000 to 6, 10900 to 5), "invulnerable", false)
    }

    fun isInvulnerable(): Boolean {
        return getMetadata("invulnerable")
    }

    fun setInvulnerable(invulnerable: Boolean) {
        setMetadata("invulnerable", invulnerable)
    }
}