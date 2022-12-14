package ink.ptms.adyeshach.impl.entity.type

import com.google.gson.annotations.Expose
import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.entity.EntityTypes
import ink.ptms.adyeshach.core.entity.type.AdyFallingBlock
import org.bukkit.Material
import org.bukkit.entity.Player

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.entity.type.DefaultFallingBlock
 *
 * @author 坏黑
 * @since 2022/6/29 19:04
 */
abstract class DefaultFallingBlock(entityTypes: EntityTypes) : DefaultEntity(entityTypes), AdyFallingBlock {

    @Expose
    private var material = Material.STONE

    @Expose
    private var data = 0.toByte()

    override fun setMaterial(material: Material, data: Byte) {
        this.material = material
        this.data = data
        respawn()
    }

    override fun setMaterial(material: Material) {
        this.material = material
        respawn()
    }

    override fun getMaterial(): Material {
        return material
    }

    override fun setData(data: Byte) {
        this.data = data
        respawn()
    }

    override fun getData(): Byte {
        return data
    }

    override fun visible(viewer: Player, visible: Boolean): Boolean {
        return if (visible) {
            prepareSpawn(viewer) {
                registerClientEntity(viewer)
                Adyeshach.api().getMinecraftAPI().getEntitySpawner().spawnEntityFallingBlock(viewer, index, normalizeUniqueId, position.toLocation(), material, data)
            }
        } else {
            super.visible(viewer, false)
        }
    }
}