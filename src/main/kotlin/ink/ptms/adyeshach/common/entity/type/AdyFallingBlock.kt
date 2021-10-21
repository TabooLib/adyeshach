package ink.ptms.adyeshach.common.entity.type

import com.google.gson.annotations.Expose
import ink.ptms.adyeshach.api.AdyeshachAPI
import ink.ptms.adyeshach.api.nms.NMS
import ink.ptms.adyeshach.common.entity.ClientEntity
import ink.ptms.adyeshach.common.entity.EntityTypes
import org.bukkit.Material
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
class AdyFallingBlock : AdyEntity(EntityTypes.FALLING_BLOCK) {

    @Expose
    var material = Material.STONE
        private set

    @Expose
    var data = 0.toByte()
        private set

    override fun visible(viewer: Player, visible: Boolean): Boolean {
        return if (visible) {
            spawn(viewer) {
                val clientId = UUID.randomUUID()
                AdyeshachAPI.clientEntityMap.computeIfAbsent(viewer.name) { ConcurrentHashMap() }[index] = ClientEntity(this, clientId)
                NMS.INSTANCE.spawnEntityFallingBlock(viewer, index, clientId, position.toLocation(), material, data)
            }
        } else {
            destroy(viewer) {
                NMS.INSTANCE.destroyEntity(viewer, index)
                AdyeshachAPI.clientEntityMap[viewer.name]?.remove(index)
            }
        }
    }

    fun setMaterial(material: Material, data: Byte) {
        this.material = material
        this.data = data
        respawn()
    }

    fun setMaterial(material: Material) {
        this.material = material
        respawn()
    }

    fun setData(data: Byte) {
        this.data = data
        respawn()
    }
}