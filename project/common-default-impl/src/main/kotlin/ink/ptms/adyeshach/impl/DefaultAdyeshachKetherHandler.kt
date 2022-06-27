package ink.ptms.adyeshach.impl

import ink.ptms.adyeshach.common.api.AdyeshachKetherHandler
import org.bukkit.entity.Player
import java.util.concurrent.CompletableFuture

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.DefaultAdyeshachKetherHandler
 *
 * @author 坏黑
 * @since 2022/6/28 00:05
 */
class DefaultAdyeshachKetherHandler : AdyeshachKetherHandler {

    override fun invoke(source: String, player: Player, vars: Map<String, Any>): CompletableFuture<Any?> {
        TODO("Not yet implemented")
    }

    override fun parseInline(source: String, player: Player): String {
        TODO("Not yet implemented")
    }
}