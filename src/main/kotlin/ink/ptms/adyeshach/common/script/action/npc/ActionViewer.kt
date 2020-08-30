package ink.ptms.adyeshach.common.script.action.npc

import ink.ptms.adyeshach.common.script.ScriptContext
import io.izzel.kether.common.api.*
import io.izzel.kether.common.util.LocalizedException
import org.bukkit.Bukkit
import java.util.concurrent.CompletableFuture
import java.util.function.Function

/**
 * @author IzzelAliz
 */
class ActionViewer(val symbol: Symbol, val viewer: String?) : QuestAction<Void, ScriptContext> {

    enum class Symbol {

        ADD, REMOVE, RESET
    }

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
            when (symbol) {
                Symbol.ADD -> {
                    Bukkit.getPlayerExact(viewer!!)?.run {
                        it.addViewer(this)
                    }
                }
                Symbol.REMOVE -> {
                    Bukkit.getPlayerExact(viewer!!)?.run {
                        it.removeViewer(this)
                    }
                }
                Symbol.RESET -> it.clearViewer()
            }
        }
        return CompletableFuture.completedFuture(null)
    }

    override fun getDataPrefix(): String {
        return "viewer"
    }

    override fun toString(): String {
        return "ActionViewer(symbol=$symbol, viewer=$viewer)"
    }

    companion object {

        @Suppress("UNCHECKED_CAST")
        fun parser(): QuestActionParser {
            return object : QuestActionParser {

                override fun <T, C : QuestContext> resolve(resolver: QuestResolver<C>): QuestAction<T, C> {
                    return Function<QuestResolver<C>, QuestAction<T, C>> { t ->
                        val symbol = when (val type = t.nextElement()) {
                            "add" -> Symbol.ADD
                            "remove" -> Symbol.REMOVE
                            "reset" -> Symbol.RESET
                            else -> throw LocalizedException.of("not-viewer-method", type)
                        }
                        ActionViewer(symbol, if (symbol != Symbol.RESET) t.nextElement() else null) as QuestAction<T, C>
                    }.apply(resolver)
                }

                override fun complete(parms: List<String>): List<String> {
                    return KetherCompleters.seq(KetherCompleters.consume()).apply(parms)
                }
            }
        }
    }
}