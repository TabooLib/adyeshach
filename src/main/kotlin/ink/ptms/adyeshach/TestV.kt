package ink.ptms.adyeshach

import ink.ptms.adyeshach.common.entity.type.EntityTypes
import ink.ptms.adyeshach.common.entity.type.impl.AdyHumanLike
import io.izzel.taboolib.module.command.lite.CommandBuilder
import io.izzel.taboolib.module.inject.TInject
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*

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
                if (sender is Player) {
                    val villager = AdyHumanLike(sender, EntityTypes.VILLAGER)
                    villager.spawn(sender.location)
                    villager.setGlowing(true)
                    villager.setItemInMainHand(ItemStack(Material.EMERALD))
                    Bukkit.getScheduler().runTaskLaterAsynchronously(Adyeshach.plugin, Runnable {
                        villager.destroy()
                        sender.sendMessage("§c[System] §7LRemoved.")
                    }, 60L)
                    sender.sendMessage("§c[System] §7Done.")
                }
            }
}