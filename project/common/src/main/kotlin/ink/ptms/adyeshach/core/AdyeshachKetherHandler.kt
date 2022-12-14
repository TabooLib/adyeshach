package ink.ptms.adyeshach.core

import org.bukkit.entity.Player
import java.util.concurrent.CompletableFuture

/**
 * Adyeshach
 * ink.ptms.adyeshach.core.AdyeshachKetherHandler
 *
 * @author 坏黑
 * @since 2022/6/16 17:31
 */
interface AdyeshachKetherHandler {

    /**
     * 执行 Kether 脚本
     */
    fun invoke(source: String, player: Player? = null, vars: Map<String, Any> = emptyMap()): CompletableFuture<Any?>

    /**
     * 识别并转换字符串中的 Kether 内敛脚本
     */
    fun parseInline(source: String, player: Player? = null, vars: Map<String, Any> = emptyMap()): String
}