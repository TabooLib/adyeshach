package ink.ptms.adyeshach.impl

import ink.ptms.adyeshach.core.AdyeshachKetherHandler
import org.bukkit.entity.Player
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.PlatformFactory
import taboolib.common.platform.function.adaptPlayer
import taboolib.common.platform.function.console
import taboolib.module.kether.KetherFunction
import taboolib.module.kether.KetherShell
import java.util.concurrent.CompletableFuture

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.DefaultAdyeshachKetherHandler
 *
 * @author 坏黑
 * @since 2022/6/28 00:05
 */
class DefaultAdyeshachKetherHandler : AdyeshachKetherHandler {

    override fun invoke(source: String, player: Player?, vars: Map<String, Any>): CompletableFuture<Any?> {
        val map = KetherShell.VariableMap(*vars.map { it.key to it.value }.toTypedArray())
        return KetherShell.eval(source, sender = if (player != null) adaptPlayer(player) else console(), namespace = listOf("adyeshach"), vars = map)
    }

    override fun parseInline(source: String, player: Player?, vars: Map<String, Any>): String {
        val map = KetherShell.VariableMap(*vars.map { it.key to it.value }.toTypedArray())
        return KetherFunction.parse(source, sender = if (player != null) adaptPlayer(player) else console(), namespace = listOf("adyeshach"), vars = map)
    }

    companion object {

        @Awake(LifeCycle.CONST)
        fun init() {
            PlatformFactory.registerAPI<AdyeshachKetherHandler>(DefaultAdyeshachKetherHandler())
        }
    }
}