package ink.ptms.adyeshach.common.script.action.npc

import ink.ptms.adyeshach.common.script.ScriptContext
import ink.ptms.adyeshach.common.script.ScriptHandler
import ink.ptms.adyeshach.common.script.ScriptParser
import io.izzel.kether.common.api.QuestAction
import io.izzel.kether.common.api.QuestContext
import io.izzel.kether.common.util.LocalizedException
import java.util.concurrent.CompletableFuture

/**
 * @author IzzelAliz
 */
class ActionController(val symbol: Symbol, val controller: String?) : QuestAction<Void>() {

    enum class Symbol {

        ADD, REMOVE, RESET
    }

    override fun process(context: QuestContext.Frame): CompletableFuture<Void> {
        val s = (context.context() as ScriptContext)
        if (s.manager == null) {
            throw RuntimeException("No manager selected.")
        }
        if (!s.entitySelected()) {
            throw RuntimeException("No entity selected.")
        }
        s.entities!!.filterNotNull().forEach {
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

    override fun toString(): String {
        return "ActionController(symbol=$symbol, controller='$controller')"
    }

    companion object {

        fun parser() = ScriptParser.parser {
            val symbol = when (val type = it.nextToken()) {
                "add" -> Symbol.ADD
                "remove" -> Symbol.REMOVE
                "reset" -> Symbol.RESET
                else -> throw LocalizedException.of("not-controller-method", type)
            }
            ActionController(symbol, if (symbol != Symbol.RESET) it.nextToken() else null)
        }
    }
}