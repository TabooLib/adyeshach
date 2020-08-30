package ink.ptms.adyeshach.common.script.action.npc

import ink.ptms.adyeshach.common.script.ScriptContext
import io.izzel.kether.common.api.*
import java.lang.RuntimeException
import java.util.concurrent.CompletableFuture
import java.util.function.Function

/**
 * @author IzzelAliz
 */
class ActionMove(val x: Double, val y: Double, val z: Double, val offset: Boolean) : QuestAction<Void, ScriptContext> {

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
            if (offset) {
                it.teleport(it.position.x + x, it.position.y + y, it.position.z + z)
            } else {
                it.teleport(x, y, z)
            }
        }
        return CompletableFuture.completedFuture(null)
    }

    override fun getDataPrefix(): String {
        return "move"
    }

    override fun toString(): String {
        return "ActionMove(x=$x, y=$y, z=$z)"
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
                        var offset = false
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
                            if (t.nextElement() == "offset") {
                                offset = true
                            } else {
                                t.reset()
                            }
                        }
                        ActionMove(x, y, z, offset) as QuestAction<T, C>
                    }.apply(resolver)
                }

                override fun complete(parms: List<String>): List<String> {
                    return KetherCompleters.seq(KetherCompleters.consume()).apply(parms)
                }
            }
        }
    }
}