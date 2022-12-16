package ink.ptms.adyeshach.impl.hologram

import ink.ptms.adyeshach.core.AdyeshachHologram
import ink.ptms.adyeshach.core.entity.manager.Manager
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
        var offset = 0.0
        content.filterIsInstance<HoloEntity<*>>().reversed().forEach {
            it.spawn(offset, origin, manager)
            offset += it.space
        }
    }

    override fun teleport(location: Location) {
        this.origin = location.clone()
        this.content.filterIsInstance<HoloEntity<*>>().reversed().forEach { it.teleport(origin) }
    }

    override fun update(content: List<AdyeshachHologram.Item>) {
        this.content.forEach { it.remove() }
        this.content = content
    }

    override fun remove() {
        this.content.forEach { it.remove() }
        this.content = emptyList()
    }

    override fun contents(): List<AdyeshachHologram.Item> {
        return content
    }
}