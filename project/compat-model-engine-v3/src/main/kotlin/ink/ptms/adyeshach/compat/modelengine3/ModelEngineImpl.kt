package ink.ptms.adyeshach.compat.modelengine3

import com.ticxo.modelengine.api.ModelEngineAPI
import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.AdyeshachEntityTypeRegistry
import ink.ptms.adyeshach.core.entity.EntityInstance
import ink.ptms.adyeshach.core.entity.EntityTypes
import ink.ptms.adyeshach.core.entity.ModelEngine
import ink.ptms.adyeshach.core.util.asLang
import ink.ptms.adyeshach.impl.entity.DefaultEntityInstance
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.function.warning
import taboolib.common.util.unsafeLazy

/**
 * Adyeshach
 * ink.ptms.adyeshach.compat.modelengine2.DefaultModelEngine
 *
 * @author 坏黑
 * @since 2022/6/19 21:58
 */
@Suppress("SpellCheckingInspection")
interface DefaultModelEngine : ModelEngine {

    override fun showModelEngine(viewer: Player): Boolean {
        if (isModelEngineHooked) {
            if (modelEngineUniqueId != null) {
                val modeledEntity = ModelEngineAPI.getModeledEntity(modelEngineUniqueId)
                if (modeledEntity != null) {
                    modeledEntity.models.forEach { (_, v) -> v.showToPlayer(viewer) }
                    return true
                }
            }
            if (modelEngineName.isNotBlank()) {
                return refreshModelEngine()
            }
        }
        return false
    }

    override fun hideModelEngine(viewer: Player): Boolean {
        if (isModelEngineHooked) {
            if (modelEngineUniqueId != null) {
                val modeledEntity = ModelEngineAPI.getModeledEntity(modelEngineUniqueId)
                if (modeledEntity != null) {
                    modeledEntity.models.forEach { (_, v) -> v.hideFromPlayer(viewer) }
                    return true
                } else {
                    warning("Not found modeled entity: $modelEngineUniqueId")
                }
            }
        }
        return false
    }

    override fun refreshModelEngine(): Boolean {
        if (isModelEngineHooked) {
            this as DefaultEntityInstance
            // 删除模型
            if (modelEngineUniqueId != null) {
                val modeledEntity = ModelEngineAPI.getModeledEntity(modelEngineUniqueId)
                if (modeledEntity != null) {
                    // TODO
                    // modeledEntity.clearModels()
                    // forViewers { modeledEntity.removePlayer(it) }

                    modeledEntity.models.toMap().forEach { (k, v) ->
                        modeledEntity.removeModel(k)
                        forViewers { v.hideFromPlayer(it) }
                    }

                    ModelEngineAPI.removeModeledEntity(modelEngineUniqueId)
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
                val blueprint = ModelEngineAPI.getBlueprint(modelEngineName)
                if (blueprint == null) {
                    warning(asLang("error-failed-to-load-model", modelEngineName))
                    return false
                }
                val modeledEntity = ModelEngineAPI.createModeledEntity(entityModeled)
                modeledEntity.isBaseEntityVisible = false
                val activeModel = ModelEngineAPI.createActiveModel(blueprint)
                modeledEntity.addModel(activeModel, true)
                forViewers { activeModel.showToPlayer(it) }

//                val model = ModelEngineAPI.createActiveModel(modelEngineName)
//                if (model == null) {
//                    warning(asLang("error-failed-to-load-model", modelEngineName))
//                    return false
//                }
//                val modeledEntity = ModelEngineAPI.createModeledEntity(entityModeled)
//                if (modeledEntity == null) {
//                    warning(asLang("error-failed-to-create-modeled-entity"))
//                    return false
//                }
//                try {
//                    modeledEntity.addModel(model, false)
//                } catch (ex: NullPointerException) {
//                    warning(asLang("error-failed-to-add-model", modelEngineName, ex.message.toString()))
//                    return false
//                }
//                despawn()

                // v2展示名称
                // modeledEntity.isInvisible = true
                // val nameTag = modeledEntity.nametagHandler.allTags.firstOrNull()
                // if (nameTag != null) {
                //     modeledEntity.nametagHandler.setCustomName(nameTag, getCustomName())
                //     modeledEntity.nametagHandler.setCustomNameVisibility(nameTag, isCustomNameVisible())
                // }

                // v3展示名称
//                val nameTag = model.nametagHandler.bones["nametag"]
//                if (nameTag != null) {
//                    nameTag.customName = getCustomName()
//                    nameTag.isCustomNameVisible = isCustomNameVisible()
//                }

                modelEngineUniqueId = entityModeled.modelUniqueId
                // 首次加载模型的时候还不存在任何观察者，因此需要延迟添加
                // 否则会出现模型消失的问题
                // 于 2022-05-16 日由阿瑞（1484813603）反馈
                // TODO
                // submit { forViewers { modeledEntity.addPlayer(it) } }

                // submit { forViewers { model.showToPlayer(it) } }
                return true
            }
        }
        return false
    }

    override fun updateModelEngineNameTag() {
        if (isModelEngineHooked) {
            this as EntityInstance
            if (modelEngineUniqueId != null) {
                val modeledEntity = ModelEngineAPI.getModeledEntity(modelEngineUniqueId) ?: return
                val model = modeledEntity.models.values.firstOrNull() ?: return
                val nameTag = model.nametagHandler.bones["nametag"]
                if (nameTag != null) {
                    nameTag.customName = getCustomName()
                    nameTag.isCustomNameVisible = isCustomNameVisible()
                }
            }
        }
    }

    override fun hurt() {
        ModelEngineAPI.getModeledEntity(modelEngineUniqueId)?.hurt()
    }

    companion object {

        val isModelEngineHooked by unsafeLazy {
            (Bukkit.getPluginManager().getPlugin("ModelEngine") != null) && kotlin.runCatching { ModelEngineAPI.api.modelRegistry }.isSuccess
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