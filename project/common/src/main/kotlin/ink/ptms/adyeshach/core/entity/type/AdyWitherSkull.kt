package ink.ptms.adyeshach.core.entity.type

import ink.ptms.adyeshach.core.entity.EntityFireball

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
interface AdyWitherSkull : AdyEntity, EntityFireball {

    fun isInvulnerable(): Boolean {
        return getMetadata("invulnerable")
    }

    fun setInvulnerable(invulnerable: Boolean) {
        setMetadata("invulnerable", invulnerable)
    }
}