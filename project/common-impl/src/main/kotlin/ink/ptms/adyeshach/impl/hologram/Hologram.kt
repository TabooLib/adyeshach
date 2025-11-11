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
        this.content.filterIsInstance<HoloEntity<*>>().asReversed().forEach { it.teleport(origin) }
    }

    override fun update(content: List<Any>) {
        // 获取新的内容
        val newContent = content.toHologramContents()
        // 检查内容是否发生变化
        if (this.content.size != newContent.size || this.content.zip(newContent).any { (old, new) -> old::class != new::class }) {
            // 如果行数或类型发生变化，则完全刷新
            this.content.forEach { it.remove() }
            this.content = newContent
            refresh()
        } else {
            // 如果内容相同，则只更新内容
            this.content.zip(newContent).forEach { (old, new) ->
                old.merge(new)
            }
        }
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
        content.filterIsInstance<HoloEntity<*>>().asReversed().forEach {
            it.spawn(offset, origin, manager)
            offset += it.space
        }
    }
}