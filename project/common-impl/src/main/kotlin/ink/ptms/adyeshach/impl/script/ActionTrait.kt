package ink.ptms.adyeshach.impl.script

import ink.ptms.adyeshach.core.util.errorBy
import ink.ptms.adyeshach.impl.entity.trait.impl.*
import ink.ptms.adyeshach.impl.getEntities
import ink.ptms.adyeshach.impl.getManager
import ink.ptms.adyeshach.impl.isEntitySelected
import ink.ptms.adyeshach.impl.throwUndefinedError
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

/**
 * @author sky
 */
class ActionTrait(val type: Int, val source: List<ParsedAction<*>>) : ScriptAction<Void>() {

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        val future = CompletableFuture<List<String>>()
        val script = frame.script()
        if (script.getManager() == null || !script.isEntitySelected()) {
            script.throwUndefinedError()
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
        process(0)
        return future.thenAccept {
            when (type) {
                1 -> script.getEntities().forEach { it.setTraitTitle(array) }
                2 -> script.getEntities().forEach { it.setTraitViewCondition(array) }
                3 -> script.getEntities().forEach { it.setTraitCommands(array) }
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
                case("title-height") {
                    expect("to")
                    val bool = it.nextParsedAction()
                    actionNow {
                        if (script().getManager() == null || !script().isEntitySelected()) {
                            script().throwUndefinedError()
                        }
                        run(bool).double { height -> script().getEntities().forEach { e -> e.setTraitTitleHeight(height) } }
                    }
                }
                case("view-condition") {
                    expect("to")
                    ActionTrait(2, it.next(ArgTypes.listOf(ArgTypes.ACTION)))
                }
                case("command") {
                    expect("to")
                    ActionTrait(3, it.next(ArgTypes.listOf(ArgTypes.ACTION)))
                }
                case("sit") {
                    expect("to")
                    val bool = it.nextParsedAction()
                    actionNow {
                        if (script().getManager() == null || !script().isEntitySelected()) {
                            script().throwUndefinedError()
                        }
                        run(bool).bool { b ->
                            if (b) {
                                script().getEntities().forEach { e -> e.setTraitSit(true) }
                            } else {
                                script().getEntities().forEach { e -> e.setTraitSit(false) }
                            }
                        }
                    }
                }
            }
        }
    }
}