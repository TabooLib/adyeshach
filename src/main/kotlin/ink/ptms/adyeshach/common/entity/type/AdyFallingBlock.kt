package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.api.nms.NMS
import ink.ptms.adyeshach.common.entity.EntityTypes
import io.izzel.taboolib.internal.gson.annotations.Expose
import org.bukkit.Material
import org.bukkit.entity.Player
import java.util.*

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
class AdyFallingBlock() : AdyEntity(EntityTypes.FALLING_BLOCK) {

    @Expose
    private var material = Material.STONE

    @Expose
    private var data = 0.toByte()

    override fun visible(viewer: Player, visible: Boolean) {
        if (visible) {
            spawn(viewer) {
                NMS.INSTANCE.spawnEntityFallingBlock(viewer, index, UUID.randomUUID(), getLatestLocation(), material, data)
            }
        } else {
            destroy(viewer) {
                NMS.INSTANCE.destroyEntity(viewer, index)
            }
        }
    }

    fun setMaterial(material: Material) {
        this.material = material
        respawn()
    }

    fun getMaterial(material: Material): Material {
        return material
    }

    fun setData(data: Byte) {
        this.data = data
        respawn()
    }

    fun getData(data: Byte): Byte {
        return data
    }
}