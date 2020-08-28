package ink.ptms.adyeshach.common.editor.move

import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.util.RayTrace
import ink.ptms.adyeshach.internal.command.Helper
import io.izzel.taboolib.module.inject.TListener
import io.izzel.taboolib.module.locale.TLocale
import io.izzel.taboolib.util.lite.Numbers
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent

/**
 * @author Arasple
 * @date 2020/8/25 14:08
 */
object Picker : Helper {

    private val playerSelected = mutableMapOf<String, Handler>()

    fun select(player: Player, entityInstance: EntityInstance?) {
        getSelected(player).let {
            it.entityInstance = entityInstance
            it.distance = 0.0
        }
    }

    fun getSelected(player: Player): Handler {
        return playerSelected.computeIfAbsent(player.name) { Handler(null, 0.0) }
    }

    class Handler(var entityInstance: EntityInstance?, var distance: Double)

    @TListener
    class Listener : org.bukkit.event.Listener {

        @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
        fun e(e: PlayerMoveEvent) {
            val select = getSelected(e.player)
            val entity = select.entityInstance ?: return

            process(e.player, entity)
        }

        @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
        fun e(e: PlayerItemHeldEvent) {
            val select = getSelected(e.player)
            val entity = select.entityInstance ?: return
            val amount = if (e.player.isSneaking) 0.5 else 2.0
            if (e.newSlot < e.previousSlot) {
                if (select.distance <= 50.0) select.distance += amount
            } else if (select.distance >= amount) {
                select.distance -= amount
            }

            TLocale.Display.sendTitle(e.player, "§3§lMove Entity", "§7Adjust Distance: §8${Numbers.format(select.distance)}", 0, 20, 0)
            TLocale.Display.sendActionBar(e.player, "§7Press §fF §7 to settled entity's position §8| §7Press §fShift+F §7to reset")

            process(e.player, entity)
        }

        @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
        fun e(e: PlayerSwapHandItemsEvent) {
            val entity = getSelected(e.player).entityInstance ?: return
            e.isCancelled = true
            if (e.player.isSneaking) {
                entity.teleport(e.player.location)
                getSelected(e.player).distance = 0.0
            } else {
                entity.let {
                    select(e.player, null)
                    e.player.info("Entity settled.")
                }
            }
        }

        fun process(player: Player, entity: EntityInstance) {
            val select = getSelected(player)
            val rayTrace = RayTrace(player)
            val teleport = rayTrace.distance(2 + select.distance).toLocation(player.world)
            entity.teleport(teleport)
        }
    }
}