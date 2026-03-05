package ink.ptms.adyeshach.compat.modelengine4

import com.ticxo.modelengine.api.ModelEngineAPI
import com.ticxo.modelengine.api.animation.BlueprintAnimation
import com.ticxo.modelengine.api.model.bone.BoneBehaviorTypes
import com.ticxo.modelengine.v1_20_R3.NMSHandler_v1_20_R3
import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.AdyeshachEntityTypeRegistry
import ink.ptms.adyeshach.core.entity.EntityTypes
import ink.ptms.adyeshach.core.entity.ModelEngine
import ink.ptms.adyeshach.impl.entity.DefaultEntityInstance
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.util.unsafeLazy
import java.util.*
import java.util.function.Consumer


/**
 * Adyeshach
 * ink.ptms.adyeshach.compat.modelengine2.DefaultModelEngine
 *
 * @author 坏黑
 * @since 2022/6/19 21:58
 */
@Suppress("UNCHECKED_CAST")
internal interface DefaultModelEngine : ModelEngine {

    // 回调函数列表
    private val modelCreateHandlers: List<Consumer<UUID>>
        get() {
            this as DefaultEntityInstance
            return getTag("ModelEngine:CreateHandlers") as? List<Consumer<UUID>> ?: emptyList()
        }

    private val modelDestroyHandlers: List<Consumer<UUID>>
        get() {
            this as DefaultEntityInstance
            return getTag("ModelEngine:DestroyHandlers") as? List<Consumer<UUID>> ?: emptyList()
        }

    override fun showModelEngine(viewer: Player): Boolean {
        if (isModelEngineHooked && modelEngineName.isNotBlank()) {
            // 初始化模型
            if (modelEngineUniqueId == null) {
                createModel()
            } else {
                getDummy()?.setForceViewing(viewer, true)
            }
            return true
        }
        return false
    }

    override fun hideModelEngine(viewer: Player): Boolean {
        if (isModelEngineHooked && modelEngineName.isNotBlank() && modelEngineUniqueId != null) {
            this as DefaultEntityInstance
            // 如果没有其他观察者，直接销毁模型而不是隐藏
            if (!viewPlayers.hasVisiblePlayer() && modelEngineOptions?.isDestroyWhenInvisible == true) {
                destroyModelEngine()
            } else {
                getDummy()?.setForceViewing(viewer, false)
            }
            return true
        }
        return false
    }

    override fun destroyModelEngine() {
        if (isModelEngineHooked && modelEngineUniqueId != null) {
            this as DefaultEntityInstance
            val uuid = modelEngineUniqueId!!
            ModelEngineAPI.removeModeledEntity(uuid)
            modelEngineUniqueId = null
            // 触发销毁回调
            modelDestroyHandlers.forEach { it.accept(uuid) }
        }
    }

    override fun refreshModelEngine(): Boolean {
        if (isModelEngineHooked) {
            this as DefaultEntityInstance
            // 创建模型
            if (modelEngineName.isNotBlank()) {
                // 初始化模型
                despawn()
                createModel()
            }
            // 销毁模型
            else if (ModelEngineAPI.removeModeledEntity(modelEngineUniqueId) != null) {
                val modeled = (getTag("ModelEngine:EntityModeled") as? EntityModeled)
                ModelEngineAPI.getModeledEntity(modeled?.uuid).isBaseEntityVisible = true
                respawn()
            }
            return true
        }
        return false
    }

    override fun updateModelEngineNameTag() {
        this as DefaultEntityInstance
        getModeledEntity()?.models?.values?.forEach { model ->
            model.getBone("nametag").flatMap { it.getBoneBehavior(BoneBehaviorTypes.NAMETAG) }.ifPresent { nameTag ->
                // 名称可见
                if (isCustomNameVisible()) {
                    nameTag.isVisible = true
                } else {
                    nameTag.isVisible = false
                    return@ifPresent
                }
                // 名称内容
                nameTag.jsonString = getCustomNameRaw()
            }
        }
    }

    override fun hurt() {
    }

    override fun restoreAnimationState() {
        if (isModelEngineHooked) {
            this as DefaultEntityInstance
            // 获取所有以 "ModelEngine:Animation:" 开头的持久化标签
            getPersistentTags().forEach { (key, value) ->
                if (key.startsWith("ModelEngine:Animation:")) {
                    val modelId = key.substringAfter("ModelEngine:Animation:")
                    val state = AnimationState.deserialize(value) ?: return@forEach
                    // 还原动画播放
                    val loopMode = try {
                        BlueprintAnimation.LoopMode.valueOf(state.loopMode)
                    } catch (_: Exception) {
                        BlueprintAnimation.LoopMode.HOLD
                    }
                    playAnimation(
                        modelId = modelId,
                        animationId = state.animationId,
                        lerpIn = state.lerpIn,
                        lerpOut = state.lerpOut,
                        speed = state.speed,
                        isForceChange = false,
                        isForceOverride = state.isForceOverride,
                        loopMode = loopMode,
                        priority = state.priority
                    )
                }
            }
        }
    }

    override fun clearAnimationState() {
        this as DefaultEntityInstance
        // 清除所有动画状态
        getPersistentTags().forEach { (key, _) ->
            if (key.startsWith("ModelEngine:Animation:")) {
                removePersistentTag(key)
            }
        }
    }

    override fun onModelCreate(handler: Consumer<UUID>) {
        this as DefaultEntityInstance
        val newHandlers = modelCreateHandlers.toMutableList()
        newHandlers += handler
        setTag("ModelEngine:CreateHandlers", newHandlers)
    }

    override fun onModelDestroy(handler: Consumer<UUID>) {
        this as DefaultEntityInstance
        val newHandlers = modelDestroyHandlers.toMutableList()
        newHandlers += handler
        setTag("ModelEngine:DestroyHandlers", newHandlers)
    }

    fun triggerModelCreateCallbacks(uuid: UUID) {
        modelCreateHandlers.forEach { it.accept(uuid) }
    }

    companion object {

        val isModelEngineHooked by unsafeLazy {
            (Bukkit.getPluginManager().getPlugin("ModelEngine") != null) && kotlin.runCatching { NMSHandler_v1_20_R3::class.java }.isSuccess
        }

        @Awake(LifeCycle.LOAD)
        fun init() {
            // 注册生成回调
            Adyeshach.api().getEntityTypeRegistry().prepareGenerate(object : AdyeshachEntityTypeRegistry.GenerateCallback {

                override fun invoke(entityType: EntityTypes, interfaces: List<String>): List<String> {
                    val array = ArrayList<String>()
                    // 是否安装 ModelEngine 扩展
                    if (isModelEngineHooked) {
                        array += DefaultModelEngine::class.java.name
                    }
                    return array
                }
            })
        }
    }
}