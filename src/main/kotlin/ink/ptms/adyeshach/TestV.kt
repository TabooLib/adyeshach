package ink.ptms.adyeshach

import io.izzel.taboolib.module.command.lite.CommandBuilder
import io.izzel.taboolib.module.inject.TInject
import io.izzel.taboolib.util.lite.Materials
import io.izzel.taboolib.util.lite.Signs
import org.bukkit.Location
import org.bukkit.entity.Player

/**
 * @author Arasple
 * @date 2020/8/3 21:55
 * Villager test
 */
object TestV {

    @TInject
    val testV: CommandBuilder = CommandBuilder
        .create("test-v", Adyeshach.plugin)
        .execute { sender, _ ->
            sender as Player

            sender.sendMessage("done. ${System.currentTimeMillis() - 1}")
        }
}