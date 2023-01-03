package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityTypes
import org.bukkit.Material
import org.bukkit.entity.Player
import java.util.*

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
@Deprecated("Outdated but usable")
class AdyFallingBlock(v2: ink.ptms.adyeshach.core.entity.EntityInstance):  AdyEntity(EntityTypes.FALLING_BLOCK, v2) {

    val material: Material
        get() {
            v2 as ink.ptms.adyeshach.core.entity.type.AdyFallingBlock
            return v2.getMaterial()
        }

    val data: Byte
        get() {
            v2 as ink.ptms.adyeshach.core.entity.type.AdyFallingBlock
            return v2.getData()
        }

    override fun visible(viewer: Player, visible: Boolean): Boolean {
        return v2.visible(viewer, visible)
    }

    fun setMaterial(material: Material, data: Byte) {
        v2 as ink.ptms.adyeshach.core.entity.type.AdyFallingBlock
        v2.setMaterial(material, data)
    }

    fun setMaterial(material: Material) {
        v2 as ink.ptms.adyeshach.core.entity.type.AdyFallingBlock
        v2.setMaterial(material)
    }

    fun setData(data: Byte) {
        v2 as ink.ptms.adyeshach.core.entity.type.AdyFallingBlock
        v2.setData(data)
    }
}