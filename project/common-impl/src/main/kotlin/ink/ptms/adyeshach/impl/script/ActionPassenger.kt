package ink.ptms.adyeshach.impl.script

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
class ActionPassenger(val symbol: Symbol, val passenger: String?) : ScriptAction<Void>() {

    enum class Symbol {

        ADD, REMOVE, RESET
    }

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        val script = frame.script()
        if (script.getManager() == null || !script.isEntitySelected()) {
            script.throwUndefinedError()
        }
        script.getEntities().forEach {
            when (symbol) {
                Symbol.ADD -> {
                    script.getManager()!!.getEntityById(passenger!!).forEach { e -> it.addPassenger(e) }
                }
                Symbol.REMOVE -> {
                    it.getPassengers().filter { e -> e.id == passenger }.forEach { e -> it.removePassenger(e) }
                }
                Symbol.RESET -> it.clearPassengers()
            }
        }
        return CompletableFuture.completedFuture(null)
    }

    companion object {

        @KetherParser(["passenger"], namespace = "adyeshach", shared = true)
        fun parser() = scriptParser {
            val symbol = when (val type = it.nextToken()) {
                "add" -> Symbol.ADD
                "remove" -> Symbol.REMOVE
                "reset" -> Symbol.RESET
                else -> throw loadError("Unknown passenger operator $type")
            }
            ActionPassenger(symbol, if (symbol != Symbol.RESET) it.nextToken() else null)
        }
    }
}