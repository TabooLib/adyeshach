package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.api.nms.NMS
import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.element.EntityPosition
import ink.ptms.adyeshach.common.entity.EntityTypes
import io.izzel.taboolib.internal.gson.annotations.Expose
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.*

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
class AdyExperienceOrb : AdyEntity(EntityTypes.EXPERIENCE_ORB) {

    @Expose
    var amount = 1

    override fun visible(viewer: Player, visible: Boolean) {
        if (visible) {
            spawn(viewer) {
                NMS.INSTANCE.spawnEntityExperienceOrb(viewer, index, getLatestLocation(), amount)
            }
        } else {
            destroy(viewer) {
                NMS.INSTANCE.destroyEntity(viewer, index)
            }
        }
    }
}