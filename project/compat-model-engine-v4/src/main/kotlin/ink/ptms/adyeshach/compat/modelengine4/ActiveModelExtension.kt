package ink.ptms.adyeshach.compat.modelengine4

import com.ticxo.modelengine.api.ModelEngineAPI
import com.ticxo.modelengine.api.animation.BlueprintAnimation
import com.ticxo.modelengine.api.animation.handler.IStateMachineHandler
import com.ticxo.modelengine.api.model.ActiveModel
import com.ticxo.modelengine.api.model.ModeledEntity
import com.ticxo.modelengine.api.nms.entity.HitboxEntity
import com.ticxo.modelengine.core.animation.handler.PriorityHandler
import com.ticxo.modelengine.core.animation.handler.StateMachineHandler
import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.entity.EntityInstance
import ink.ptms.adyeshach.core.entity.ModelEngine
import ink.ptms.adyeshach.core.entity.ModelEngineOptions
import org.bukkit.entity.Entity
import taboolib.common.platform.function.warning
import taboolib.common.util.orNull

/**
 * 将 ModelEngineOptions 中的配置应用到 ActiveModel 实例上
 *
 * @param activeModel 需要应用配置的 ActiveModel 实例
 */
fun ModelEngineOptions.apply(activeModel: ActiveModel) {
    // 设置是否自动初始化渲染器
    activeModel.setAutoRendererInitialization(isAutoRendererInitialization)
    // 设置模型缩放比例
    activeModel.setScale(scale)
    // 设置碰撞箱缩放比例
    activeModel.setHitboxScale(hitboxScale)
    // 设置是否可以受到伤害
    activeModel.setCanHurt(canHurt)
    // 设置是否锁定俯仰角和偏航角
    activeModel.isLockPitch = isLockPitch
    activeModel.isLockYaw = isLockYaw
    // 设置碰撞箱和阴影的可见性
    activeModel.isHitboxVisible = isHitboxVisible
    activeModel.isShadowVisible = isShadowVisible
    // 设置默认和受伤时的模型颜色（如果有指定）
    if (defaultTint != null) activeModel.defaultTint = defaultTint
    if (damageTint != null) activeModel.damageTint = damageTint
}

/**
 * 获取与当前实体关联的 ModeledEntity 实例
 *
 * @return 如果找到则返回 ModeledEntity 实例，否则返回 null
 */
fun ModelEngine.getModeledEntity(): ModeledEntity? {
    return ModelEngineAPI.getModeledEntity(modelEngineUniqueId)
}

/**
 * 根据模型 ID 获取 ActiveModel 实例
 *
 * @param modelId 模型 ID
 * @return 如果找到则返回 ActiveModel 实例，否则返回 null
 */
fun ModelEngine.getActiveModel(modelId: String): ActiveModel? {
    val modeledEntity = getModeledEntity() ?: return null
    return modeledEntity.getModel(modelId.lowercase()).orNull()
}

/**
 * 播放模型动画
 *
 * @param modelId 模型 ID
 * @param animationId 动画 ID
 * @param lerpIn 动画淡入时间（单位：tick）
 * @param lerpOut 动画淡出时间（单位：tick）
 * @param speed 动画播放速度
 * @param isForceChange 是否强制切换动画
 * @param isForceOverride 是否强制覆盖其他动画
 * @param loopMode 动画循环模式
 * @param priority 动画优先级，数值越大优先级越高
 */
fun ModelEngine.playAnimation(
    modelId: String,
    animationId: String,
    lerpIn: Int = 0,
    lerpOut: Int = 0,
    speed: Double = 1.0,
    isForceChange: Boolean = false,
    isForceOverride: Boolean = false,
    loopMode: BlueprintAnimation.LoopMode = BlueprintAnimation.LoopMode.ONCE,
    priority: Int = 1,
) {
    val activeModel = getActiveModel(modelId) ?: return
    val handler = activeModel.animationHandler
    if (handler is IStateMachineHandler) {
        val property = handler.playAnimation(priority, animationId, lerpIn * 0.05, lerpOut * 0.05, speed, isForceChange)
        if (property != null) {
            property.forceLoopMode = loopMode
            property.isForceOverride = isForceOverride
        }
    } else {
        val property = handler.playAnimation(animationId, lerpIn * 0.05, lerpOut * 0.05, speed, isForceChange)
        if (property != null) {
            property.forceLoopMode = loopMode
            property.isForceOverride = isForceOverride
        }
    }
}

/**
 * 停止模型动画
 *
 * @param modelId 模型 ID
 * @param animationId 动画 ID
 * @param ignoreLerp 是否忽略淡出效果，true 表示立即停止
 * @param priority 动画优先级，仅在使用状态机时生效
 */
fun ModelEngine.stopAnimation(
    modelId: String,
    animationId: String,
    ignoreLerp: Boolean = false,
    priority: Int = 1,
) {
    val activeModel = getActiveModel(modelId) ?: return
    val handler = activeModel.animationHandler
    if (handler is IStateMachineHandler) {
        if (ignoreLerp) {
            handler.forceStopAnimation(priority, animationId)
        } else {
            handler.stopAnimation(priority, animationId)
        }
    } else if (ignoreLerp) {
        handler.forceStopAnimation(animationId)
    } else {
        handler.stopAnimation(animationId)
    }
}

internal fun ModelEngine.createModel() {
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
    despawn()
    // 创建模型
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
}

internal fun ModelEngine.getDummy(): EntityModeled? {
    this as EntityInstance
    return getTag("ModelEngine:EntityModeled") as? EntityModeled
}

internal fun Entity.asHitboxEntity(): HitboxEntity? {
    return Adyeshach.api().getMinecraftAPI().getHelper().toMinecraft(this) as? HitboxEntity?
}