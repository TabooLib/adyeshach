package ink.ptms.adyeshach.impl.entity.handler

import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.entity.EntityInstance
import ink.ptms.adyeshach.core.entity.Rideable
import ink.ptms.adyeshach.core.entity.StandardTags
import ink.ptms.adyeshach.core.event.AdyeshachEntityVehicleEnterEvent
import ink.ptms.adyeshach.core.event.AdyeshachEntityVehicleLeaveEvent
import ink.ptms.adyeshach.core.util.errorBy
import ink.ptms.adyeshach.impl.entity.DefaultEntityInstance
import org.bukkit.entity.Player

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.entity.handler.PassengerHandler
 *
 * 负责实体的骑乘相关逻辑
 */
class PassengerHandler(private val self: DefaultEntityInstance) : Rideable {

    override fun isVehicle(): Boolean {
        return getPassengers().isNotEmpty()
    }

    override fun hasVehicle(): Boolean {
        return getVehicle() != null
    }

    override fun getVehicle(): EntityInstance? {
        return self.manager?.getEntity {
            it as DefaultEntityInstance
            it.passengers.contains(self.uniqueId)
        }
    }

    override fun getVehicleCache(): EntityInstance? {
        return self.cacheVehicleEntity
    }

    override fun hasPassengers(): Boolean {
        return self.passengers.isNotEmpty()
    }

    override fun getPassengers(): List<EntityInstance> {
        return self.passengers.mapNotNull { self.manager?.getEntityByUniqueId(it) }
    }

    override fun addPassenger(vararg entity: EntityInstance) {
        // 单位管理器必须有效
        if (self.manager == null || entity.any { it.manager == null }) {
            errorBy("error-entity-manager-is-null")
        }
        // 单位管理器必须相同
        if (entity.any { it.manager != self.manager }) {
            errorBy("error-entity-manager-not-match")
        }
        
        entity.filter { it != self }.forEach { target ->
            target as DefaultEntityInstance
            // 避免循环骑乘
            target.removePassenger(self)
            // 从当前载具中离开
            target.getVehicle()?.removePassenger(target)
            // 事件
            if (AdyeshachEntityVehicleEnterEvent(target, self).call()) {
                self.passengers.add(target.uniqueId)
                // 标记状态
                target.cacheVehicleEntity = self
                target.setPersistentTag(StandardTags.IS_IN_VEHICLE, "true")
            }
        }
        refreshPassenger()
    }

    override fun removePassenger(vararg entity: EntityInstance) {
        // 单位管理器必须有效
        if (self.manager == null || entity.any { it.manager == null }) {
            errorBy("error-entity-manager-is-null")
        }
        // 单位管理器必须相同
        if (entity.any { it.manager != self.manager }) {
            errorBy("error-entity-manager-not-match")
        }
        
        entity.filter { it != self }.forEach { target ->
            target as DefaultEntityInstance
            // 进行二次判断是否为乘客
            if (self.passengers.contains(target.uniqueId)) {
                // 事件
                if (AdyeshachEntityVehicleLeaveEvent(target, self).call()) {
                    self.passengers.remove(target.uniqueId)
                    // 移除状态
                    target.cacheVehicleEntity = null
                    target.removePersistentTag(StandardTags.IS_IN_VEHICLE)
                    // 校准位置
                    self.manager?.getEntityByUniqueId(target.uniqueId)?.refreshPosition()
                }
            }
        }
        refreshPassenger()
    }

    override fun removePassenger(vararg id: String) {
        removePassenger(*getPassengers().filter { it.id in id }.toTypedArray())
    }

    override fun clearPassengers() {
        removePassenger(*getPassengers().toTypedArray())
    }

    override fun refreshPassenger(viewer: Player) {
        // 刷新自己
        Adyeshach.api().getMinecraftAPI().getEntityOperator().updatePassengers(
            viewer, 
            self.index, 
            *getPassengers().map { e -> e.index }.toIntArray()
        )
        // 刷新坐骑
        getVehicle()?.refreshPassenger(viewer)
    }

    override fun refreshPassenger() {
        self.forViewers { refreshPassenger(it) }
    }

    override fun verifyPassenger() {
        val validPassengers = getPassengers()
        self.passengers.clear()
        self.passengers += validPassengers.map { it.uniqueId }
        self.cacheVehicleEntity = getVehicle()
    }
}
