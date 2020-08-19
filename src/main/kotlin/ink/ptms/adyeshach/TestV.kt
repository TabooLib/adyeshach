package ink.ptms.adyeshach

import ink.ptms.adyeshach.common.entity.type.AdySheep
import ink.ptms.adyeshach.common.util.Tasks
import io.izzel.taboolib.module.command.lite.CommandBuilder
import io.izzel.taboolib.module.inject.TInject
import org.bukkit.DyeColor
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

            val sheep = AdySheep()
            sheep.addViewer(sender)
            sheep.spawn(sender.location)

            Tasks.delay(20, true) { sheep.setDyeColor(DyeColor.PINK) }
            Tasks.delay(40, true) { sheep.setDyeColor(DyeColor.GREEN) }
            Tasks.delay(60, true) { sheep.setDyeColor(DyeColor.BLUE) }
            Tasks.delay(80, true) { sheep.setDyeColor(DyeColor.RED) }
            Tasks.delay(100, true) { sheep.setSheared(true) }
            Tasks.delay(200, true) { sheep.destroy() }
        }

}