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
class ActionTag(val persistent: Boolean, val key: String, val symbol: Symbol, val value: String?) : ScriptAction<Any?>() {

    enum class Symbol {

        GET, SET, REMOVE, HAS
    }

    @Suppress("UNCHECKED_CAST")
    override fun run(frame: ScriptFrame): CompletableFuture<Any?> {
        val script = frame.script()
        if (script.getManager() == null || !script.entitySelected()) {
            error("Manager or Entity is not selected")
        }
        return when (symbol) {
            Symbol.REMOVE -> {
                script.getEntities()?.forEach { if (persistent) it?.removePersistentTag(key) else it?.removeTag(key) }
                CompletableFuture.completedFuture(null)
            }
            Symbol.SET -> {
                script.getEntities()?.forEach {
                    if (value!! == "null") {
                        if (persistent) it?.removePersistentTag(key) else it?.removeTag(key)
                    } else {
                        if (persistent) it?.setPersistentTag(key, value) else it?.setTag(key, value)
                    }
                }
                CompletableFuture.completedFuture(null)
            }
            else -> {
                val en = script.getEntities()!!.filterNotNull().firstOrNull()
                CompletableFuture.completedFuture(if (persistent) en?.hasPersistentTag(key) else en?.hasTag(key))
            }
        }
    }

    companion object {

        /**
         * tag [persistent] set a to 1
         */
        @KetherParser(["tag"], namespace = "adyeshach", shared = true)
        fun parser() = scriptParser {
            it.mark()
            val persistent = try {
                it.expect("persistent")
                true
            } catch (ex: Throwable) {
                it.reset()
                false
            }
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
            ActionTag(persistent, key, symbol, value)
        }
    }
}