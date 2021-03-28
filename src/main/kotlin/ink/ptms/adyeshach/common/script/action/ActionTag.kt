package ink.ptms.adyeshach.common.script.action

import ink.ptms.adyeshach.common.script.ScriptHandler
import ink.ptms.adyeshach.common.script.ScriptHandler.entitySelected
import ink.ptms.adyeshach.common.script.ScriptHandler.getEntities
import ink.ptms.adyeshach.common.script.ScriptHandler.getManager
import io.izzel.taboolib.kotlin.kether.ScriptContext
import io.izzel.taboolib.kotlin.kether.ScriptParser
import io.izzel.taboolib.kotlin.kether.common.api.QuestAction
import io.izzel.taboolib.kotlin.kether.common.api.QuestContext
import java.util.concurrent.CompletableFuture

/**
 * @author IzzelAliz
 */
class ActionTag(val key: String, val symbol: Symbol, val value: String?) : QuestAction<Any?>() {

    enum class Symbol {

        GET, SET, REMOVE, HAS
    }

    @Suppress("UNCHECKED_CAST")
    override fun process(context: QuestContext.Frame): CompletableFuture<Any?> {
        val s = (context.context() as ScriptContext)
        if (s.getManager() == null) {
            throw RuntimeException("No manager selected.")
        }
        if (!s.entitySelected()) {
            throw RuntimeException("No entity selected.")
        }
        return when (symbol) {
            Symbol.REMOVE -> {
                s.getEntities()!!.filterNotNull().forEach {
                    it.removeTag(key)
                }
                CompletableFuture.completedFuture(null)
            }
            Symbol.SET -> {
                s.getEntities()!!.filterNotNull().forEach {
                    if (value!! == "null") {
                        it.removeTag(key)
                    } else {
                        it.setTag(key, value)
                    }
                }
                CompletableFuture.completedFuture(null)
            }
            Symbol.HAS -> {
                CompletableFuture.completedFuture(s.getEntities()!!.filterNotNull().firstOrNull()?.hasTag(key))
            }
            else -> {
                CompletableFuture.completedFuture(s.getEntities()!!.filterNotNull().firstOrNull()?.getTag(key))
            }
        }
    }

    override fun toString(): String {
        return "ActionTag(key='$key', symbol=$symbol, value=$value)"
    }

    companion object {

        fun parser() = ScriptParser.parser {
            val symbol = when (val type = it.nextToken()) {
                "set" -> Symbol.SET
                "get" -> Symbol.GET
                "has" -> Symbol.HAS
                "remove" -> Symbol.REMOVE
                else -> throw ScriptHandler.loadError("Unknown tag operator $type")
            }
            val key = it.nextToken()
            val value = if (symbol == Symbol.SET) {
                it.expect("to")
                it.nextToken()
            } else null
            ActionTag(key, symbol, value)
        }
    }
}