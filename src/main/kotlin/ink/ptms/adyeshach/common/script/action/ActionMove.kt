package ink.ptms.adyeshach.common.script.action

import ink.ptms.adyeshach.common.script.ScriptHandler.entitySelected
import ink.ptms.adyeshach.common.script.ScriptHandler.getEntities
import ink.ptms.adyeshach.common.script.ScriptHandler.getManager
import io.izzel.taboolib.kotlin.kether.ScriptContext
import io.izzel.taboolib.kotlin.kether.ScriptParser
import io.izzel.taboolib.kotlin.kether.common.actions.LiteralAction
import io.izzel.taboolib.kotlin.kether.common.api.ParsedAction
import io.izzel.taboolib.kotlin.kether.common.api.QuestAction
import io.izzel.taboolib.kotlin.kether.common.api.QuestContext
import io.izzel.taboolib.kotlin.kether.common.loader.types.ArgTypes
import io.izzel.taboolib.util.Coerce
import org.bukkit.Location
import java.util.concurrent.CompletableFuture

/**
 * @author IzzelAliz
 */
class ActionMove(val x: ParsedAction<*>, val y: ParsedAction<*>, val z: ParsedAction<*>, val relative: Boolean) : QuestAction<Void>() {

    fun Any.d() = Coerce.toDouble(this)

    override fun process(context: QuestContext.Frame): CompletableFuture<Void> {
        return context.newFrame(x).run<Any>().thenAccept { x ->
            context.newFrame(y).run<Any>().thenAccept { y ->
                context.newFrame(z).run<Any>().thenAccept { z ->
                    val s = (context.context() as ScriptContext)
                    if (s.getManager() == null) {
                        throw RuntimeException("No manager selected.")
                    }
                    if (!s.entitySelected()) {
                        throw RuntimeException("No entity selected.")
                    }
                    s.getEntities()!!.filterNotNull().forEach {
                        if (relative) {
                            it.controllerMove(Location(it.position.world, it.position.x + x.d(), it.position.y + y.d(), it.position.z + z.d()))
                        } else {
                            it.controllerMove(Location(it.position.world, x.d(), y.d(), z.d()))
                        }
                    }
                }
            }
        }
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
            var x: ParsedAction<*> = ParsedAction(LiteralAction<Any>(0.0))
            var y: ParsedAction<*> = ParsedAction(LiteralAction<Any>(0.0))
            var z: ParsedAction<*> = ParsedAction(LiteralAction<Any>(0.0))
            while (it.hasNext()) {
                it.mark()
                when (it.nextToken()) {
                    "x" -> {
                        it.expect("to")
                        x = it.next(ArgTypes.ACTION)
                    }
                    "y" -> {
                        it.expect("to")
                        y = it.next(ArgTypes.ACTION)
                    }
                    "z" -> {
                        it.expect("to")
                        z = it.next(ArgTypes.ACTION)
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