package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.bukkit.data.VectorNull
import ink.ptms.adyeshach.common.entity.EntityTypes
import org.bukkit.util.Vector

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
@Deprecated("Outdated but usable")
class AdyEndCrystal(v2: ink.ptms.adyeshach.core.entity.EntityInstance):  AdyEntity(EntityTypes.END_CRYSTAL, v2) {

    fun setBeamTarget(position: Vector?) {
        setMetadata("beamTarget", position ?: VectorNull())
    }

    fun getBeamTarget(): Vector? {
        val position = getMetadata<Vector>("beamTarget")
        return if (position is VectorNull) null else position
    }

    fun isShowBottom(): Boolean {
        return getMetadata("showBottom")
    }

    fun setShowBottom(showBottom: Boolean) {
        setMetadata("showBottom", showBottom)
    }
}