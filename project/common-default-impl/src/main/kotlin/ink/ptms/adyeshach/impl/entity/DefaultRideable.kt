package ink.ptms.adyeshach.impl.entity

import ink.ptms.adyeshach.api.event.AdyeshachEntityVehicleEnterEvent
import ink.ptms.adyeshach.common.api.Adyeshach
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
@Suppress("DuplicatedCode")
interface DefaultRideable : Rideable {

    override fun isVehicle(): Boolean {
        return getPassengers().isNotEmpty()
    }

    override fun hasVehicle(): Boolean {
        return getVehicle() != null
    }

    override fun getVehicle(): EntityInstance? {
        this as EntityInstance
        return manager?.getEntity { it.getPassengers().any { p -> p.uniqueId == uniqueId } }
    }

    override fun getPassengers(): List<EntityInstance> {
        this as DefaultEntityInstance
        return passengers.mapNotNull { manager?.getEntityByUniqueId(it) }
    }

    override fun addPassenger(vararg entity: EntityInstance) {
        this as DefaultEntityInstance
        if (manager == null || entity.any { it.manager == null }) {
            error("Entity Manager must not be null")
        }
        if (entity.any { it.manager != manager }) {
            error("Entity Manager not match")
        }
        entity.forEach {
            // 避免循环骑乘
            it.removePassenger(this)
            // 事件
            if (AdyeshachEntityVehicleEnterEvent(it, this).call()) {
                passengers.add(it.uniqueId)
            }
        }
        refreshPassenger()
    }

    override fun removePassenger(vararg entity: EntityInstance) {
        this as DefaultEntityInstance
        if (manager == null || entity.any { it.manager == null }) {
            error("Entity Manager must not be null")
        }
        if (entity.any { it.manager != manager }) {
            error("Entity Manager not match")
        }
        entity.forEach {
            // 事件
            if (AdyeshachEntityVehicleEnterEvent(it, this).call()) {
                passengers.remove(it.uniqueId)
                // 将实体传送到正确的位置
                val en = manager?.getEntityByUniqueId(it.uniqueId)
                en?.teleport(en.getLocation())
            }
        }
        refreshPassenger()
    }

    override fun removePassenger(vararg id: String) {
        this as DefaultEntityInstance
        removePassenger(*getPassengers().filter { it.id in id }.toTypedArray())
    }

    override fun clearPassengers() {
        this as DefaultEntityInstance
        removePassenger(*getPassengers().toTypedArray())
    }

    override fun refreshPassenger(viewer: Player) {
        this as DefaultEntityInstance
        // 刷新自己
        Adyeshach.api().getMinecraftAPI().getEntityOperator().updatePassengers(viewer, index, *getPassengers().map { e -> e.index }.toIntArray())
        // 刷新坐骑
        getVehicle()?.refreshPassenger(viewer)
    }

    override fun refreshPassenger() {
        this as DefaultEntityInstance
        forViewers { refreshPassenger(it) }
    }
}