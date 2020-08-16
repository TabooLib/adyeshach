package ink.ptms.adyeshach

import ink.ptms.adyeshach.api.AdyeshachAPI
import ink.ptms.adyeshach.common.entity.EntityTypes
import ink.ptms.adyeshach.common.entity.type.AdyItemFrame
import ink.ptms.adyeshach.common.util.Tasks
import io.izzel.taboolib.module.command.lite.CommandBuilder
import io.izzel.taboolib.module.inject.TInject
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
            val v = AdyeshachAPI.getEntityManager().create(EntityTypes.ITEM_FRAME, (sender as Player).location) as AdyItemFrame
            v.setItem(sender.inventory.itemInMainHand)
            v.addViewer(sender)
            v.spawn(sender.location)

            Tasks.delay(20, true) {
                var delay = 0L
                (0..25).forEach { _ ->
                    Tasks.delay(delay, true) {
                        v.setRotation(v.getRotation() + 10)
                    }
                    delay += 10
                }
            }

            Tasks.delay(100, true) {
                v.destroy()
            }
        }

}