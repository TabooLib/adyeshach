package ink.ptms.adyeshach.common.entity.editor

import ink.ptms.adyeshach.api.nms.NMS
import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.util.RayTrace
import ink.ptms.adyeshach.common.util.info
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Sound
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import taboolib.common.platform.*
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.adaptPlayer
import taboolib.common5.Baffle
import taboolib.common5.Coerce
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

/**
 * @author Arasple
 * @date 2020/8/25 14:08
 */
object EditorPicker {

    class Handler(var entity: EntityInstance?, var distance: Double)

    private val cooldown = Baffle.of(200, TimeUnit.MILLISECONDS)
    private val playerSelected = ConcurrentHashMap<String, Handler>()

    @Schedule(period = 1)
    private fun e() {
        Bukkit.getOnlinePlayers().forEach {
            val select = getSelected(it)
            val entity = select.entity ?: return@forEach
            process(it, entity)
        }
    }

    @SubscribeEvent
    private fun e(e: PlayerQuitEvent) {
        cooldown.reset(e.player.name)
        playerSelected.remove(e.player.name)
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private fun e(e: PlayerItemHeldEvent) {
        val select = getSelected(e.player)
        val entity = select.entity ?: return
        val amount = if (e.player.isSneaking) 1.0 else 0.1
        if (e.newSlot < e.previousSlot) {
            if (select.distance <= 50.0) select.distance += amount
        } else if (select.distance >= amount) {
            select.distance -= amount
        }
        val adaptPlayer = adaptPlayer(e.player)
        adaptPlayer.sendTitle("§3§lMove Entity", "§7Adjust Distance: §8${Coerce.format(select.distance)}", 0, 20, 0)
        adaptPlayer.sendActionBar("§7Press §fF §7to settled entity's position §8| §7Press §fSHIFT + F §7to reset")
        process(e.player, entity)
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private fun e(e: PlayerSwapHandItemsEvent) {
        val entity = getSelected(e.player).entity ?: return
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
                if (distance < 0.5 && cooldown.hasNext(player.name)) {
                    player.playSound(player.location, Sound.UI_BUTTON_CLICK, 0.1f, 2f)
                    adaptPlayer(player).sendActionBar("§7The distance between the §fNPC §7and the §fGround §7is §f${Coerce.format(distance)}§7.")
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

    fun select(player: Player, entityInstance: EntityInstance?) {
        getSelected(player).let {
            it.entity = entityInstance
            it.distance = 1.0
            if (entityInstance != null) {
                val adaptPlayer = adaptPlayer(player)
                adaptPlayer.sendTitle("§3§lMove Entity", "§7Adjust Distance: §8${Coerce.format(it.distance)}", 0, 20, 0)
                adaptPlayer.sendActionBar("§7Press §fF §7to settled entity's position §8| §7Press §fSHIFT + F §7to reset")
            }
        }
    }

    fun getSelected(player: Player): Handler {
        return playerSelected.computeIfAbsent(player.name) { Handler(null, 0.0) }
    }
}