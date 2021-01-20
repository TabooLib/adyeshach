package ink.ptms.adyeshach.common.script.action.npc

import ink.ptms.adyeshach.common.script.ScriptContext
import ink.ptms.adyeshach.common.script.ScriptParser
import io.izzel.kether.common.api.QuestAction
import io.izzel.kether.common.api.QuestContext
import org.bukkit.Location
import java.util.concurrent.CompletableFuture

/**
 * @author IzzelAliz
 */
class ActionLook(val x: Double, val y: Double, val z: Double, val smooth: Boolean) : QuestAction<Void>() {

    override fun process(context: QuestContext.Frame): CompletableFuture<Void> {
        val s = (context.context() as ScriptContext)
        if (s.manager == null) {
            throw RuntimeException("No manager selected.")
        }
        if (!s.entitySelected()) {
            throw RuntimeException("No entity selected.")
        }
        s.entities!!.filterNotNull().forEach {
            it.controllerLook(Location(it.position.world, x, y, z), smooth)
        }
        return CompletableFuture.completedFuture(null)
    }

    override fun toString(): String {
        return "ActionLook(x=$x, y=$y, z=$z, smooth=$smooth)"
    }

    companion object {

        @Suppress("UNCHECKED_CAST")
        fun parser() = ScriptParser.parser {
            var smooth = false
            it.mark()
            if (it.nextToken() == "smooth") {
                smooth = true
            } else {
                it.reset()
            }
            var x = 0.0
            var y = 0.0
            var z = 0.0
            while (it.hasNext()) {
                it.mark()
                when (it.nextToken()) {
                    "x" -> {
                        it.expect("to")
                        x = it.nextDouble()
                    }
                    "y" -> {
                        it.expect("to")
                        y = it.nextDouble()
                    }
                    "z" -> {
                        it.expect("to")
                        z = it.nextDouble()
                    }
                    else -> {
                        it.reset()
                        break
                    }
                }
            }
            ActionLook(x, y, z, smooth)
        }
    }
}