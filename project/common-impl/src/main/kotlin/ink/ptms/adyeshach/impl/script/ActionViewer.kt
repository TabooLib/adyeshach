package ink.ptms.adyeshach.impl.script

import ink.ptms.adyeshach.core.entity.type.errorBy
import ink.ptms.adyeshach.impl.getEntities
import ink.ptms.adyeshach.impl.getManager
import ink.ptms.adyeshach.impl.isEntitySelected
import ink.ptms.adyeshach.impl.loadError
import ink.ptms.adyeshach.impl.throwUndefinedError
import org.bukkit.Bukkit
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

/**
 * @author sky
 */
class ActionViewer(val symbol: Symbol, val viewer: ParsedAction<*>?) : ScriptAction<Void>() {

    enum class Symbol {

        ADD, REMOVE, RESET
    }

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        val script = frame.script()
        if (script.getManager() == null || !script.isEntitySelected()) {
            script.throwUndefinedError()
        }
        if (viewer == null || symbol == Symbol.RESET) {
            script.getEntities().forEach { it.clearViewer() }
        } else {
            frame.newFrame(viewer).run<Any>().thenAccept { viewer ->
                script.getEntities().forEach {
                    when (symbol) {
                        Symbol.ADD -> Bukkit.getPlayerExact(viewer.toString())?.apply { it.addViewer(this) }
                        Symbol.REMOVE -> Bukkit.getPlayerExact(viewer.toString())?.apply { it.removeViewer(this) }
                        else -> {}
                    }
                }
            }
        }
        return CompletableFuture.completedFuture(null)
    }

    companion object {

        @KetherParser(["viewer"], namespace = "adyeshach", shared = true)
        fun parser() = scriptParser {
            val symbol = when (val type = it.nextToken()) {
                "add" -> Symbol.ADD
                "remove" -> Symbol.REMOVE
                "reset" -> Symbol.RESET
                else -> throw loadError("Unknown viewer operator $type")
            }
            ActionViewer(symbol, if (symbol != Symbol.RESET) it.next(ArgTypes.ACTION) else null)
        }

        @KetherParser(["viewers"], namespace = "adyeshach", shared = true)
        fun parser2() = scriptParser {
            actionNow {
                if (script().getManager() == null || !script().isEntitySelected()) {
                    script().throwUndefinedError()
                }
                script().getEntities().first().viewPlayers.getViewPlayers().map { it.name }
            }
        }
    }
}