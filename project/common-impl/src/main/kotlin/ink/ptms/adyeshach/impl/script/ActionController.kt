package ink.ptms.adyeshach.impl.script

import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.util.errorBy
import ink.ptms.adyeshach.impl.getEntities
import ink.ptms.adyeshach.impl.getManager
import ink.ptms.adyeshach.impl.isEntitySelected
import ink.ptms.adyeshach.impl.loadError
import ink.ptms.adyeshach.impl.throwUndefinedError
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

/**
 * @author sky
 */
class ActionController(val symbol: Symbol, val controller: String?) : ScriptAction<Void>() {

    enum class Symbol {

        ADD, REMOVE, RESET
    }

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        val script = frame.script()
        if (script.getManager() == null || !script.isEntitySelected()) {
            script.throwUndefinedError()
        }
        var id = controller
        // 忽略无效的 v1 控制器
        if (id == "Move" || id == "Gravity" || id == "SmoothLook" || id == "RandomStrollLand") {
            return CompletableFuture.completedFuture(null)
        }
        // 转换有效的 v1 控制器
        when (id) {
            "LookAtPlayer" -> id = "LOOK_AT_PLAYER"
            "LookAtPlayerAlways" -> id = "LOOK_AT_PLAYER_ALWAYS"
            "LookAtPlayerWithPacket" -> id = "LOOK_AT_PLAYER_WITH_PACKET"
            "RandomLookGround" -> id = "RANDOM_LOOKAROUND"
        }
        val registry = Adyeshach.api().getEntityControllerRegistry()
        script.getEntities().forEach {
            when (symbol) {
                Symbol.ADD -> {
                    val controller = registry.getControllerGenerator(id!!) ?: error("Unknown controller $id")
                    it.registerController(controller.generate(it))
                }
                Symbol.REMOVE -> {
                    it.unregisterController(id!!)
                }
                Symbol.RESET -> it.resetController()
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