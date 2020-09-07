package ink.ptms.adyeshach.common.script.action.npc

import ink.ptms.adyeshach.common.script.ScriptContext
import ink.ptms.adyeshach.common.script.ScriptHandler
import io.izzel.kether.common.api.*
import io.izzel.kether.common.util.LocalizedException
import java.util.concurrent.CompletableFuture
import java.util.function.Function

/**
 * @author IzzelAliz
 */
class ActionController(val symbol: Symbol, val controller: String?) : QuestAction<Void, ScriptContext> {

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
                    val controller = ScriptHandler.getKnownController(controller!!) ?: throw RuntimeException("Unknown controller $controller")
                    it.registerController(controller.get.invoke(it))
                }
                Symbol.REMOVE -> {
                    val controller = ScriptHandler.getKnownController(controller!!) ?: throw RuntimeException("Unknown controller $controller")
                    it.unregisterController(controller.controllerClass)
                }
                Symbol.RESET -> it.resetController()
            }
        }
        return CompletableFuture.completedFuture(null)
    }

    override fun getDataPrefix(): String {
        return "controller"
    }

    override fun toString(): String {
        return "ActionController(symbol=$symbol, controller='$controller')"
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
                            else -> throw LocalizedException.of("not-controller-method", type)
                        }
                        ActionController(symbol, if (symbol != Symbol.RESET) t.nextElement() else null) as QuestAction<T, C>
                    }.apply(resolver)
                }

                override fun complete(parms: List<String>): List<String> {
                    return KetherCompleters.seq(KetherCompleters.consume()).apply(parms)
                }
            }
        }
    }
}