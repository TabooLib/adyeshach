package ink.ptms.adyeshach

import ink.ptms.adyeshach.api.AdyeshachAPI
import ink.ptms.adyeshach.common.entity.EntityTypes
import ink.ptms.adyeshach.common.entity.ai.general.GeneralGravity
import ink.ptms.adyeshach.common.entity.ai.general.GeneralMove
import ink.ptms.adyeshach.common.entity.ai.general.GeneralSmoothLook
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
            sender as Player

            val time = System.currentTimeMillis()

            val entity = AdyeshachAPI.getEntityManagerPublicTemporary().create(EntityTypes.VILLAGER, sender.location)

            entity.pathfinder.add(GeneralMove(entity))
            entity.pathfinder.add(GeneralGravity(entity))
            entity.pathfinder.add(GeneralSmoothLook(entity))

            Tasks.delay(40, true) {
                entity.controllerMove(sender.location)
                entity.controllerLook(sender.location)
            }

            Tasks.delay(300, true) {
                entity.delete()
            }
            sender.sendMessage("done. ${System.currentTimeMillis() - time}")
        }
}