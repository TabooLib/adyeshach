package ink.ptms.adyeshach.common.script.action

import com.ticxo.modelengine.api.ModelEngineAPI
import ink.ptms.adyeshach.api.AdyeshachAPI
import ink.ptms.adyeshach.common.script.ScriptHandler.entitySelected
import ink.ptms.adyeshach.common.script.ScriptHandler.getEntities
import ink.ptms.adyeshach.common.script.ScriptHandler.getManager
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

class ActionModelAnimation(val model: String, val state: String, val remove: Boolean, val speed: Double, val lerpin: Int, val lerpout: Int, val ingorelerp: Boolean): ScriptAction<Void>() {

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        if (!AdyeshachAPI.modelEngineHooked) return CompletableFuture.failedFuture(null)
        val s = frame.script()
        if (s.getManager() == null) {
            error("No manager selected.")
        }
        if (!s.entitySelected()) {
            error("No entity selected.")
        }
        s.getEntities()!!.filterNotNull().forEach {
            val modelManager = ModelEngineAPI.api.modelManager
            if (it.modelEngineUniqueId != null) {
                val modeledEntity =
                    modelManager.getModeledEntity(it.modelEngineUniqueId)
                if (remove) {
                    while (modeledEntity.allActiveModel.values.iterator().hasNext()) {
                        val active = modeledEntity.allActiveModel.values.iterator().next()
                        active.removeState(state, ingorelerp)
                    }
                }
                else {
                    while (modeledEntity.allActiveModel.values.iterator().hasNext()) {
                        val active = modeledEntity.allActiveModel.values.iterator().next()
                        active.addState(state, lerpin, lerpout, speed)
                    }
                }
            } else return CompletableFuture.failedFuture(null)
        }
        return CompletableFuture.completedFuture(null)
    }
    internal object Parser {

        @KetherParser(["model_animation"], namespace = "adyeshach", shared = true)
        fun parser() = scriptParser {
            val model = it.nextToken()
            val state = it.nextToken()
            val remove = (it.nextToken() ?: false).toString().toBoolean()
            val speed = (it.nextToken() ?: 1).toString().toDouble()
            val lerpin = (it.nextToken() ?: 0).toString().toInt()
            val lerpout = (it.nextToken() ?: 1).toString().toInt()
            val ingorelerp = (it.nextToken() ?: false).toString().toBoolean()
            ActionModelAnimation(model, state, remove, speed, lerpin, lerpout, ingorelerp)
        }
    }
}