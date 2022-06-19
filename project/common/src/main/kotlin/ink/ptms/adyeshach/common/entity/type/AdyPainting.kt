package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.bukkit.BukkitDirection
import ink.ptms.adyeshach.common.bukkit.BukkitPaintings

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
interface AdyPainting : AdyEntity {

    /**
     * 设置方向
     */
    fun setDirection(direction: BukkitDirection)

    /**
     * 获取方向
     */
    fun getDirection(): BukkitDirection

    /**
     * 设置画像类型
     */
    fun setPainting(painting: BukkitPaintings)

    /**
     * 获取画像类型
     */
    fun getPainting(): BukkitPaintings
}