package ink.ptms.adyeshach

import ink.ptms.adyeshach.api.AdyeshachAPI
import ink.ptms.adyeshach.common.entity.EntityTypes
import ink.ptms.adyeshach.common.entity.ai.expand.ControllerLookAtPlayer
import ink.ptms.adyeshach.common.entity.ai.expand.ControllerRandomLookaround
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
object AdyeshachTest {

    @TInject
    val test = CommandBuilder
        .create("test", Adyeshach.plugin)
        .execute { sender, _ ->
            sender as Player
            val npc = AdyeshachAPI.getEntityManagerPublicTemporary().create(EntityTypes.VILLAGER, sender.location)
            npc.registerController(GeneralMove(npc))
            npc.registerController(GeneralGravity(npc))
            npc.registerController(GeneralSmoothLook(npc))
            npc.registerController(ControllerLookAtPlayer(npc))
            npc.registerController(ControllerRandomLookaround(npc))
//            Tasks.delay(40) {
//                npc.controllerMove(sender.location)
//                sender.sendMessage("move. ")
//            }
            Tasks.delay(100) {
                npc.delete()
                sender.sendMessage("delete. ")
            }
            sender.sendMessage("done. ")
        }
}