package ink.ptms.adyeshach.impl.script

import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.entity.manager.ManagerType
import ink.ptms.adyeshach.core.util.errorBy
import ink.ptms.adyeshach.impl.setManager
import ink.ptms.adyeshach.impl.throwUndefinedError
import taboolib.common.platform.ProxyPlayer
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

/**
 * @author sky
 */
class ActionUse(val manager: String, val temporary: Boolean): ScriptAction<Void>() {

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        val script = frame.script()
        if (manager == "private" && script.sender !is ProxyPlayer) {
            script.throwUndefinedError()
        }
        script.setManager(when (manager) {
            "public" -> {
                if (temporary) {
                    Adyeshach.api().getPublicEntityManager(ManagerType.TEMPORARY)
                } else {
                    Adyeshach.api().getPublicEntityManager(ManagerType.PERSISTENT)
                }
            }
            "private" -> {
                if (temporary) {
                    Adyeshach.api().getPrivateEntityManager(script.sender!!.cast(), ManagerType.TEMPORARY)
                } else {
                    Adyeshach.api().getPrivateEntityManager(script.sender!!.cast(), ManagerType.PERSISTENT)
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