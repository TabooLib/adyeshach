package ink.ptms.adyeshach.impl.entity.type

import com.google.gson.annotations.Expose
import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.entity.EntityTypes
import ink.ptms.adyeshach.core.entity.type.AdyFallingBlock
import ink.ptms.adyeshach.impl.util.ifTrue
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import taboolib.common.platform.function.submit
import taboolib.library.xseries.parseToMaterial

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.entity.type.DefaultFallingBlock
 *
 * @author 坏黑
 * @since 2022/6/29 19:04
 */
abstract class DefaultFallingBlock(entityTypes: EntityTypes) : DefaultEntity(entityTypes), AdyFallingBlock {

    @Expose
    private var material = Material.DIAMOND_BLOCK

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
                viewPlayers.visible += viewer.name
                // 创建客户端对应表
                registerClientEntity(viewer)
                // 添加到可见实体索引
                updateVisibleEntityIndex(viewer, true)
                // 生成实体
                Adyeshach.api().getMinecraftAPI().getEntitySpawner().spawnEntityFallingBlock(viewer, index, normalizeUniqueId, getLocation(), material, data)
                // 修正向量
                setNoGravity(true)
                sendVelocity(Vector(0, 0, 0))
            }
        } else {
            prepareDestroy(viewer) {
                viewPlayers.visible -= viewer.name
                // 从可见实体索引中移除
                updateVisibleEntityIndex(viewer, false)
                // 销毁实体
                Adyeshach.api().getMinecraftAPI().getEntityOperator().destroyEntity(viewer, index)
                // 移除客户端对应表
                unregisterClientEntity(viewer)
            }
        }
    }

    override fun setCustomMeta(key: String, value: String?): Boolean {
        super.setCustomMeta(key, value).ifTrue { return true }
        return when (key) {
            "material", "block" -> {
                setMaterial(value?.parseToMaterial() ?: Material.STONE)
                true
            }
            "data" -> {
                setData(value?.toByte() ?: 0)
                true
            }
            else -> false
        }
    }
}