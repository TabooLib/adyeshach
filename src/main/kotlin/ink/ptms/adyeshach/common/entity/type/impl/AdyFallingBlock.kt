package ink.ptms.adyeshach.common.entity.type.impl

import ink.ptms.adyeshach.api.nms.NMS
import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.type.EntityTypes
import io.izzel.taboolib.internal.gson.annotations.Expose
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import java.util.*

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
class AdyFallingBlock(owner: Player) : EntityInstance(owner, EntityTypes.FALLING_BLOCK) {

    @Expose
    private var material = Material.STONE

    @Expose
    private var data = 0.toByte()

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

    @Suppress("DEPRECATION")
    private fun toMaterialData(): Int {
        // TODO
        return material.id + (data.toInt() shl 12)
    }

    override fun spawn(location: Location) {
        super.spawn(location)
        NMS.INSTANCE.spawnEntity(owner, entityType.getEntityTypeNMS(), index, UUID.randomUUID(), location, toMaterialData())
    }

}