package ink.ptms.adyeshach

import ink.ptms.adyeshach.api.AdyeshachAPI
import ink.ptms.adyeshach.api.Settings
import ink.ptms.adyeshach.api.nms.NMS
import ink.ptms.adyeshach.common.entity.EntityTypes
import ink.ptms.adyeshach.common.entity.ai.expand.PathfinderLookAtPlayer
import ink.ptms.adyeshach.common.entity.ai.expand.PathfinderRandomLookaround
import ink.ptms.adyeshach.common.entity.ai.expand.PathfinderRandomStrollLand
import ink.ptms.adyeshach.common.entity.ai.general.GeneralGravity
import ink.ptms.adyeshach.common.entity.ai.general.GeneralMove
import ink.ptms.adyeshach.common.entity.ai.general.GeneralSmoothLook
import ink.ptms.adyeshach.common.entity.type.AdyWitherSkeleton
import ink.ptms.adyeshach.common.path.PathFinderProxy
import ink.ptms.adyeshach.common.util.Tasks
import io.izzel.taboolib.module.command.lite.CommandBuilder
import io.izzel.taboolib.module.inject.TInject
import io.izzel.taboolib.module.packet.Packet
import io.izzel.taboolib.module.packet.TPacket
import net.minecraft.server.v1_12_R1.BlockPosition
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld
import org.bukkit.craftbukkit.v1_12_R1.block.CraftBlock
import org.bukkit.entity.EntityType
import org.bukkit.entity.Pig
import org.bukkit.entity.Player
import org.bukkit.entity.Villager
import org.bukkit.inventory.ItemStack

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
            val spider = manager.create(EntityTypes.WITHER_SKELETON, sender.location) as AdyWitherSkeleton

            spider.pathfinder.add(GeneralSmoothLook(spider))
            spider.pathfinder.add(PathfinderLookAtPlayer(spider))
            spider.setItemInMainHand(ItemStack(Material.STONE_SWORD))
            sender.sendMessage("done. ")
        }
}