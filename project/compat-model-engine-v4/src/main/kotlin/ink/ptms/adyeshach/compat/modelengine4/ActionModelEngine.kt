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
 * modelengine animation add {token} [speed {double} [lerpin {int} [lerpout {int} [priority {int}]]]]
 * modelengine animation remove {token} [ingorelerp {boolean} [priority {int}]]
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
                command("loop", then = text()).option().defaultsTo("once"), // loop {text}
                command("priority", then = int()).option().defaultsTo(1) // priority {int}
            ).apply(it) { action, method, token, speed, lerpin, lerpout, ingorelerp, forceChange, forceOverride, loop, priority ->
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
                                            e.playAnimation(
                                                modelId = e.modelEngineName,
                                                animationId = token,
                                                lerpIn = lerpin,
                                                lerpOut = lerpout,
                                                speed = speed,
                                                isForceChange = forceChange,
                                                isForceOverride = forceOverride,
                                                loopMode = loopMode,
                                                priority = priority
                                            )
                                        }
                                        // 删除
                                        "remove" -> {
                                            e.stopAnimation(
                                                modelId = e.modelEngineName,
                                                animationId = token,
                                                ignoreLerp = ingorelerp,
                                                priority = priority
                                            )
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
