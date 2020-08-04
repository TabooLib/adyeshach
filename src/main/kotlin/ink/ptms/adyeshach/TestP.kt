package ink.ptms.adyeshach

import ink.ptms.adyeshach.common.entity.type.impl.AdyHuman
import ink.ptms.adyeshach.common.util.Serializer
import io.izzel.taboolib.module.command.lite.CommandBuilder
import io.izzel.taboolib.module.inject.TInject
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
                    val human = AdyHuman(sender)
                    human.spawn(sender.location)
                    human.hideInTabList()
                    Bukkit.getScheduler().runTaskLaterAsynchronously(Adyeshach.plugin, Runnable {
                        human.setFired(true)
                        human.setGlowing(true)
                        sender.sendMessage("§c[System] §7Glowing & OnFire Enabled.")
                    }, 20)
                    Bukkit.getScheduler().runTaskLaterAsynchronously(Adyeshach.plugin, Runnable {
                        human.setFired(false)
                        human.setGlowing(false)
                        sender.sendMessage("§c[System] §7Glowing & OnFire Disabled.")
                    }, 40)
                    Bukkit.getScheduler().runTaskLaterAsynchronously(Adyeshach.plugin, Runnable {
                        human.controllerLook(sender.eyeLocation)
                        sender.sendMessage("§c[System] §7Looking for you.")
                    }, 60L)
                    Bukkit.getScheduler().runTaskLater(Adyeshach.plugin, Runnable {
                        human.setItemInMainHand(ItemStack(Material.DIAMOND))
                        human.setItemInOffHand(ItemStack(Material.IRON_INGOT))
                        sender.sendMessage("§c[System] §7Equipment updated.")
                    }, 80L)
                    Bukkit.getScheduler().runTaskLater(Adyeshach.plugin, Runnable {
                        sender.sendMessage("§c[System] §7Json:")
                        sender.sendMessage(io.izzel.taboolib.internal.gson.GsonBuilder().setPrettyPrinting().create().toJson(Serializer.serializer.toJsonTree(human)))
                        human.destroy()
                        sender.sendMessage("§c[System] §7Removed.")
                    }, 100)
                    sender.sendMessage("§c[System] §7Done.")
                }
            }
}