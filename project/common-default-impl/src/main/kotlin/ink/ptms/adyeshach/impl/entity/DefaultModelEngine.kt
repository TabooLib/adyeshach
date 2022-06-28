package ink.ptms.adyeshach.impl.entity

import com.ticxo.modelengine.api.ModelEngineAPI
import ink.ptms.adyeshach.common.api.Adyeshach
import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.ModelEngine
import ink.ptms.adyeshach.common.util.asLang
import ink.ptms.adyeshach.common.util.errorBy
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import taboolib.common.platform.function.console
import taboolib.common.platform.function.submit
import taboolib.common.platform.function.warning
import java.util.*

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.entity.DefaultModelEngine
 *
 * @author 坏黑
 * @since 2022/6/19 21:58
 */
interface DefaultModelEngine : ModelEngine {

    override fun showModelEngine(viewer: Player): Boolean {
        if (modelEngineHooked) {
            val modelManager = ModelEngineAPI.api.modelManager
            if (modelEngineUniqueId != null) {
                modelManager.getModeledEntity(modelEngineUniqueId)?.addPlayer(viewer) ?: return false
                return true
            } else if (modelEngineName.isNotBlank()) {
                return refreshModelEngine()
            }
        }
        return false
    }

    override fun hideModelEngine(viewer: Player): Boolean {
        if (modelEngineHooked) {
            val modelManager = ModelEngineAPI.api.modelManager
            if (modelEngineUniqueId != null) {
                modelManager.getModeledEntity(modelEngineUniqueId)?.removePlayer(viewer) ?: return false
                return true
            }
        }
        return false
    }

    override fun refreshModelEngine(): Boolean {
        if (modelEngineHooked) {
            this as DefaultEntityInstance
            val modelManager = ModelEngineAPI.api.modelManager
            // 删除模型
            if (modelEngineUniqueId != null) {
                val modeledEntity = modelManager.getModeledEntity(modelEngineUniqueId)
                if (modeledEntity != null) {
                    modeledEntity.clearModels()
                    forViewers { modeledEntity.removePlayer(it) }
                    modelManager.removeModeledEntity(modelEngineUniqueId)
                    modelEngineUniqueId = null
                    // 是否恢复单位
                    if (modelEngineName.isBlank()) {
                        respawn()
                    }
                }
            }
            // 创建模型
            if (modelEngineName.isNotBlank()) {
                val entityModeled = EntityModeled(this)
                val model = modelManager.createActiveModel(modelEngineName)
                if (model == null) {
                    warning(asLang("error-failed-to-load-model", modelEngineName))
                    return false
                }
                val modeledEntity = modelManager.createModeledEntity(entityModeled)
                if (modeledEntity == null) {
                    warning(asLang("error-failed-to-create-modeled-entity"))
                    return false
                }
                try {
                    modeledEntity.addActiveModel(model)
                } catch (ex: NullPointerException) {
                    warning(asLang("error-failed-to-add-model", modelEngineName, ex.message.toString()))
                    return false
                }
                destroy()
                modeledEntity.isInvisible = true
                val nameTag = modeledEntity.nametagHandler.allTags.firstOrNull()
                if (nameTag != null) {
                    modeledEntity.nametagHandler.setCustomName(nameTag, getCustomName())
                    modeledEntity.nametagHandler.setCustomNameVisibility(nameTag, isCustomNameVisible())
                }
                modelEngineUniqueId = entityModeled.modelUniqueId
                // 首次加载模型的时候还不存在任何观察者，因此需要延迟添加
                // 否则会出现模型消失的问题
                // 于 2022-05-16 日由阿瑞（1484813603）反馈
                submit { forViewers { modeledEntity.addPlayer(it) } }
                return true
            }
        }
        return false
    }

    override fun updateModelEngineNameTag() {
        if (modelEngineHooked) {
            this as EntityInstance
            val modelManager = ModelEngineAPI.api.modelManager
            if (modelEngineUniqueId != null) {
                val modeledEntity = modelManager.getModeledEntity(modelEngineUniqueId) ?: return
                val nameTag = modeledEntity.nametagHandler.allTags.firstOrNull()
                if (nameTag != null) {
                    modeledEntity.nametagHandler.setCustomName(nameTag, getCustomName())
                    modeledEntity.nametagHandler.setCustomNameVisibility(nameTag, isCustomNameVisible())
                }
            }
        }
    }

    companion object {

        val modelEngineHooked by lazy { Bukkit.getPluginManager().getPlugin("ModelEngine") != null }
    }
}