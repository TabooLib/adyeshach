package ink.ptms.adyeshach.common.script.action

import ink.ptms.adyeshach.common.script.ScriptHandler
import ink.ptms.adyeshach.common.script.ScriptHandler.entitySelected
import ink.ptms.adyeshach.common.script.ScriptHandler.getEntities
import ink.ptms.adyeshach.common.script.ScriptHandler.getManager
import io.izzel.taboolib.kotlin.kether.ScriptContext
import io.izzel.taboolib.kotlin.kether.ScriptParser
import io.izzel.taboolib.kotlin.kether.common.api.QuestAction
import io.izzel.taboolib.kotlin.kether.common.api.QuestContext
import io.izzel.taboolib.kotlin.kether.common.util.LocalizedException
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
        if (s.getManager() == null) {
            throw RuntimeException("No manager selected.")
        }
        if (!s.entitySelected()) {
            throw RuntimeException("No entity selected.")
        }
        s.getEntities()!!.filterNotNull().forEach {
            when (symbol) {
                Symbol.ADD -> {
                    String
                    val controller = ScriptHandler.getKnownController(controller!!) ?: throw RuntimeException("Unknown controller $controller")
                    it.registerController(controller.get(it))
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
                else -> throw ScriptHandler.loadError("Unknown controller operator $type")
            }
            ActionController(symbol, if (symbol != Symbol.RESET) it.nextToken() else null)
        }
    }
}