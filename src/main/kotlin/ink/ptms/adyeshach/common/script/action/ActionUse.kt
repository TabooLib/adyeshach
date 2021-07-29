package ink.ptms.adyeshach.common.script.action

import ink.ptms.adyeshach.api.AdyeshachAPI
import ink.ptms.adyeshach.common.script.ScriptHandler.setManager
import org.bukkit.entity.Player
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

/**
 * @author IzzelAliz
 */
class ActionUse(val manager: String, val temporary: Boolean): ScriptAction<Void>() {

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        val s = frame.script()
        if (manager == "private" && s.sender !is Player) {
            error("The private manager required a player viewer.")
        }
        s.setManager(when (manager) {
            "public" -> {
                if (temporary) {
                    AdyeshachAPI.getEntityManagerPublicTemporary()
                } else {
                    AdyeshachAPI.getEntityManagerPublic()
                }
            }
            "private" -> {
                if (temporary) {
                    AdyeshachAPI.getEntityManagerPrivateTemporary(s.sender as Player)
                } else {
                    AdyeshachAPI.getEntityManagerPrivate(s.sender as Player)
                }
            }
            else -> null
        })
        return CompletableFuture.completedFuture(null)
    }

    internal object Parser {

        @KetherParser(["use"], namespace = "adyeshach", shared = true)
        fun parser() = scriptParser {
            val manager = it.nextToken()
            var temporary = false
            if (it.hasNext()) {
                it.mark()
                if (it.nextToken() in listOf("temp", "temporary")) {
                    temporary = true
                } else {
                    it.reset()
                }
            }
            ActionUse(manager, temporary)
        }
    }
}