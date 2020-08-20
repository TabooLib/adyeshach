package ink.ptms.adyeshach

import ink.ptms.adyeshach.common.entity.ai.general.GeneralGravity
import ink.ptms.adyeshach.common.entity.type.AdyHorse
import ink.ptms.adyeshach.common.util.Tasks
import io.izzel.taboolib.module.command.lite.CommandBuilder
import io.izzel.taboolib.module.inject.TInject
import org.bukkit.entity.Horse
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

            val horse = AdyHorse()
            horse.addViewer(sender)
            horse.spawn(sender.location)

            horse.pathfinder.add(GeneralGravity(horse))

            Tasks.delay(100, true) {
                horse.destroy()
            }

            sender.sendMessage("done.")
        }
}