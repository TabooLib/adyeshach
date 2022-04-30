package ink.ptms.adyeshach.common.script.action

import ink.ptms.adyeshach.common.script.ScriptHandler.entitySelected
import ink.ptms.adyeshach.common.script.ScriptHandler.getEntities
import ink.ptms.adyeshach.common.script.ScriptHandler.getManager
import ink.ptms.adyeshach.internal.trait.impl.setTraitTitle
import ink.ptms.adyeshach.internal.trait.impl.setViewCondition
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

/**
 * @author IzzelAliz
 */
class ActionTrait(val type: Int, val source: List<ParsedAction<*>>) : ScriptAction<Void>() {

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        val future = CompletableFuture<List<String>>()
        val script = frame.script()
        if (script.getManager() == null || !script.entitySelected()) {
            error("Manager or Entity is not selected")
        }
        val array = ArrayList<String>()
        fun process(cur: Int) {
            if (cur < source.size) {
                frame.newFrame(source[cur]).run<Any>().thenApply {
                    array.add(it.toString())
                    process(cur + 1)
                }
            } else {
                future.complete(array)
            }
        }
        return future.thenAccept {
            if (type == 1) {
                script.getEntities()?.forEach { it?.setTraitTitle(array) }
            } else if (type == 2) {
                script.getEntities()?.forEach { it?.setViewCondition(array) }
            }
        }
    }

    companion object {

        /**
         * trait title to
         */
        @KetherParser(["trait"], namespace = "adyeshach", shared = true)
        fun parser() = scriptParser {
            it.switch {
                case("title") {
                    expect("to")
                    ActionTrait(1, it.next(ArgTypes.listOf(ArgTypes.ACTION)))
                }
                case("view-condition") {
                    expect("to")
                    ActionTrait(2, it.next(ArgTypes.listOf(ArgTypes.ACTION)))
                }
            }
        }
    }
}