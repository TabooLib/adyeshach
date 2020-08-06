package ink.ptms.adyeshach.common.entity.type.impl

import ink.ptms.adyeshach.api.nms.NMS
import ink.ptms.adyeshach.common.bukkit.BukkitDirection
import ink.ptms.adyeshach.common.bukkit.BukkitPaintings
import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.element.EntityPosition
import ink.ptms.adyeshach.common.entity.type.EntityTypes
import io.izzel.taboolib.internal.gson.annotations.Expose
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.*

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
class AdyPainting(owner: Player) : EntityInstance(owner, EntityTypes.PAINTING) {

    @Expose
    private var direction: BukkitDirection = BukkitDirection.NORTH

    @Expose
    private var painting: BukkitPaintings = BukkitPaintings.KEBAB

    fun getDirection(): BukkitDirection {
        return direction
    }

    fun setDirection(direction: BukkitDirection) {
        this.direction = direction
        respawn()
    }

    fun setPainting(painting: BukkitPaintings) {
        this.painting = painting
        respawn()
    }

    fun getPainting(): BukkitPaintings {
        return painting
    }

    override fun spawn(location: Location) {
        this.world = location.world!!.name
        this.position = EntityPosition.fromLocation(location)
        NMS.INSTANCE.spawnEntityPainting(owner, index, UUID.randomUUID(), location, direction, painting)
    }

}