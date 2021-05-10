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
class ActionLook(val x: ParsedAction<*>, val y: ParsedAction<*>, val z: ParsedAction<*>, val smooth: Boolean) : QuestAction<Void>() {

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
                        it.controllerLook(Location(it.position.world, Coerce.toDouble(x), Coerce.toDouble(y), Coerce.toDouble(z)), smooth)
                    }
                }
            }
        }
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
            ActionLook(x, y, z, smooth)
        }
    }
}