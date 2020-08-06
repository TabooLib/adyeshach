package ink.ptms.adyeshach.common.entity.type.impl

import ink.ptms.adyeshach.api.nms.NMS
import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.element.EntityPosition
import ink.ptms.adyeshach.common.entity.type.EntityTypes
import io.izzel.taboolib.internal.gson.annotations.Expose
import org.bukkit.Location
import org.bukkit.entity.Player

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
class AdyExperienceOrb(owner: Player, @Expose var amount: Int = -1) : EntityInstance(owner, EntityTypes.EXPERIENCE_ORB) {

    override fun spawn(location: Location) {
        this.world = location.world!!.name
        this.position = EntityPosition.fromLocation(location)
        NMS.INSTANCE.spawnEntityExperienceOrb(owner, index, location, amount)
    }

}