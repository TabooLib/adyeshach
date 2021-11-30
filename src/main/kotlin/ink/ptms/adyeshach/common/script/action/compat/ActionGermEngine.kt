package ink.ptms.adyeshach.common.script.action.compat

import com.germ.germplugin.api.GermPacketAPI
import ink.ptms.adyeshach.common.script.ScriptHandler.entitySelected
import ink.ptms.adyeshach.common.script.ScriptHandler.getEntities
import ink.ptms.adyeshach.common.script.ScriptHandler.getManager
import org.bukkit.entity.Player
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

class ActionGermEngine(val state: String, val remove: Boolean) : ScriptAction<Void>() {

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        val s = frame.script()
        if (s.getManager() == null) {
            error("No manager selected.")
        }
        if (!s.entitySelected()) {
            error("No entity selected.")
        }
        s.getEntities()!!.filterNotNull().forEach {
            if (remove) {
                it.forViewers { p ->
                    GermPacketAPI.stopModelAnimation(p, it.index, state)
                }
            } else {
                it.forViewers { p ->
                    GermPacketAPI.sendModelAnimation(p, it.index, state)
                }
            }
        }
        return CompletableFuture.completedFuture(null)
    }

    internal object Parser {

        @KetherParser(["germengine"], namespace = "adyeshach", shared = true)
        fun parser() = scriptParser {
            it.switch {
                case("animation") {
                    when (it.expects("send", "stop")) {
                        "send" -> {
                            ActionGermEngine(it.nextToken(), false)
                        }
                        "stop" -> {
                            ActionGermEngine(it.nextToken(), true)
                        }
                        else -> error("out of case")
                    }
                }
            }
        }
    }
}