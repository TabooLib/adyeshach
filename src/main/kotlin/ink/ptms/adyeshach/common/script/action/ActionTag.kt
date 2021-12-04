package ink.ptms.adyeshach.common.script.action

import ink.ptms.adyeshach.common.script.ScriptHandler.entitySelected
import ink.ptms.adyeshach.common.script.ScriptHandler.getEntities
import ink.ptms.adyeshach.common.script.ScriptHandler.getManager
import ink.ptms.adyeshach.common.script.ScriptHandler.loadError
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

/**
 * @author IzzelAliz
 */
class ActionTag(val key: String, val symbol: Symbol, val value: String?): ScriptAction<Any?>() {

    enum class Symbol {

        GET, SET, REMOVE, HAS
    }

    @Suppress("UNCHECKED_CAST")
    override fun run(frame: ScriptFrame): CompletableFuture<Any?> {
        val s = frame.script()
        if (s.getManager() == null) {
            error("No manager selected.")
        }
        if (!s.entitySelected()) {
            error("No entity selected.")
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

    companion object {

        @KetherParser(["tag"], namespace = "adyeshach", shared = true)
        fun parser() = scriptParser {
            val symbol = when (val type = it.nextToken()) {
                "set" -> Symbol.SET
                "get" -> Symbol.GET
                "has" -> Symbol.HAS
                "remove" -> Symbol.REMOVE
                else -> throw loadError("Unknown tag operator $type")
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