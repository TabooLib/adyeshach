package ink.ptms.adyeshach.common.script.action

import ink.ptms.adyeshach.api.AdyeshachAPI
import ink.ptms.adyeshach.common.script.ScriptHandler.setManager
import taboolib.common.platform.ProxyPlayer
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

/**
 * @author IzzelAliz
 */
class ActionUse(val manager: String, val temporary: Boolean): ScriptAction<Void>() {

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        val script = frame.script()
        if (manager == "private" && script.sender !is ProxyPlayer) {
            error("The private manager required a player viewer.")
        }
        script.setManager(when (manager) {
            "public" -> {
                if (temporary) {
                    AdyeshachAPI.getEntityManagerPublicTemporary()
                } else {
                    AdyeshachAPI.getEntityManagerPublic()
                }
            }
            "private" -> {
                if (temporary) {
                    AdyeshachAPI.getEntityManagerPrivateTemporary(script.sender!!.cast())
                } else {
                    AdyeshachAPI.getEntityManagerPrivate(script.sender!!.cast())
                }
            }
            else -> null
        })
        return CompletableFuture.completedFuture(null)
    }

    companion object {

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