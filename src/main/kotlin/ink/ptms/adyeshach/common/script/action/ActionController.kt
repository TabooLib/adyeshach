package ink.ptms.adyeshach.common.script.action

import ink.ptms.adyeshach.common.script.ScriptHandler
import ink.ptms.adyeshach.common.script.ScriptHandler.entitySelected
import ink.ptms.adyeshach.common.script.ScriptHandler.getEntities
import ink.ptms.adyeshach.common.script.ScriptHandler.getManager
import ink.ptms.adyeshach.common.script.ScriptHandler.loadError
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

/**
 * @author IzzelAliz
 */
class ActionController(val symbol: Symbol, val controller: String?) : ScriptAction<Void>() {

    enum class Symbol {

        ADD, REMOVE, RESET
    }

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        val script = frame.script()
        if (script.getManager() == null || !script.entitySelected()) {
            error("Manager or Entity is not selected")
        }
        script.getEntities()?.forEach {
            when (symbol) {
                Symbol.ADD -> {
                    val controller = ScriptHandler.getControllerGenerator(controller!!) ?: error("Unknown controller $controller")
                    it?.registerController(controller.generator.apply(it))
                }
                Symbol.REMOVE -> {
                    val controller = ScriptHandler.getControllerGenerator(controller!!) ?: error("Unknown controller $controller")
                    it?.unregisterController(controller.type)
                }
                Symbol.RESET -> it?.resetController()
            }
        }
        return CompletableFuture.completedFuture(null)
    }

    companion object {

        @KetherParser(["controller"], namespace = "adyeshach", shared = true)
        fun parser() = scriptParser {
            val symbol = when (val type = it.nextToken()) {
                "add" -> Symbol.ADD
                "remove" -> Symbol.REMOVE
                "reset" -> Symbol.RESET
                else -> throw loadError("Unknown controller operator $type")
            }
            ActionController(symbol, if (symbol != Symbol.RESET) it.nextToken() else null)
        }
    }
}