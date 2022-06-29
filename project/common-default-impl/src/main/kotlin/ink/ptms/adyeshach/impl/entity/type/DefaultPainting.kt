package ink.ptms.adyeshach.impl.entity.type

import com.google.gson.annotations.Expose
import ink.ptms.adyeshach.common.api.Adyeshach
import ink.ptms.adyeshach.common.bukkit.BukkitDirection
import ink.ptms.adyeshach.common.bukkit.BukkitPaintings
import ink.ptms.adyeshach.common.entity.EntityTypes
import ink.ptms.adyeshach.common.entity.type.AdyPainting
import ink.ptms.adyeshach.common.entity.type.minecraftVersion
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
        if (MinecraftVersion.majorLegacy < 11900) {
            return super.visible(viewer, visible)
        }
        // 低版本使用独立的 Painting 包生成实体
        val api = Adyeshach.api().getMinecraftAPI()
        return if (visible) {
            spawn(viewer) {
                registerClientEntity(viewer)
                api.getEntitySpawner().spawnEntityPainting(viewer, index, normalizeUniqueId, position.toLocation(), direction, painting)
            }
        } else {
            super.visible(viewer, false)
        }
    }

    @Deprecated("1.19 以上不支持")
    override fun setDirection(direction: BukkitDirection) {
        ink.ptms.adyeshach.common.entity.type.assert(minecraftVersion >= 11900, "setDirection")
        this.direction = direction
        respawn()
    }

    @Deprecated("1.19 以上不支持")
    override fun getDirection(): BukkitDirection {
        ink.ptms.adyeshach.common.entity.type.assert(minecraftVersion >= 11900, "getDirection")
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
}