package ink.ptms.adyeshach

import ink.ptms.adyeshach.common.bukkit.BukkitPaintings
import ink.ptms.adyeshach.common.entity.type.impl.AdyFallingBlock
import ink.ptms.adyeshach.common.entity.type.impl.AdyPainting
import ink.ptms.adyeshach.common.util.Tasks
import io.izzel.taboolib.module.command.lite.CommandBuilder
import io.izzel.taboolib.module.inject.TInject
import io.izzel.taboolib.module.inject.TListener
import io.izzel.taboolib.util.Files
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent

/**
 * @author Arasple
 * @date 2020/8/3 21:55
 * Villager test
 */
object TestV {

    @TListener
    class Interact : Listener {

        @EventHandler
        fun onInteract(e: PlayerInteractEvent) {
            val player = e.player
            val block = e.clickedBlock ?: return

            if (player.isSneaking && e.action == Action.RIGHT_CLICK_BLOCK) {
                val entity = AdyPainting(player)
                entity.spawn(block.location)

                var delay = 20L
                BukkitPaintings.values().forEach {
                    Tasks.delay(delay, true) {
                        entity.setPainting(it)
                    }
                    delay += 20
                }

                Tasks.delay(20 * 20) {
                    entity.destroy()
                }

                Files.file(Adyeshach.plugin.dataFolder, "output.json").writeText(entity.toJson())
                player.sendMessage("§c[System] §7Done.")
            }
        }

    }

    @TInject
    val testV: CommandBuilder = CommandBuilder
        .create("test-v", Adyeshach.plugin)
        .execute { sender, _ ->
            if (sender is Player) {
                val entity = AdyFallingBlock(sender)

                entity.spawn(sender.location)
                entity.setNoGravity(true)
                entity.setMaterial(Material.CRYING_OBSIDIAN)

                Tasks.delay(40) {
                    entity.setMaterial(
                        Material.DIAMOND_ORE
                    )
                }

                Tasks.delay(80) {
                    entity.destroy()
                }

                Files.file(Adyeshach.plugin.dataFolder, "output.json").writeText(entity.toJson())
                sender.sendMessage("§c[System] §7Done.")
            }
        }
}