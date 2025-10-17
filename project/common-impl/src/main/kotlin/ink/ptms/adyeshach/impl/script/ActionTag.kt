package ink.ptms.adyeshach.impl.script

import ink.ptms.adyeshach.core.util.errorBy
import ink.ptms.adyeshach.impl.*
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

/**
 * @author sky
 */
class ActionTag(val persistent: Boolean, val key: String, val symbol: Symbol, val value: String?) : ScriptAction<Any?>() {

    enum class Symbol {

        GET, SET, REMOVE, HAS
    }

    @Suppress("UNCHECKED_CAST")
    override fun run(frame: ScriptFrame): CompletableFuture<Any?> {
        val script = frame.script()
        if (script.getManager() == null || !script.isEntitySelected()) {
            script.throwUndefinedError()
        }
        return when (symbol) {
            Symbol.REMOVE -> {
                script.getEntities().forEach { e -> if (persistent) e.removePersistentTag(key) else e.removeTag(key) }
                CompletableFuture.completedFuture(null)
            }
            Symbol.SET -> {
                script.getEntities().forEach { e ->
                    if (value!! == "null") {
                        if (persistent) e.removePersistentTag(key) else e.removeTag(key)
                    } else {
                        if (persistent) e.setPersistentTag(key, value) else e.setTag(key, value)
                    }
                }
                CompletableFuture.completedFuture(null)
            }
            else -> {
                CompletableFuture.completedFuture(if (persistent) script.getEntity().hasPersistentTag(key) else script.getEntity().hasTag(key))
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