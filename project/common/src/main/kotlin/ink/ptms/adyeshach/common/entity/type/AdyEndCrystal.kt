package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.bukkit.data.EmptyVector
import org.bukkit.util.Vector

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
interface AdyEndCrystal : AdyEntity {

    fun setBeamTarget(position: Vector?) {
        setMetadata("beamTarget", position ?: EmptyVector())
    }

    fun getBeamTarget(): Vector? {
        return getMetadata<Vector>("beamTarget").takeUnless { it is EmptyVector }
    }

    fun isShowBottom(): Boolean {
        return getMetadata("showBottom")
    }

    fun setShowBottom(showBottom: Boolean) {
        setMetadata("showBottom", showBottom)
    }
}