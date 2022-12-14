package ink.ptms.adyeshach.impl.hologram

import ink.ptms.adyeshach.common.api.AdyeshachHologram
import ink.ptms.adyeshach.common.entity.manager.Manager
import ink.ptms.adyeshach.common.entity.type.AdyEntity
import org.bukkit.Location

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.hologram.HoloEntity
 *
 * @author 坏黑
 * @since 2022/12/14 13:39
 */
abstract class HoloEntity<T : AdyEntity> : AdyeshachHologram.ItemByEntity<T> {

    /** 生成全息 */
    abstract fun spawn(location: Location, manager: Manager)

    /** 删除全息 */
    abstract fun delete()

    /** 传送到某处 */
    abstract fun teleport(location: Location)
}