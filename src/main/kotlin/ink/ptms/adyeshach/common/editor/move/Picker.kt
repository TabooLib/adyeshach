package ink.ptms.adyeshach.common.editor.move

import ink.ptms.adyeshach.api.nms.NMS
import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.util.RayTrace
import ink.ptms.adyeshach.internal.command.Helper
import io.izzel.taboolib.module.inject.PlayerContainer
import io.izzel.taboolib.module.inject.TInject
import io.izzel.taboolib.module.inject.TListener
import io.izzel.taboolib.module.inject.TSchedule
import io.izzel.taboolib.module.locale.TLocale
import io.izzel.taboolib.util.lite.Numbers
import io.izzel.taboolib.util.lite.cooldown.Cooldown
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Sound
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import java.util.concurrent.ConcurrentHashMap

/**
 * @author Arasple
 * @date 2020/8/25 14:08
 */
object Picker : Helper {

    @PlayerContainer
    private val playerSelected = ConcurrentHashMap<String, Handler>()

    fun select(player: Player, entityInstance: EntityInstance?) {
        getSelected(player).let {
            it.entityInstance = entityInstance
            it.distance = 1.0
            if (entityInstance != null) {
                TLocale.Display.sendTitle(player, "§3§lMove Entity", "§7Adjust Distance: §8${Numbers.format(it.distance)}", 0, 20, 0)
                TLocale.Display.sendActionBar(player, "§7Press §fF §7to settled entity's position §8| §7Press §fSHIFT + F §7to reset")
            }
        }
    }

    fun getSelected(player: Player): Handler {
        return playerSelected.computeIfAbsent(player.name) { Handler(null, 0.0) }
    }

    class Handler(var entityInstance: EntityInstance?, var distance: Double)

    @TListener
    class Listener : org.bukkit.event.Listener {

        @TInject
        private val cooldown = Cooldown("Adyeshach:picker:move", 200)

        @TSchedule(period = 1)
        fun e() {
            Bukkit.getOnlinePlayers().forEach {
                val select = getSelected(it)
                val entity = select.entityInstance ?: return@forEach
                process(it, entity)
            }
        }

        @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
        fun e(e: PlayerItemHeldEvent) {
            val select = getSelected(e.player)
            val entity = select.entityInstance ?: return
            val amount = if (e.player.isSneaking) 1.0 else 0.1
            if (e.newSlot < e.previousSlot) {
                if (select.distance <= 50.0) select.distance += amount
            } else if (select.distance >= amount) {
                select.distance -= amount
            }
            TLocale.Display.sendTitle(e.player, "§3§lMove Entity", "§7Adjust Distance: §8${Numbers.format(select.distance)}", 0, 20, 0)
            TLocale.Display.sendActionBar(e.player, "§7Press §fF §7to settled entity's position §8| §7Press §fSHIFT + F §7to reset")
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
            val teleport = rayTrace.distance(select.distance).toLocation(player.world)
            val b = getHeightBlock(teleport)
            if (b != null) {
                val maxY = NMS.INSTANCE.getBlockHeight(b) + b.y
                if (maxY != entity.position.y) {
                    val distance = entity.position.y - maxY
                    if (distance < 0.5 && !cooldown.isCooldown(player.name)) {
                        player.playSound(player.location, Sound.UI_BUTTON_CLICK, 0.1f, 2f)
                        TLocale.Display.sendActionBar(player, "§7The distance between the §fNPC §7and the §fGround §7is §f${Numbers.format(distance)}§7.")
                    }
                }
            }
            entity.teleport(teleport)
        }

        fun getHeightBlock(loc: Location): Block? {
            if (loc.y < 1) {
                return null
            }
            val block = loc.subtract(0.0, 1.0, 0.0).block
            return if (NMS.INSTANCE.getBlockHeight(block) == 0.0) getHeightBlock(block.location) else block
        }
    }
}