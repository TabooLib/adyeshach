package ink.ptms.adyeshach.compat.modelengine2

import com.ticxo.modelengine.api.ModelEngineAPI
import ink.ptms.adyeshach.core.entity.ModelEngine
import ink.ptms.adyeshach.core.util.errorBy
import ink.ptms.adyeshach.impl.getEntities
import ink.ptms.adyeshach.impl.getManager
import ink.ptms.adyeshach.impl.isEntitySelected
import ink.ptms.adyeshach.impl.throwUndefinedError
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.module.kether.*

/**
 * modelengine animation add {token} [speed {double} [lerpin {int} [lerpout {int}]]]
 * modelengine animation remove {token} [ingorelerp {boolean}]
 */
@Awake(LifeCycle.LOAD)
internal fun init() {
    if (DefaultModelEngine.isModelEngineHooked) {
        val modelManager = ModelEngineAPI.api.modelManager
        KetherLoader.registerParser(combinationParser {
            it.group(
                symbol(), // animation
                symbol(), // add, remove
                text(), // {token}
                command("speed", then = double()).option().defaultsTo(0.0), // speed {double}
                command("lerpin", then = double()).option().defaultsTo(0.0), // lerpin {double}
                command("lerpout", then = double()).option().defaultsTo(0.0), // lerpout {double}
                command("ingorelerp", then = bool()).option().defaultsTo(false), // ingorelerp {boolean}
            ).apply(it) { action, method, token, speed, lerpin, lerpout, ingorelerp ->
                now {
                    if (script().getManager() == null || !script().isEntitySelected()) {
                        script().throwUndefinedError()
                    }
                    script().getEntities().filterIsInstance<ModelEngine>().forEach { e ->
                        if (e.modelEngineUniqueId != null) {
                            val modeledEntity = modelManager.getModeledEntity(e.modelEngineUniqueId)
                            when (action.lowercase()) {
                                // 播放动画
                                "animation" -> {
                                    when (method.lowercase()) {
                                        // 添加
                                        "add" -> {
                                            modeledEntity.allActiveModel.forEach { (_, model) ->
                                                model.addState(token, speed.toInt(), lerpin.toInt(), lerpout)
                                            }
                                        }
                                        // 删除
                                        "remove" -> {
                                            modeledEntity.allActiveModel.forEach { (_, model) -> model.removeState(token, ingorelerp) }
                                        }
                                        // 其他
                                        else -> error("Unknown method: $method (add, remove)")
                                    }
                                }
                                // 其他
                                else -> error("Unknown action: $action (animation)")
                            }
                        }
                    }
                }
            }
        }, arrayOf("modelengine"), "adyeshach", true)
    }
}