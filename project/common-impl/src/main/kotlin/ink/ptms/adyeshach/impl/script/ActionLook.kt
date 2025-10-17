package ink.ptms.adyeshach.impl.script

import ink.ptms.adyeshach.core.event.AdyeshachScriptEvent
import ink.ptms.adyeshach.core.util.errorBy
import ink.ptms.adyeshach.core.util.submitRepeat
import ink.ptms.adyeshach.impl.getEntities
import ink.ptms.adyeshach.impl.getManager
import ink.ptms.adyeshach.impl.isEntitySelected
import ink.ptms.adyeshach.impl.throwUndefinedError
import org.bukkit.Location
import org.bukkit.entity.Player
import taboolib.common.util.isPlayer
import taboolib.module.kether.KetherParser
import taboolib.module.kether.combinationParser
import taboolib.module.kether.script

@KetherParser(["look"], namespace = "adyeshach", shared = true)
private fun actionLook() = combinationParser {
    it.group(
        expects("smooth", "delayed", "waiting"),
        command("to", then = any()).option(),
        command("x", then = command("to", then = double())).option(),
        command("y", then = command("to", then = double())).option(),
        command("z", then = command("to", then = double())).option(),
    ).apply(it) { smooth, to, x, y, z ->
        now {
            val script = script()
            if (script.getManager() == null || !script.isEntitySelected()) {
                script.throwUndefinedError()
            }
            val sender = if (script.sender?.isPlayer() == true) script.sender!!.cast<Player>() else null
            val entities = script.getEntities()
            if (AdyeshachScriptEvent.Look(entities, sender, smooth != null, to as? Location, x, y, z).call()) {
                if (smooth != null) {
                    submitRepeat(5) {
                        entities.forEach { e ->
                            val lookAt = to as? Location ?: Location(e.world, x ?: e.x, y ?: e.y, z ?: e.z)
                            e.controllerLookAt(lookAt.x, lookAt.y, lookAt.z, 35f, 40f)
                        }
                    }
                } else {
                    entities.forEach { e -> e.setHeadRotation(to as? Location ?: Location(e.world, x ?: e.x, y ?: e.y, z ?: e.z)) }
                }
            }
        }
    }
}