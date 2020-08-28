package ink.ptms.adyeshach

import ink.ptms.adyeshach.api.AdyeshachAPI
import ink.ptms.adyeshach.common.bukkit.BukkitAnimation
import ink.ptms.adyeshach.common.entity.EntityTypes
import ink.ptms.adyeshach.common.entity.ai.expand.ControllerLookAtPlayer
import ink.ptms.adyeshach.common.entity.ai.expand.ControllerRandomLookaround
import ink.ptms.adyeshach.common.entity.ai.expand.ControllerRandomStrollLand
import ink.ptms.adyeshach.common.entity.ai.general.GeneralGravity
import ink.ptms.adyeshach.common.entity.ai.general.GeneralMove
import ink.ptms.adyeshach.common.entity.ai.general.GeneralSmoothLook
import ink.ptms.adyeshach.common.entity.type.AdyHuman
import ink.ptms.adyeshach.common.entity.type.AdyWitherSkeleton
import ink.ptms.adyeshach.common.util.Tasks
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
            val player = manager.create(EntityTypes.PLAYER, sender.location) as AdyHuman
            player.setItemInMainHand(ItemStack(Material.STONE_SWORD))
            player.setTexture("Bakurit_Maueic")
            Tasks.delay(20) {
                player.setSleeping(true)
                sender.sendMessage(" 0. ")
            }
            sender.sendMessage("done. ")
        }
}