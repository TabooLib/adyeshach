package ink.ptms.adyeshach.impl.script

import ink.ptms.adyeshach.core.util.errorBy
import ink.ptms.adyeshach.impl.getEntities
import ink.ptms.adyeshach.impl.getManager
import ink.ptms.adyeshach.impl.loadError
import ink.ptms.adyeshach.impl.setEntities
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

/**
 * @author IzzelAliz
 */
class ActionSelect(val value: ParsedAction<*>, val byId: Boolean) : ScriptAction<Void>() {

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        val script = frame.script()
        if (script.getManager() == null) {
            errorBy("error-no-manager-selected")
        }
        return frame.newFrame(value).run<Any>().thenAccept {
            val manager = script.getManager()!!
            script.setEntities(if (byId) {
                manager.getEntityById(it.toString())
            } else {
                manager.getEntityByUniqueId(it.toString())?.let { e -> listOf(e) } ?: emptyList()
            })
        }
    }

    companion object {

        @KetherParser(["select"], namespace = "adyeshach", shared = true)
        fun parser1() = scriptParser {
            val value = it.next(ArgTypes.ACTION)
            var byId = true
            if (it.hasNext()) {
                it.mark()
                if (it.nextToken() == "by" && it.hasNext()) {
                    byId = when (val type = it.nextToken().lowercase()) {
                        "id" -> true
                        "uniqueid", "uuid" -> false
                        else -> throw loadError("Unknown select type $type")
                    }
                } else {
                    it.reset()
                }
            }
            ActionSelect(value, byId)
        }

        @KetherParser(["selected"], namespace = "adyeshach", shared = true)
        fun parser2() = scriptParser {
            actionNow {
                val npc = script().getEntities()
                when {
                    npc.isEmpty() -> null
                    npc.size == 1 -> npc.first().id
                    else -> npc.map { it.id }
                }
            }
        }
    }
}