
package ink.ptms.adyeshach.common.entity

import org.bukkit.entity.Player

/**
 * Adyeshach
 * ink.ptms.adyeshach.common.entity.InternalEntity
 *
 * @author 坏黑
 * @since 2022/6/19 16:25
 */
@Suppress("SpellCheckingInspection")
interface InternalEntity {

    /**
     * 生成回调
     */
    fun spawn(viewer: Player, spawn: Runnable): Boolean

    /**
     * 销毁回调
     */
    fun destroy(viewer: Player, destroy: Runnable): Boolean
}