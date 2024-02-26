package ink.ptms.adyeshach.compat.modelengine4

import com.ticxo.modelengine.api.ModelEngineAPI
import com.ticxo.modelengine.api.events.BaseEntityInteractEvent
import ink.ptms.adyeshach.compat.modelengine4.DefaultModelEngine.Companion.isModelEngineHooked
import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.entity.ModelEngine
import ink.ptms.adyeshach.core.event.AdyeshachEntityInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.util.Vector
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.function.registerBukkitListener
import taboolib.common.platform.function.submit

object ModelEngineEvents {

    @Awake(LifeCycle.ENABLE)
    fun init() {
        if (isModelEngineHooked) {
            // 实体生成
            Adyeshach.api().getEventBus().prepareSpawn { e ->
                (e.entity as ModelEngine).showModelEngine(e.viewer)
            }
            // 实体销毁
            Adyeshach.api().getEventBus().prepareDestroy { e ->
                (e.entity as ModelEngine).hideModelEngine(e.viewer)
            }
            // 名称变动
            Adyeshach.api().getEventBus().postMetaUpdate { e ->
                if (e.key == "customName" || e.key == "isCustomNameVisible") {
                    (e.entity as ModelEngine).updateModelEngineNameTag()
                }
            }
            // 位置变动
            Adyeshach.api().getEventBus().postTeleport { e ->
                val modeledEntity = ModelEngineAPI.getModeledEntity(e.entity.normalizeUniqueId) ?: return@postTeleport
                val entityModeled = modeledEntity.base as EntityModeled
                entityModeled.syncLocation(e.location)
            }
            // 交互事件
            registerBukkitListener(BaseEntityInteractEvent::class.java) { e ->
                // 忽略 INTERACT 类型
                if (e.action == BaseEntityInteractEvent.Action.INTERACT) {
                    return@registerBukkitListener
                }
                val entityModeled = e.baseEntity as? EntityModeled ?: return@registerBukkitListener
                val isMainHand = e.slot == EquipmentSlot.HAND
                val clickedPos = e.clickedPosition ?: Vector(0, 0, 0)
                submit { AdyeshachEntityInteractEvent(entityModeled.entity, e.player, isMainHand, clickedPos).call() }
            }
        }
    }
}