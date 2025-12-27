package ink.ptms.adyeshach.compat.modelengine4

import com.ticxo.modelengine.api.ModelEngineAPI
import com.ticxo.modelengine.api.events.AnimationEndEvent
import com.ticxo.modelengine.api.nms.entity.HitboxEntity
import com.ticxo.modelengine.core.animation.handler.PriorityHandler
import com.ticxo.modelengine.core.animation.handler.StateMachineHandler
import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.entity.EntityInstance
import ink.ptms.adyeshach.core.entity.ModelEngine
import ink.ptms.adyeshach.core.entity.ModelEngineOptions
import org.bukkit.entity.Entity
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.warning

internal fun ModelEngine.createModel() {
    this as EntityInstance
    // 先销毁原始模型
    destroyModelEngine()
    // 没有观察者，不创建模型
    if (!viewPlayers.hasVisiblePlayer()) return
    // 获取配置
    val options = modelEngineOptions ?: ModelEngineOptions()
    // 创建模型对象
    val activeModel = try {
        ModelEngineAPI.createActiveModel(modelEngineName, null) {
            if (options.useStateMachine) StateMachineHandler(it) else PriorityHandler(it)
        }
    } catch (ex: RuntimeException) {
        // 没找到模型
        warning("Cannot find model: $modelEngineName")
        return
    }
    // 创建代理实体
    val entity = EntityModeled(this as EntityInstance)
    setTag("ModelEngine:EntityModeled", entity)
    entity.syncLocation(getLocation())
    entity.isDetectingPlayers = false
    entity.bodyRotationController.yBodyRot = entity.location.yaw
    // 销毁原版实体
    // despawn()
    // 创建模型
    modelEngineUniqueId = normalizeUniqueId
    ModelEngineAPI.createModeledEntity(entity) { model ->
        model.isBaseEntityVisible = false
        // 设置是否强制显示
        forViewers { t -> entity.setForceViewing(t, true) }
        // 应用配置
        options.apply(activeModel)
        // 添加模型
        model.addModel(activeModel, options.isOverrideHitbox)
    }
    // 更新名称
    updateModelEngineNameTag()
    // 还原动画状态
    restoreAnimationState()
    // 触发创建回调
    this as DefaultModelEngine
    triggerModelCreateCallbacks(normalizeUniqueId)
}

internal fun ModelEngine.getDummy(): EntityModeled? {
    this as EntityInstance
    return getTag("ModelEngine:EntityModeled") as? EntityModeled
}

internal fun Entity.asHitboxEntity(): HitboxEntity? {
    return Adyeshach.api().getMinecraftAPI().getHelper().toMinecraft(this) as? HitboxEntity?
}

@SubscribeEvent
internal fun onEnd(e: AnimationEndEvent) {
    val entity = e.model.getBaseEntityInstance() ?: return
    val callback = entity.getTag("animation_end_callback") as? Runnable ?: return
    callback.run()
}