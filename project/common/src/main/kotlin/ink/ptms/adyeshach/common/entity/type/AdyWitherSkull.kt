package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityFireball

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
abstract class AdyWitherSkull : AdyEntity(), EntityFireball {

    open fun isInvulnerable(): Boolean {
        return getMetadata("invulnerable")
    }

    open fun setInvulnerable(invulnerable: Boolean) {
        setMetadata("invulnerable", invulnerable)
    }
}