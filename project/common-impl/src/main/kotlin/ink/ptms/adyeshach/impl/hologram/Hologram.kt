package ink.ptms.adyeshach.impl.hologram

import ink.ptms.adyeshach.core.AdyeshachHologram
import ink.ptms.adyeshach.core.entity.manager.Manager
import ink.ptms.adyeshach.impl.hologram.DefaultAdyeshachHologramHandler.Companion.toHologramContents
import org.bukkit.Location

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.hologram.Hologram
 *
 * @author 坏黑
 * @since 2022/12/16 20:48
 */
class Hologram(val manager: Manager, var origin: Location, var content: List<AdyeshachHologram.Item>) : AdyeshachHologram {

    init {
        refresh()
    }

    override fun teleport(location: Location) {
        this.origin = location.clone()
        this.content.filterIsInstance<HoloEntity<*>>().reversed().forEach { it.teleport(origin) }
    }

    override fun update(content: List<Any>) {
        this.content.forEach { it.remove() }
        this.content = content.toHologramContents()
        refresh()
    }

    override fun remove() {
        this.content.forEach { it.remove() }
        this.content = emptyList()
    }

    override fun contents(): List<AdyeshachHologram.Item> {
        return content
    }

    fun refresh() {
        if (content.isEmpty()) {
            return
        }
        var offset = -content.first().space
        content.filterIsInstance<HoloEntity<*>>().reversed().forEach {
            it.spawn(offset, origin, manager)
            offset += it.space
        }
    }
}