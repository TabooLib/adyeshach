package ink.ptms.adyeshach.common.script.action.npc

import ink.ptms.adyeshach.common.script.ScriptContext
import io.izzel.kether.common.api.*
import org.bukkit.Location
import java.lang.RuntimeException
import java.util.concurrent.CompletableFuture
import java.util.function.Function

/**
 * @author IzzelAliz
 */
class ActionLook(val x: Double, val y: Double, val z: Double, val smooth: Boolean) : QuestAction<Void, ScriptContext> {

    override fun isAsync(): Boolean {
        return false
    }

    override fun process(context: ScriptContext): CompletableFuture<Void> {
        if (context.getManager() == null) {
            throw RuntimeException("No manager selected.")
        }
        if (!context.entitySelected()) {
            throw RuntimeException("No entity selected.")
        }
        context.getEntity()!!.filterNotNull().forEach {
            it.controllerLook(Location(it.position.world, x, y, z), smooth)
        }
        return CompletableFuture.completedFuture(null)
    }

    override fun getDataPrefix(): String {
        return "look"
    }

    override fun toString(): String {
        return "ActionLook(x=$x, y=$y, z=$z, smooth=$smooth)"
    }

    companion object {

        @Suppress("UNCHECKED_CAST")
        fun parser(): QuestActionParser {
            return object : QuestActionParser {

                override fun <T, C : QuestContext> resolve(resolver: QuestResolver<C>): QuestAction<T, C> {
                    return Function<QuestResolver<C>, QuestAction<T, C>> { t ->
                        var x = 0.0
                        var y = 0.0
                        var z = 0.0
                        var smooth = false
                        while (t.hasNext()) {
                            t.mark()
                            when (t.nextElement()) {
                                "x" -> x = t.nextDouble()
                                "y" -> y = t.nextDouble()
                                "z" -> z = t.nextDouble()
                                else -> {
                                    t.reset()
                                    break
                                }
                            }
                        }
                        if (t.hasNext()) {
                            t.mark()
                            if (t.nextElement() == "smooth") {
                                smooth = true
                            } else {
                                t.reset()
                            }
                        }
                        ActionLook(x, y, z, smooth) as QuestAction<T, C>
                    }.apply(resolver)
                }

                override fun complete(parms: List<String>): List<String> {
                    return KetherCompleters.seq(KetherCompleters.consume()).apply(parms)
                }
            }
        }
    }
}