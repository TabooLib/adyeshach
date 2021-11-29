package ink.ptms.adyeshach.common.script.action

import com.ticxo.modelengine.api.ModelEngineAPI
import com.ticxo.modelengine.api.manager.ModelManager
import ink.ptms.adyeshach.api.AdyeshachAPI
import ink.ptms.adyeshach.common.script.ScriptHandler.entitySelected
import ink.ptms.adyeshach.common.script.ScriptHandler.getEntities
import ink.ptms.adyeshach.common.script.ScriptHandler.getManager
import taboolib.common5.Coerce
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

class ActionModelAnimation(
    val state: String,
    val remove: Boolean,
    val speed: Double,
    val lerpin: Int,
    val lerpout: Int,
    val ingorelerp: Boolean,
) : ScriptAction<Void>() {

    val modelManager: ModelManager = ModelEngineAPI.api.modelManager

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        if (!AdyeshachAPI.modelEngineHooked) {
            return CompletableFuture.failedFuture(null)
        }
        val s = frame.script()
        if (s.getManager() == null) {
            error("No manager selected.")
        }
        if (!s.entitySelected()) {
            error("No entity selected.")
        }
        s.getEntities()!!.filterNotNull().forEach {
            if (it.modelEngineUniqueId != null) {
                val modeledEntity = modelManager.getModeledEntity(it.modelEngineUniqueId)
                val activeModel = modeledEntity.allActiveModel.values.firstOrNull()
                if (activeModel != null) {
                    if (remove) {
                        activeModel.removeState(state, ingorelerp)
                    } else {
                        activeModel.addState(state, lerpin, lerpout, speed)
                    }
                }
            }
        }
        return CompletableFuture.completedFuture(null)
    }

    internal object Parser {

        /**
         * modelengine animation add {token} [speed {double} [lerpin {int} [lerpout {int}]]]
         * modelengine animation remove {token} [ingorelerp {boolean}]
         */
        @KetherParser(["modelengine"], namespace = "adyeshach", shared = true)
        fun parser() = scriptParser {
            it.switch {
                case("animation") {
                    when (it.expects("add", "remove")) {
                        "add" -> {
                            val state = it.nextToken()
                            it.mark()
                            try {
                                it.expect("speed")
                                val speed = it.nextDouble()
                                it.mark()
                                try {
                                    it.expect("lerpin")
                                    val lerpin = it.nextInt()
                                    it.mark()
                                    try {
                                        it.expect("lerpout")
                                        val lerpout = it.nextInt()
                                        ActionModelAnimation(state, false, speed, lerpin, lerpout, false)
                                    } catch (ex: Exception) {
                                        it.reset()
                                        ActionModelAnimation(state, false, speed, lerpin, 1, false)
                                    }
                                } catch (ex: Exception) {
                                    it.reset()
                                    ActionModelAnimation(state, false, speed, 0, 1, false)
                                }
                            } catch (ex: Exception) {
                                it.reset()
                                ActionModelAnimation(state, false, 1.0, 0, 1, false)
                            }
                        }
                        "remove" -> {
                            val state = it.nextToken()
                            it.mark()
                            try {
                                it.expects("ingorelerp")
                                ActionModelAnimation(state, true, 0.0, 0, 0, Coerce.toBoolean(it.nextToken()))
                            } catch (ex: Exception) {
                                it.reset()
                                ActionModelAnimation(state, true, 0.0, 0, 0, false)
                            }
                        }
                        else -> error("out of case")
                    }
                }
            }
        }
    }
}