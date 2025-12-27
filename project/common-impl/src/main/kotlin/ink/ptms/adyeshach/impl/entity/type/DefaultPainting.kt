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
import taboolib.module.nms.MinecraftVersion

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
        // 伴生实体禁止外部直接操作可见性
        if (isCompanion()) return false
        return handleVisibleInternal(viewer, visible)
    }

    override fun handleVisibleInternal(viewer: Player, visible: Boolean): Boolean {
        if (MinecraftVersion.majorLegacy >= 11900) {
            return super.handleVisibleInternal(viewer, visible)
        }
        // 低版本使用独立的 Painting 包生成实体
        val api = Adyeshach.api().getMinecraftAPI()
        return if (visible) {
            prepareSpawn(viewer) {
                api.getEntitySpawner().spawnEntityPainting(viewer, index, normalizeUniqueId, position.toLocation(), direction, painting)
            }
        } else {
            prepareDestroy(viewer) {
                api.getEntityOperator().destroyEntity(viewer, index)
            }
        }
    }

    override fun handleCompanionVisible(viewer: Player, visible: Boolean) {
        handleVisibleInternal(viewer, visible)
    }

    @Deprecated("1.19 以上不支持")
    override fun setDirection(direction: BukkitDirection) {
        ink.ptms.adyeshach.core.entity.type.assert(minecraftVersion >= 11900, "setDirection")
        this.direction = direction
        respawn()
    }

    @Deprecated("1.19 以上不支持")
    override fun getDirection(): BukkitDirection {
        ink.ptms.adyeshach.core.entity.type.assert(minecraftVersion >= 11900, "getDirection")
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