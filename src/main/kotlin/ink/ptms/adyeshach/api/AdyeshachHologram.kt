package ink.ptms.adyeshach.api

import ink.ptms.adyeshach.common.entity.EntityTypes
import ink.ptms.adyeshach.common.entity.type.AdyArmorStand
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerQuitEvent
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submit
import taboolib.common5.util.printed
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * 创建全息
 */
fun Player.createHologram(location: Location, content: List<String>): Hologram<*> {
    return HologramAdyeshach().also {
        it.create(this, location, content)
    }
}

/**
 * 以全息形式发送位于世界中的提示信息
 *
 * @param location 坐标
 * @param stay 停留时间
 * @param transfer 过渡转换
 * @param message 内容
 */
fun Player.createHolographic(location: Location, stay: Long = 40L, transfer: (String) -> (String) = { it }, vararg message: String) {
    val key = "${location.world!!.name},${location.x},${location.y},${location.z}"
    val messages = Cache.holographicMap.computeIfAbsent(name) { ConcurrentHashMap() }
    if (messages.containsKey(key)) {
        return
    }
    val holographic = Holographic(this, location, message.toList(), transfer)
    messages[key] = holographic
    submit(delay = stay) {
        messages.remove(key)
        holographic.cancel()
    }
}

abstract class Hologram<T> {

    private val map = HashMap<Int, T>()

    fun create(player: Player, location: Location, content: List<String>) {
        content.forEachIndexed { index, line ->
            map[index] = create(player, location.clone().add(0.0, (((content.size - 1) - index) * 0.3), 0.0), line)
        }
    }

    fun teleport(location: Location) {
        map.forEach { (index, obj) ->
            teleport(obj, location.clone().add(0.0, (((map.size - 1) - index) * 0.3), 0.0))
        }
    }

    fun update(content: List<String>) {
        content.forEachIndexed { index, line ->
            if (index < map.size) {
                update(map[index]!!, line)
            }
        }
    }

    fun delete() {
        map.forEach { delete(it.value) }
    }

    protected abstract fun create(player: Player, location: Location, line: String): T

    protected abstract fun update(obj: T, line: String)

    protected abstract fun teleport(obj: T, location: Location)

    protected abstract fun delete(obj: T)
}

class HologramAdyeshach : Hologram<AdyArmorStand>() {

    override fun create(player: Player, location: Location, line: String): AdyArmorStand {
        return AdyeshachAPI.getEntityManagerPrivateTemporary(player).create(EntityTypes.ARMOR_STAND, location) {
            val npc = it as AdyArmorStand
            npc.setSmall(true)
            npc.setMarker(true)
            npc.setBasePlate(false)
            npc.setInvisible(true)
            npc.setCustomName(line)
            npc.setCustomNameVisible(true)
        } as AdyArmorStand
    }

    override fun update(obj: AdyArmorStand, line: String) {
        if (!obj.isDeleted) {
            obj.setCustomName(line)
        }
    }

    override fun teleport(obj: AdyArmorStand, location: Location) {
        if (!obj.isDeleted) {
            obj.teleport(location)
        }
    }

    override fun delete(obj: AdyArmorStand) {
        obj.delete()
    }
}

object Cache {

    val holographicMap = ConcurrentHashMap<String, MutableMap<String, Holographic>>()

    @SubscribeEvent
    fun e(e: PlayerQuitEvent) {
        holographicMap.remove(e.player.name)
    }
}

/**
 * 全息警示缓存
 */
class Holographic(val player: Player, val location: Location, val message: List<String>, val transfer: (String) -> (String)) {

    val holograms = ArrayList<Hologram<*>>()
    val time = System.currentTimeMillis()

    init {
        message.forEachIndexed { index, content ->
            val hologram = player.createHologram(location.clone().add(0.0, (((message.size - 1) - index) * 0.3), 0.0), listOf(content))
            val map = content.printed("_").map(transfer)
            map.forEachIndexed { i, s ->
                submit(delay = i.toLong()) {
                    hologram.update(listOf(s))
                }
            }
            holograms += hologram
        }
    }

    fun cancel() {
        holograms.forEach { it.delete() }
    }
}