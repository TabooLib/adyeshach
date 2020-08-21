package ink.ptms.adyeshach.common.entity.ai.general

import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.ai.Pathfinder
import ink.ptms.adyeshach.common.path.PathResult
import ink.ptms.adyeshach.common.path.PathType

/**
 * 实体平滑视角改变
 *
 * @Author sky
 * @Since 2020-08-19 22:09
 */
class GeneralSmoothLook(entity: EntityInstance) : Pathfinder(entity) {

    var yaw = 0f
    var pitch = 0f
    var isLooking = false

    override fun shouldExecute(): Boolean {
        return isLooking
    }

    override fun onTick() {
    }
}