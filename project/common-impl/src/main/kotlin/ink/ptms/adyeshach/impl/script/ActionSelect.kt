package ink.ptms.adyeshach.impl.script

import ink.ptms.adyeshach.core.event.AdyeshachScriptEvent
import ink.ptms.adyeshach.core.util.errorBy
import ink.ptms.adyeshach.impl.getEntities
import ink.ptms.adyeshach.impl.getManager
import ink.ptms.adyeshach.impl.loadError
import ink.ptms.adyeshach.impl.setEntities
import ink.ptms.adyeshach.impl.setEntitySelectId
import org.bukkit.entity.Player
import taboolib.common.util.isPlayer
import taboolib.library.kether.ArgTypes
import taboolib.module.kether.*

/**
 * - select test
 * - select test by id
 * - select test in player world
 */
@KetherParser(["select"], namespace = "adyeshach", shared = true)
private fun actionSelect() = scriptParser {
    val value = it.next(ArgTypes.ACTION)
    var byId = true
    try {
        it.mark()
        it.expect("by")
        byId = when (val type = it.nextToken().lowercase()) {
            "id" -> true
            "uniqueid", "uuid" -> false
            else -> throw loadError("Unknown select type $type")
        }
    } catch (_: Throwable) {
        it.reset()
    }
    var world = literalAction("*")
    try {
        it.mark()
        it.expect("in")
        world = it.nextParsedAction()
    } catch (_: Throwable) {
        it.reset()
    }
    actionFuture { f ->
        val script = script()
        if (script.getManager() == null) {
            errorBy("error-no-manager-selected")
        }
        run(value).str { id ->
            run(world).str { world ->
                var entities = if (byId) {
                    script.getManager()!!.getEntityById(id)
                } else {
                    script.getManager()!!.getEntityByUniqueId(id)?.let { e -> listOf(e) } ?: emptyList()
                }
                if (world != "*") {
                    entities = entities.filter { e -> e.world.name == world }
                }
                val sender = if (script.sender?.isPlayer() == true) script.sender!!.cast<Player>() else null
                val event = AdyeshachScriptEvent.Select(entities.toMutableList(), script.getManager()!!, sender, id, world, byId)
                if (event.call()) {
                    script.setEntities(event.entity)
                    script.setEntitySelectId(id)
                }
                f.complete(null)
            }
        }
    }
}

@KetherParser(["selected"], namespace = "adyeshach", shared = true)
private fun actionSelected() = scriptParser {
    actionNow {
        val npc = script().getEntities()
        when {
            npc.isEmpty() -> null
            npc.size == 1 -> npc.first().id
            else -> npc.map { it.id }
        }
    }
}