package ink.ptms.adyeshach.compat.modelengine4

import ink.ptms.adyeshach.compat.modelengine4.DefaultModelEngine.Companion.isModelEngineHooked
import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.entity.EntityInstance
import ink.ptms.adyeshach.core.entity.ModelEngine
import ink.ptms.adyeshach.core.event.AdyeshachEntityDamageEvent
import ink.ptms.adyeshach.core.event.AdyeshachEntityInteractEvent
import ink.ptms.adyeshach.core.util.safeDistance
import ink.ptms.adyeshach.impl.util.RayTrace
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.util.Vector
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submit
import taboolib.module.navigation.BoundingBox
import taboolib.platform.util.isMainhand

object ModelEngineEvents {

    @Awake(LifeCycle.ENABLE)
    fun init() {
        if (isModelEngineHooked) {
            // 实体生成，若未生成 ModelEngine 模型则发送原版数据包
            // 这可能会导致 getEntityFromClientUniqueId 方法无法获取
            Adyeshach.api().getEventBus().prepareSpawn { e -> !(e.entity as ModelEngine).showModelEngine(e.viewer) }
            // 实体销毁
            Adyeshach.api().getEventBus().prepareDestroy { e -> !(e.entity as ModelEngine).hideModelEngine(e.viewer) }
            // 名称变动
            Adyeshach.api().getEventBus().prepareMetaUpdate { e ->
                val entity = e.entity as? ModelEngine ?: return@prepareMetaUpdate true
                if (e.key == "customName" || e.key == "isCustomNameVisible") {
                    submit(delay = 1) { entity.updateModelEngineNameTag() }
                }
                true
            }
            // 移动状态变动
            Adyeshach.api().getEventBus().prepareMove { e ->
                val entity = e.entity as? ModelEngine ?: return@prepareMove
                if (entity.modelEngineUniqueId != null) {
                    // ModelEngineAPI.getModeledEntity(entity.modelEngineUniqueId)?.state = if (e.isMoving) ModelState.WALK else ModelState.IDLE
                }
            }
        }
    }

    @SubscribeEvent
    private fun onInteract(e: PlayerInteractEvent) {
        if (isModelEngineHooked && e.action != Action.PHYSICAL) {
            val entities = ArrayList<Pair<EntityInstance, BoundingBox>>()
            Adyeshach.api().getEntityFinder().getEntities(e.player) { it.getLocation().safeDistance(e.player.location) <= 5 }.forEach {
                if (it !is ModelEngine) {
                    return@forEach
                }
                if (it.modelEngineUniqueId != null) {
//                    val modeledEntity = ModelEngineAPI.getModeledEntity(it.modelEngineUniqueId) ?: return@forEach
//                    val blueprint = modeledEntity.getModel(it.modelEngineName).blueprint ?: return@forEach
//                    val boundingBoxHeight = blueprint.mainHitbox.height
//                    val boundingBoxWidth = blueprint.mainHitbox.width / 2
//                    val location = it.getLocation()
//                    entities += it to BoundingBox(
//                        location.x - boundingBoxWidth,
//                        location.y,
//                        location.z - boundingBoxWidth,
//                        location.x + boundingBoxWidth,
//                        location.y + boundingBoxHeight,
//                        location.z + boundingBoxWidth,
//                    )
                }
            }
            RayTrace(e.player).traces(5.0, 0.2).forEach { vec ->
                entities.filter { it.second.contains(vec) && it.first.isVisibleViewer(e.player) }.forEach {
                    val result = if (e.action == Action.LEFT_CLICK_AIR || e.action == Action.LEFT_CLICK_BLOCK) {
                        AdyeshachEntityDamageEvent(it.first, e.player).call()
                    } else {
                        AdyeshachEntityInteractEvent(it.first, e.player, e.isMainhand(), Vector(vec.x, vec.y, vec.z)).call()
                    }
                    if (result) {
                        return
                    }
                }
            }
        }
    }
}