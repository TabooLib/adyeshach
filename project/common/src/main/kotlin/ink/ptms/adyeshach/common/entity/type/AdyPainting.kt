package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.bukkit.BukkitDirection
import ink.ptms.adyeshach.common.bukkit.BukkitPaintings

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
abstract class AdyPainting : AdyEntity() {

    /**
     * 设置方向
     */
    abstract fun setDirection(direction: BukkitDirection)

    /**
     * 获取方向
     */
    abstract fun getDirection(): BukkitDirection

    /**
     * 设置画像类型
     */
    abstract fun setPainting(painting: BukkitPaintings)

    /**
     * 获取画像类型
     */
    abstract fun getPainting(): BukkitPaintings
}