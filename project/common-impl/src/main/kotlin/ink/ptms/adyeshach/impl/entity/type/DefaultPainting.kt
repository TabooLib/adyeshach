package ink.ptms.adyeshach.impl.entity.type

import com.google.gson.annotations.Expose
import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.bukkit.BukkitDirection
import ink.ptms.adyeshach.core.bukkit.BukkitPaintings
import ink.ptms.adyeshach.core.entity.EntityTypes
import ink.ptms.adyeshach.core.entity.type.AdyPainting
import ink.ptms.adyeshach.core.entity.type.minecraftVersion
import ink.ptms.adyeshach.core.util.getEnum
import ink.ptms.adyeshach.impl.util.ifTrue
import org.bukkit.Art
import org.bukkit.entity.Player

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.entity.type.DefaultPainting
 *
 * @author 坏黑
 * @since 2022/6/29 19:10
 */
abstract class DefaultPainting(entityTypes: EntityTypes) : DefaultEntity(entityTypes), AdyPainting {

    @Expose
    private var painting = BukkitPaintings.KEBAB

    @Expose
    private var direction = BukkitDirection.NORTH

    override fun visible(viewer: Player, visible: Boolean): Boolean {
        val api = Adyeshach.api().getMinecraftAPI()
        return if (visible) {
            prepareSpawn(viewer) {
                viewPlayers.visible += viewer.name
                registerClientEntity(viewer)
                // 添加到可见实体索引
                updateVisibleEntityIndex(viewer, true)
                api.getEntitySpawner().spawnEntityPainting(viewer, index, normalizeUniqueId, position.toLocation(), direction, painting)
            }
        } else {
            prepareDestroy(viewer) {
                viewPlayers.visible -= viewer.name
                // 从可见实体索引中移除
                updateVisibleEntityIndex(viewer, false)
                // 销毁实体
                api.getEntityOperator().destroyEntity(viewer, index)
                // 移除客户端对应表
                unregisterClientEntity(viewer)
            }
        }
    }

    override fun setDirection(direction: BukkitDirection) {
        this.direction = direction
        respawn()
    }

    override fun getDirection(): BukkitDirection {
        return direction
    }

    override fun setPainting(painting: BukkitPaintings) {
        if (minecraftVersion >= 11900) {
            setMetadata("paintingVariant", Art.valueOf(painting.name))
        } else {
            this.painting = painting
            respawn()
        }
    }

    override fun getPainting(): BukkitPaintings {
        return if (minecraftVersion >= 11900) {
            BukkitPaintings.valueOf(getMetadata<Art>("paintingVariant").name)
        } else {
            painting
        }
    }

    override fun setCustomMeta(key: String, value: String?): Boolean {
        super.setCustomMeta(key, value).ifTrue { return true }
        return when (key) {
            "painting" -> {
                setPainting(if (value != null) BukkitPaintings::class.java.getEnum(value) else BukkitPaintings.KEBAB)
                true
            }
            "direction" -> {
                setDirection(if (value != null) BukkitDirection::class.java.getEnum(value) else BukkitDirection.NORTH)
                true
            }
            else -> false
        }
    }
}