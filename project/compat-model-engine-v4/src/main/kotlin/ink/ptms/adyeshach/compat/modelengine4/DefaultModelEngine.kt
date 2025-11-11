package ink.ptms.adyeshach.compat.modelengine4

import com.ticxo.modelengine.api.ModelEngineAPI
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


/**
 * Adyeshach
 * ink.ptms.adyeshach.compat.modelengine2.DefaultModelEngine
 *
 * @author 坏黑
 * @since 2022/6/19 21:58
 */
internal interface DefaultModelEngine : ModelEngine {

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
            if (!viewPlayers.hasVisiblePlayer()) {
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
            ModelEngineAPI.removeModeledEntity(modelEngineUniqueId)
            modelEngineUniqueId = null
        }
    }

    override fun refreshModelEngine(): Boolean {
        if (isModelEngineHooked) {
            this as DefaultEntityInstance
            // 创建模型
            if (modelEngineName.isNotBlank()) {
                // 初始化模型
                createModel()
            }
            // 销毁模型
            else if (ModelEngineAPI.removeModeledEntity(modelEngineUniqueId) != null) {
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