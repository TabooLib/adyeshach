package ink.ptms.adyeshach.compat.dragoncore

import eos.moe.dragoncore.network.PacketSender
import ink.ptms.adyeshach.core.util.errorBy
import ink.ptms.adyeshach.impl.getEntities
import ink.ptms.adyeshach.impl.getManager
import ink.ptms.adyeshach.impl.isEntitySelected
import ink.ptms.adyeshach.impl.script.getBukkitPlayer
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

class ActionDragonCore(val state: String, val remove: Boolean, val transferTime: Int, val self: ParsedAction<*>) : ScriptAction<Void>() {

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {

        val script = frame.script()

        if (script.getManager() == null || !script.isEntitySelected()) {
            errorBy("error-no-manager-or-entity-selected")
        }

        val player = frame.getBukkitPlayer()

        frame.run(self).bool { self ->
            script.getEntities().forEach {

                if (self) {
                    if (remove) {
                        PacketSender.removeModelEntityAnimation(player, it.normalizeUniqueId, state, transferTime)
                    } else {
                        PacketSender.setModelEntityAnimation(player, it.normalizeUniqueId, state, transferTime)
                    }
                } else {
                    if (remove) {
                        it.forViewers { p -> PacketSender.removeModelEntityAnimation(p, it.normalizeUniqueId, state, transferTime) }
                    } else {
                        it.forViewers { p -> PacketSender.setModelEntityAnimation(p, it.normalizeUniqueId, state, transferTime) }
                    }
                }

            }

        }


        return CompletableFuture.completedFuture(null)
    }

    companion object {

        @KetherParser(["dragoncore"], namespace = "adyeshach", shared = true)
        fun parser() = scriptParser {
            it.switch {
                case("animation") {
                    when (it.expects("send", "stop")) {
                        "send" -> ActionDragonCore(it.nextToken(), false, it.nextInt(), it.nextParsedAction())
                        "stop" -> ActionDragonCore(it.nextToken(), true, it.nextInt(), it.nextParsedAction())
                        else -> error("out of case")
                    }
                }
            }
        }
    }
}
