package ink.ptms.adyeshach.common.entity.type.impl

import ink.ptms.adyeshach.common.entity.type.EntityTypes
import ink.ptms.adyeshach.common.position.Position
import ink.ptms.adyeshach.nms.NMS
import io.izzel.taboolib.internal.gson.annotations.Expose
import net.minecraft.server.v1_16_R1.Entity
import org.bukkit.World
import org.bukkit.craftbukkit.v1_16_R1.CraftWorld
import org.bukkit.entity.Player
import java.util.*

/**
 * @Author sky
 * @Since 2020-08-04 15:44
 */
class AdyHuman(owner: Player) : AdyHumanLike(owner, EntityTypes.PLAYER) {

    val uuid = UUID.randomUUID()!!

    override fun spawn(world: World, position: Position) {
        super.spawn(world, position)
        NMS.INSTANCE.addPlayerInfo(owner, uuid)
        NMS.INSTANCE.spawnNamedEntity(owner, EntityTypes.PLAYER.getEntityTypeNMS(), index, uuid, position.toLocation(world))
    }

    override fun destroy() {
        super.destroy()
        hideInTabList()
    }

    fun hideInTabList() {
        NMS.INSTANCE.removePlayerInfo(owner, uuid)
    }
}