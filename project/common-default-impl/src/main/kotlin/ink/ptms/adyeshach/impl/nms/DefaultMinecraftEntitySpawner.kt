package ink.ptms.adyeshach.impl.nms

import ink.ptms.adyeshach.common.api.MinecraftEntitySpawner
import ink.ptms.adyeshach.common.bukkit.BukkitDirection
import ink.ptms.adyeshach.common.bukkit.BukkitPaintings
import ink.ptms.adyeshach.common.entity.EntityTypes
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import java.util.*

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.nms.DefaultMinecraftEntitySpawner
 *
 * @author 坏黑
 * @since 2022/6/28 00:08
 */
class DefaultMinecraftEntitySpawner : MinecraftEntitySpawner {

    override fun spawnEntity(player: Player, entityType: EntityTypes, entityId: Int, uuid: UUID, location: Location) {
        TODO("Not yet implemented")
    }

    override fun spawnEntityLiving(player: Player, entityType: EntityTypes, entityId: Int, uuid: UUID, location: Location) {
        TODO("Not yet implemented")
    }

    override fun spawnNamedEntity(player: Player, entityId: Int, uuid: UUID, location: Location) {
        TODO("Not yet implemented")
    }

    override fun spawnEntityFallingBlock(player: Player, entityId: Int, uuid: UUID, location: Location, material: Material, data: Byte) {
        TODO("Not yet implemented")
    }

    override fun spawnEntityExperienceOrb(player: Player, entityId: Int, location: Location, amount: Int) {
        TODO("Not yet implemented")
    }

    override fun spawnEntityPainting(player: Player, entityId: Int, uuid: UUID, location: Location, direction: BukkitDirection, painting: BukkitPaintings) {
        TODO("Not yet implemented")
    }
}