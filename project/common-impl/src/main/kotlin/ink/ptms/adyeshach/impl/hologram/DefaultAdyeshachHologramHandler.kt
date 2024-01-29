package ink.ptms.adyeshach.impl.hologram

import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.AdyeshachHologram
import ink.ptms.adyeshach.core.AdyeshachHologramHandler
import ink.ptms.adyeshach.core.entity.manager.ManagerType
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.PlatformFactory
import taboolib.common.platform.function.submit
import taboolib.common5.util.printed

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.hologram.DefaultAdyeshachHologramHandler
 *
 * @author 坏黑
 * @since 2022/6/28 00:05
 */
class DefaultAdyeshachHologramHandler : AdyeshachHologramHandler {

    override fun createHologramItem(text: String, space: Double): AdyeshachHologram.ItemByText {
        return HoloEntityText(text, space)
    }

    override fun createHologramItem(itemStack: ItemStack, space: Double): AdyeshachHologram.ItemByItemStack {
        return HoloEntityItemStack(itemStack, space)
    }

    override fun createHologram(location: Location, content: List<Any>, isolate: Boolean): AdyeshachHologram {
        val manager = Adyeshach.api().getPublicEntityManager(if (isolate) ManagerType.ISOLATED else ManagerType.TEMPORARY)
        return Hologram(manager, location, content.toHologramContents())
    }

    override fun createHologram(player: Player, location: Location, content: List<Any>, isolate: Boolean): AdyeshachHologram {
        val manager = Adyeshach.api().getPrivateEntityManager(player, if (isolate) ManagerType.ISOLATED else ManagerType.TEMPORARY)
        return Hologram(manager, location, content.toHologramContents())
    }

    override fun sendHologramMessage(location: Location, message: List<String>, stay: Long) {
        hologramMessage(null, location, message, stay)
    }

    override fun sendHologramMessage(player: Player, location: Location, message: List<String>, stay: Long) {
        hologramMessage(player, location, message, stay)
    }

    fun hologramMessage(player: Player?, location: Location, message: List<String>, stay: Long) {
        if (message.isEmpty() || stay < 1) {
            return
        }
        val hologram = if (player == null) {
            Adyeshach.api().getHologramHandler().createHologram(location, message.map { "" }, true)
        } else {
            Adyeshach.api().getHologramHandler().createHologram(player, location, message.map { "" }, true)
        }
        val hologramItems = hologram.contents().map { it as HoloEntityText }
        val content = message.map { it.printed("_") }
        val len = content.maxOf { it.size }
        for (i in 0 until len) {
            val frame = content.map { if (i < it.size) it[i] else it.lastOrNull() ?: "" }
            // 分段更新内容
            submit(delay = i.toLong()) {
                hologramItems.forEachIndexed { index, entity -> entity.text = frame[index] }
            }
        }
        // 延迟后移除
        submit(delay = stay) { hologram.remove() }
    }

    companion object {

        @Awake(LifeCycle.CONST)
        fun init() {
            PlatformFactory.registerAPI<AdyeshachHologramHandler>(DefaultAdyeshachHologramHandler())
        }

        fun List<Any>.toHologramContents(): List<AdyeshachHologram.Item> {
            return map {
                when (it) {
                    is AdyeshachHologram.Item -> it
                    is String -> Adyeshach.api().getHologramHandler().createHologramItem(it)
                    is ItemStack -> Adyeshach.api().getHologramHandler().createHologramItem(it)
                    else -> throw IllegalArgumentException("Unknown content type: $it")
                }
            }
        }
    }
}