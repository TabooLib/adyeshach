package ink.ptms.adyeshach

import ink.ptms.adyeshach.api.AdyeshachAPI
import ink.ptms.adyeshach.api.nms.NMS
import ink.ptms.adyeshach.common.entity.EntityTypes
import ink.ptms.adyeshach.common.entity.ai.general.GeneralGravity
import ink.ptms.adyeshach.common.entity.ai.general.GeneralMove
import ink.ptms.adyeshach.common.util.Tasks
import io.izzel.taboolib.module.command.lite.CommandBuilder
import io.izzel.taboolib.module.inject.TInject
import net.minecraft.server.v1_12_R1.BlockPosition
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld
import org.bukkit.craftbukkit.v1_12_R1.block.CraftBlock
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.entity.Villager

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

            val manager = AdyeshachAPI.getEntityManagerPublicTemporary()
            val spider = manager.create(EntityTypes.SPIDER, sender.location)
            val villager = manager.create(EntityTypes.SKELETON, sender.location.add(2.0, 0.0, 2.0))

            Tasks.delay(20) {
                spider.addPassenger(villager)
            }
            Tasks.delay(40) {
                spider.pathfinder.add(GeneralMove(spider))
                spider.pathfinder.add(GeneralGravity(spider))
                spider.controllerMove(sender.location)
            }
            Tasks.delay(100) {
                spider.removePassenger(villager)
            }

            sender.sendMessage("done. ")
        }
}