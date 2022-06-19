
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

    fun spawn(viewer: Player, spawn: Runnable): Boolean

    fun destroy(viewer: Player, destroy: Runnable): Boolean
}