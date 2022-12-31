package ink.ptms.adyeshach.impl.hologram

import ink.ptms.adyeshach.core.AdyeshachHologram
import ink.ptms.adyeshach.core.entity.manager.Manager
import ink.ptms.adyeshach.core.entity.type.AdyEntity
import ink.ptms.adyeshach.core.util.plus
import org.bukkit.Location

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.hologram.HoloEntity
 *
 * @author 坏黑
 * @since 2022/12/14 13:39
 */
@Suppress("UNCHECKED_CAST")
abstract class HoloEntity<T : AdyEntity>(space: Double) : AdyeshachHologram.ItemByEntity<T> {

    var offset = 0.0

    var entity: T? = null
    var origin: Location? = null

    override var space = space
        set(value) {
            field = value
            teleport(origin ?: return)
        }

    /** 生成回调 */
    abstract fun prepareSpawn(entity: T)

    /** 生成全息 */
    override fun spawn(offset: Double, location: Location, manager: Manager) {
        this.offset = offset
        this.origin = location.clone()
        this.entity?.remove()
        this.entity = manager.create(type, origin!!.clone().plus(y = offset + space)) { prepareSpawn(it as T) } as T
        this.entity?.setDerived("AdyeshachHologram")
    }

    /** 删除全息 */
    override fun remove() {
        entity?.remove()
    }

    /** 传送到某处 */
    override fun teleport(location: Location) {
        origin = location
        entity?.teleport(origin!!.clone().plus(y = offset + space))
    }

    /** 获取实体 */
    override fun entity(): T {
        return entity!!
    }
}