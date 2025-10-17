package ink.ptms.adyeshach.compat.germengine

import com.germ.germplugin.api.GermPacketAPI
import ink.ptms.adyeshach.core.util.errorBy
import ink.ptms.adyeshach.impl.getEntities
import ink.ptms.adyeshach.impl.getManager
import ink.ptms.adyeshach.impl.isEntitySelected
import ink.ptms.adyeshach.impl.throwUndefinedError
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

class ActionGermEngine(val state: String, val remove: Boolean) : ScriptAction<Void>() {

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        val script = frame.script()
        if (script.getManager() == null || !script.isEntitySelected()) {
            script.throwUndefinedError()
        }
        script.getEntities().forEach {
            if (remove) {
                it.forViewers { p -> GermPacketAPI.stopModelAnimation(p, it.index, state) }
            } else {
                it.forViewers { p -> GermPacketAPI.sendModelAnimation(p, it.index, state) }
            }
        }
        return CompletableFuture.completedFuture(null)
    }

    companion object {

        @KetherParser(["germengine"], namespace = "adyeshach", shared = true)
        fun parser() = scriptParser {
            it.switch {
                case("animation") {
                    when (it.expects("send", "stop")) {
                        "send" -> ActionGermEngine(it.nextToken(), false)
                        "stop" -> ActionGermEngine(it.nextToken(), true)
                        else -> error("out of case")
                    }
                }
            }
        }
    }
}