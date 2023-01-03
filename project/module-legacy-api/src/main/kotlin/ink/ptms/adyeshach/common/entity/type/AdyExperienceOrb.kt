package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityTypes
import ink.ptms.adyeshach.core.entity.type.AdyExperienceOrb
import org.bukkit.entity.Player

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
@Deprecated("Outdated but usable")
class AdyExperienceOrb(v2: ink.ptms.adyeshach.core.entity.EntityInstance):  AdyEntity(EntityTypes.EXPERIENCE_ORB, v2) {

    var amount: Int
        get() {
            v2 as AdyExperienceOrb
            return v2.getAmount()
        }
        set(value) {
            v2 as AdyExperienceOrb
            v2.setAmount(value)
        }

    override fun visible(viewer: Player, visible: Boolean): Boolean {
        return v2.visible(viewer, visible)
    }
}