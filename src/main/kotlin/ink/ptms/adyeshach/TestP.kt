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
                    human.setTexture(
                            "ewogICJ0aW1lc3RhbXAiIDogMTU5NjU0NjE3OTEzOCwKICAicHJvZmlsZUlkIiA6ICJhMzZkNzY0NDhkYWU0NmIxYmQwMDU0MjJjNjYyOTk3ZiIsCiAgInByb2ZpbGVOYW1lIiA6ICJCYWt1cml0X01hdWVpYyIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS80NzRhYzQyNDM1ZTJmMmU4NjA3ZTAxZTAyMmJmOGIzNWMzZDNhNDI0ZDQxMmEzMmQ5YTRmNWU1NzRiZDU2NzZjIgogICAgfQogIH0KfQ==",
                            "AkZtxjwY8urkJpWUUhi4dR8DELAwfZZ7UUX1nWIIb0r/vvxBcar3ifWhmGAgKUiAh4JYT9i1hOUfL9JeoMPM0XhCxHsFCR5q3LA8ycUkSWfCDWD1h4byr/mH4PuWFIglH0QKOBlSDaCWDSkdhAJ142o4bcl9vH8rNFon93anY2Wb44bWe4JWmMwuMn3YuZnYPkPEeu97j940zf1xgl2cq4ASsuQw/CifMUxO6jG4xrube1dyZNn8dJUmGWrcBVWMHegiWTgGDnQC/2P2ki599STCnzkIXi0Ncj43rOE0jlN7POn6hzrnEIkObYSJZJR02wBjS50Y612q1R9sG8y/UZ7IfORRGq0ke5PaikfCwoApqEji8fKLkl0kG9t5YTT0RZwEaOdbUpKknLMPFYvrRrHFl4lWee6bg5wGu2p5v3Gn9DSd7uU7FQPbt65r+cYknU0mfz7dqzCdz6oEFii8ltAdbUltaSWbWUqNyAcNNr8NVMMqkEuW/BiE5N/cptUKajIz9qLxiWr+2Va9eKQYMUr6gWAUi7KqFc6eYoywgysNbSmZupeMop+jJlLjUcKrE+LdwUX7LjkHiF055qlLT3vwGQUuEE4Nem83Ib9cbPNyGUW3IcsukCtVjzqYofAH8aOF5cYKirlEuyIMXoJRX9DwOVfkBuWTG1xDxfJDxzE="
                    )
                    Bukkit.getScheduler().runTaskLaterAsynchronously(Adyeshach.plugin, Runnable {
                        human.removePlayerInfo()
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
                    }, 180)
                    sender.sendMessage("§c[System] §7Done.")
                }
            }
}