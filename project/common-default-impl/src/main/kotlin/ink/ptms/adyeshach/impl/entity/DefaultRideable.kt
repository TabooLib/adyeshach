package ink.ptms.adyeshach.impl.entity

import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.Rideable
import org.bukkit.entity.Player

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.entity.DefaultRideable
 *
 * @author 坏黑
 * @since 2022/6/20 00:32
 */
interface DefaultRideable : Rideable {

    override fun isVehicle(): Boolean {
        TODO("Not yet implemented")
    }

    override fun hasVehicle(): Boolean {
        TODO("Not yet implemented")
    }

    override fun getVehicle(): EntityInstance? {
        TODO("Not yet implemented")
    }

    override fun getPassengers(): List<EntityInstance> {
        TODO("Not yet implemented")
    }

    override fun addPassenger(vararg entity: EntityInstance) {
        TODO("Not yet implemented")
    }

    override fun removePassenger(vararg entity: EntityInstance) {
        TODO("Not yet implemented")
    }

    override fun removePassenger(vararg id: String) {
        TODO("Not yet implemented")
    }

    override fun clearPassengers() {
        TODO("Not yet implemented")
    }

    override fun refreshPassenger(viewer: Player, error: Boolean) {
        TODO("Not yet implemented")
    }

    override fun refreshPassenger() {
        TODO("Not yet implemented")
    }
}