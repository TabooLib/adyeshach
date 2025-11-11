package ink.ptms.adyeshach.compat.modelengine4

import com.ticxo.modelengine.api.animation.BlueprintAnimation
import ink.ptms.adyeshach.core.entity.ModelEngine
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
        KetherLoader.registerParser(combinationParser {
            it.group(
                symbol(), // animation
                symbol(), // add, remove
                text(), // {token}
                command("speed", then = double()).option().defaultsTo(1.0), // speed {double}
                command("lerpin", then = int()).option().defaultsTo(0), // lerpin {int}
                command("lerpout", then = int()).option().defaultsTo(0), // lerpout {int}
                command("ingorelerp", then = bool()).option().defaultsTo(false), // ingorelerp {boolean}
                command("force-change", then = bool()).option().defaultsTo(false), // force-change {boolean}
                command("force-override", then = bool()).option().defaultsTo(false), // force-override {boolean}
                command("loop", then = text()).option().defaultsTo("once") // loop {text}
            ).apply(it) { action, method, token, speed, lerpin, lerpout, ingorelerp, forceChange, forceOverride, loop ->
                now {
                    if (script().getManager() == null || !script().isEntitySelected()) {
                        script().throwUndefinedError()
                    }
                    val loopMode = BlueprintAnimation.LoopMode.get(loop)
                    script().getEntities().filterIsInstance<ModelEngine>().forEach { e ->
                        if (e.modelEngineUniqueId != null) {
                            when (action.lowercase()) {
                                // 播放动画
                                "animation" -> {
                                    when (method.lowercase()) {
                                        // 添加
                                        "add", "play" -> {
                                            e.playAnimation(e.modelEngineName, token, lerpin, lerpout, speed, forceChange, forceOverride, loopMode)
                                        }
                                        // 删除
                                        "remove" -> {
                                            e.stopAnimation(e.modelEngineName, token, ingorelerp)
                                        }
                                        // 其他
                                        else -> error("Unknown method: $method (play, remove)")
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