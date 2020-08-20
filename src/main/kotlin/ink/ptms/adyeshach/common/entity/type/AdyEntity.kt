package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.api.nms.NMS
import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.EntityTypes
import net.minecraft.server.v1_16_R1.EntityFireball
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.*

/**
 * @Author sky
 * @Since 2020-08-04 18:28
 */
open class AdyEntity(entityTypes: EntityTypes) : EntityInstance(entityTypes) {

    override fun visible(viewer: Player, visible: Boolean) {
        if (visible) {
            spawn(viewer) {
                NMS.INSTANCE.spawnEntity(viewer, entityType.getEntityTypeNMS(), index, UUID.randomUUID(), position.toLocation().run {
                    if (this is EntityFireball) {
                        yaw = 0f
                        pitch = 0f
                    }
                    this
                })
            }
        } else {
            destroy(viewer) {
                NMS.INSTANCE.destroyEntity(viewer, index)
            }
        }
    }

    fun teleport(world: String, x: Double, y: Double, z: Double) {
        teleport(Location(Bukkit.getWorld(world), x, y, z))
    }

    fun teleport(world: String, x: Double, y: Double, z: Double, yaw: Double, pitch: Double) {
        teleport(Location(Bukkit.getWorld(world), x, y, z, yaw.toFloat(), pitch.toFloat()))
    }
}