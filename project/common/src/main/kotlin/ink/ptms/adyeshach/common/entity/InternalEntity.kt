
package ink.ptms.adyeshach.common.entity

import org.bukkit.entity.Player

/**
 * Adyeshach
 * ink.ptms.adyeshach.common.entity.InternalEntity
 *
 * @author 坏黑
 * @since 2022/6/19 16:25
 */
interface InternalEntity {

    /**
     * 实体预生成方法
     */
    fun prepareSpawn(viewer: Player, spawn: Runnable): Boolean

    /**
     * 实体预销毁方法
     */
    fun prepareDestroy(viewer: Player, destroy: Runnable): Boolean
}