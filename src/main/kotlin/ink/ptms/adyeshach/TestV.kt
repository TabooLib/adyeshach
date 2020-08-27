package ink.ptms.adyeshach

import ink.ptms.adyeshach.api.AdyeshachAPI
import ink.ptms.adyeshach.common.entity.EntityTypes
import ink.ptms.adyeshach.common.entity.ai.expand.PathfinderLookAtPlayer
import ink.ptms.adyeshach.common.entity.ai.expand.PathfinderRandomLookaround
import ink.ptms.adyeshach.common.entity.ai.expand.PathfinderRandomStrollLand
import ink.ptms.adyeshach.common.entity.ai.general.GeneralGravity
import ink.ptms.adyeshach.common.entity.ai.general.GeneralMove
import ink.ptms.adyeshach.common.entity.ai.general.GeneralSmoothLook
import ink.ptms.adyeshach.common.entity.type.AdyWitherSkeleton
import io.izzel.taboolib.module.command.lite.CommandBuilder
import io.izzel.taboolib.module.inject.TInject
import org.bukkit.Material
import org.bukkit.entity.Player
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
            val npc = manager.create(EntityTypes.WITHER_SKELETON, sender.location) as AdyWitherSkeleton

            npc.pathfinder.add(GeneralMove(npc))
            npc.pathfinder.add(GeneralGravity(npc))
            npc.pathfinder.add(GeneralSmoothLook(npc))
            npc.pathfinder.add(PathfinderLookAtPlayer(npc))
            npc.pathfinder.add(PathfinderRandomStrollLand(npc))
            npc.pathfinder.add(PathfinderRandomLookaround(npc))
            npc.setItemInMainHand(ItemStack(Material.STONE_SWORD))
            sender.sendMessage("done. ")
        }
}