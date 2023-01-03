package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.bukkit.BukkitDirection
import ink.ptms.adyeshach.common.bukkit.BukkitPaintings
import ink.ptms.adyeshach.common.entity.EntityTypes
import ink.ptms.adyeshach.core.entity.type.AdyPainting
import ink.ptms.adyeshach.core.util.getEnumOrNull
import org.bukkit.entity.Player
import java.util.*

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
@Deprecated("Outdated but usable")
class AdyPainting(v2: ink.ptms.adyeshach.core.entity.EntityInstance):  AdyEntity(EntityTypes.PAINTING, v2) {

    override fun visible(viewer: Player, visible: Boolean): Boolean {
        return v2.visible(viewer, visible)
    }

    fun setDirection(direction: BukkitDirection) {
        v2 as AdyPainting
        v2.setDirection(direction.v2())
    }

    fun getDirection(): BukkitDirection {
        v2 as AdyPainting
        return BukkitDirection.values()[v2.getDirection().ordinal]
    }

    fun setPainting(painting: BukkitPaintings) {
        v2 as AdyPainting
        v2.setPainting(painting.v2())
    }

    fun getPainting(): BukkitPaintings {
        v2 as AdyPainting
        return BukkitPaintings::class.java.getEnumOrNull(v2.getPainting().name) ?: BukkitPaintings.KEBAB
    }
}