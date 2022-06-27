package ink.ptms.adyeshach.impl.nms

import ink.ptms.adyeshach.common.api.MinecraftEntityOperator
import ink.ptms.adyeshach.common.api.MinecraftMeta
import ink.ptms.adyeshach.common.bukkit.BukkitAnimation
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.nms.DefaultMinecraftEntityOperator
 *
 * @author 坏黑
 * @since 2022/6/28 00:08
 */
class DefaultMinecraftEntityOperator : MinecraftEntityOperator {

    override fun destroyEntity(player: Player, entityId: Int) {
        TODO("Not yet implemented")
    }

    override fun teleportEntity(player: Player, entityId: Int, location: Location) {
        TODO("Not yet implemented")
    }

    override fun relMoveEntity(player: Player, entityId: Int, x: Double, y: Double, z: Double) {
        TODO("Not yet implemented")
    }

    override fun updateEntityVelocity(player: Player, entityId: Int, vector: Vector) {
        TODO("Not yet implemented")
    }

    override fun updateHeadRotation(player: Player, entityId: Int, yaw: Float, pitch: Float) {
        TODO("Not yet implemented")
    }

    override fun updateEquipment(player: Player, entityId: Int, slot: EquipmentSlot, itemStack: ItemStack) {
        TODO("Not yet implemented")
    }

    override fun updateEquipment(player: Player, entityId: Int, equipment: Map<EquipmentSlot, ItemStack>) {
        TODO("Not yet implemented")
    }

    override fun updatePassengers(player: Player, entityId: Int, vararg passengers: Int) {
        TODO("Not yet implemented")
    }

    override fun updateEntityMetadata(player: Player, entityId: Int, vararg metadata: MinecraftMeta) {
        TODO("Not yet implemented")
    }

    override fun updateEntityAnimation(player: Player, entityId: Int, animation: BukkitAnimation) {
        TODO("Not yet implemented")
    }

    override fun updateEntityAttach(player: Player, attached: Int, holding: Int) {
        TODO("Not yet implemented")
    }

    override fun updatePlayerSleeping(player: Player, entityId: Int, location: Location) {
        TODO("Not yet implemented")
    }
}