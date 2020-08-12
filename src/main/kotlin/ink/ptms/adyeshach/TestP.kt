package ink.ptms.adyeshach

import ink.ptms.adyeshach.api.AdyeshachAPI
import ink.ptms.adyeshach.api.nms.NMS
import ink.ptms.adyeshach.common.entity.type.EntityTypes
import ink.ptms.adyeshach.common.entity.type.impl.AdyHuman
import ink.ptms.adyeshach.common.util.Serializer
import io.izzel.taboolib.module.command.lite.CommandBuilder
import io.izzel.taboolib.module.inject.TInject
import io.izzel.taboolib.module.nms.NMSImpl
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/**
 * @author Arasple
 * @date 2020/8/3 22:34
 * Player (NPC) test
 */
object TestP {

    @TInject
    val testV: CommandBuilder = CommandBuilder
            .create("test-p", Adyeshach.plugin)
            .execute { sender, _ ->
                if (sender is Player) {
                    val human = AdyeshachAPI.spawn(EntityTypes.VILLAGER, sender, sender.location)
                    Bukkit.getScheduler().runTaskLaterAsynchronously(Adyeshach.plugin, Runnable {
                        val loc = sender.location.subtract(human.getLatestLocation())
                        NMS.INSTANCE.relMoveEntity(sender, human.index, loc.x, loc.y, loc.z)
                        sender.sendMessage("§c[System] §7Moving for you.")
                    }, 40L)
                    Bukkit.getScheduler().runTaskLater(Adyeshach.plugin, Runnable {
                        human.destroy()
                        sender.sendMessage("§c[System] §7Removed.")
                    }, 60)
                    sender.sendMessage("§c[System] §7Done.")
                }
            }
}