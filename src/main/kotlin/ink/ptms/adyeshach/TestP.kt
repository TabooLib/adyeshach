package ink.ptms.adyeshach

import io.izzel.taboolib.module.command.lite.CommandBuilder
import io.izzel.taboolib.module.inject.TInject
import org.bukkit.entity.Player

/**
 * @author Arasple
 * @date 2020/8/3 22:34
 * Player (NPC) test
 */
@Suppress("UNCHECKED_CAST")
object TestP {

    @TInject
    val testV: CommandBuilder = CommandBuilder
            .create("test-p", Adyeshach.plugin)
            .execute { sender, _ ->
                if (sender is Player) {
                    sender.sendMessage("§c[System] §7Done.")
                }
            }
}