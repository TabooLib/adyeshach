package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.EntityTypes
import org.bukkit.entity.Player

/**
 * @author sky
 * @since 2020-08-04 18:28
 */
@Deprecated("Outdated but usable")
open class AdyEntity(entityTypes: EntityTypes, v2: ink.ptms.adyeshach.core.entity.EntityInstance) : EntityInstance(entityTypes, v2) {

    override fun visible(viewer: Player, visible: Boolean): Boolean {
        return v2.visible(viewer, visible)
    }
}