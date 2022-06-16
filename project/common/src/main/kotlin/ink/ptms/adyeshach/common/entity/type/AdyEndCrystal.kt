package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.bukkit.data.EmptyVector
import org.bukkit.util.Vector

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
abstract class AdyEndCrystal : AdyEntity() {

    open fun setBeamTarget(position: Vector?) {
        setMetadata("beamTarget", position ?: EmptyVector())
    }

    open fun getBeamTarget(): Vector? {
        return getMetadata<Vector>("beamTarget").takeUnless { it is EmptyVector }
    }

    open fun isShowBottom(): Boolean {
        return getMetadata("showBottom")
    }

    open fun setShowBottom(showBottom: Boolean) {
        setMetadata("showBottom", showBottom)
    }
}