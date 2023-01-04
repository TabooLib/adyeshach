package ink.ptms.adyeshach.compat.modelengine2

import com.ticxo.modelengine.api.ModelEngineAPI
import com.ticxo.modelengine.api.manager.ModelManager
import ink.ptms.adyeshach.core.entity.ModelEngine
import ink.ptms.adyeshach.core.util.errorBy
import ink.ptms.adyeshach.impl.getEntities
import ink.ptms.adyeshach.impl.getManager
import ink.ptms.adyeshach.impl.isEntitySelected
import taboolib.common5.Coerce
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

class ActionModelEngine(
    val state: String,
    val remove: Boolean,
    val speed: Double,
    val lerpin: Int,
    val lerpout: Int,
    val ingorelerp: Boolean,
) : ScriptAction<Void>() {

    val modelManager: ModelManager = ModelEngineAPI.api.modelManager

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        val script = frame.script()
        if (script.getManager() == null || !script.isEntitySelected()) {
            errorBy("error-no-manager-or-entity-selected")
        }
        script.getEntities().filterIsInstance<ModelEngine>().forEach {
            if (it.modelEngineUniqueId != null) {
                val modeledEntity = modelManager.getModeledEntity(it.modelEngineUniqueId)
                if (remove) {
                    val active = modeledEntity.allActiveModel.values.iterator()
                    while (active.hasNext()) {
                        val activeModel = active.next()
                        activeModel.removeState(state, ingorelerp)
                    }
                } else {
                    val active = modeledEntity.allActiveModel.values.iterator()
                    while (active.hasNext()) {
                        val activeModel = active.next()
                        activeModel.addState(state, lerpin, lerpout, speed)
                    }
                }
            }
        }
        return CompletableFuture.completedFuture(null)
    }

    companion object {

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
                                        ActionModelEngine(state, false, speed, lerpin, lerpout, false)
                                    } catch (ex: Exception) {
                                        it.reset()
                                        ActionModelEngine(state, false, speed, lerpin, 1, false)
                                    }
                                } catch (ex: Exception) {
                                    it.reset()
                                    ActionModelEngine(state, false, speed, 0, 1, false)
                                }
                            } catch (ex: Exception) {
                                it.reset()
                                ActionModelEngine(state, false, 1.0, 0, 1, false)
                            }
                        }
                        "remove" -> {
                            val state = it.nextToken()
                            it.mark()
                            try {
                                it.expects("ingorelerp")
                                ActionModelEngine(state, true, 0.0, 0, 0, Coerce.toBoolean(it.nextToken()))
                            } catch (ex: Exception) {
                                it.reset()
                                ActionModelEngine(state, true, 0.0, 0, 0, false)
                            }
                        }
                        else -> error("out of case")
                    }
                }
            }
        }
    }
}