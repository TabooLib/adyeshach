package ink.ptms.adyeshach.common.script.action

import ink.ptms.adyeshach.common.script.ScriptHandler.entitySelected
import ink.ptms.adyeshach.common.script.ScriptHandler.getEntities
import ink.ptms.adyeshach.common.script.ScriptHandler.getManager
import io.izzel.taboolib.kotlin.kether.ScriptContext
import io.izzel.taboolib.kotlin.kether.ScriptParser
import io.izzel.taboolib.kotlin.kether.common.api.QuestAction
import io.izzel.taboolib.kotlin.kether.common.api.QuestContext
import org.bukkit.Location
import java.util.concurrent.CompletableFuture

/**
 * @author IzzelAliz
 */
class ActionMove(val x: Double, val y: Double, val z: Double, val relative: Boolean) : QuestAction<Void>() {

    override fun process(context: QuestContext.Frame): CompletableFuture<Void> {
        val s = (context.context() as ScriptContext)
        if (s.getManager() == null) {
            throw RuntimeException("No manager selected.")
        }
        if (!s.entitySelected()) {
            throw RuntimeException("No entity selected.")
        }
        s.getEntities()!!.filterNotNull().forEach {
            if (relative) {
                it.controllerMove(Location(it.position.world, it.position.x + x, it.position.y + y, it.position.z + z))
            } else {
                it.controllerMove(Location(it.position.world, x, y, z))
            }
        }
        return CompletableFuture.completedFuture(null)
    }

    override fun toString(): String {
        return "ActionMove(x=$x, y=$y, z=$z)"
    }

    companion object {

        @Suppress("UNCHECKED_CAST")
        fun parser() = ScriptParser.parser {
            var relative = false
            it.mark()
            if (it.nextToken() == "relative") {
                relative = true
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
            ActionMove(x, y, z, relative)
        }
    }
}