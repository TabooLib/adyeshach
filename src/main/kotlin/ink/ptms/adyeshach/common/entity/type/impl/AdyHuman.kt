package ink.ptms.adyeshach.common.entity.type.impl

import ink.ptms.adyeshach.common.entity.type.EntityTypes
import ink.ptms.adyeshach.common.entity.element.EntityPosition
import ink.ptms.adyeshach.nms.NMS
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Player
import java.util.*

/**
 * @Author sky
 * @Since 2020-08-04 15:44
 */
class AdyHuman(owner: Player) : AdyHumanLike(owner, EntityTypes.PLAYER) {

    val uuid = UUID.randomUUID()!!

    override fun spawn(location: Location) {
        super.spawn(location)
        NMS.INSTANCE.addPlayerInfo(owner, uuid)
        NMS.INSTANCE.spawnNamedEntity(owner, EntityTypes.PLAYER.getEntityTypeNMS(), index, uuid, location)
    }

    override fun destroy() {
        super.destroy()
        hideInTabList()
    }

    fun hideInTabList() {
        NMS.INSTANCE.removePlayerInfo(owner, uuid)
    }
}