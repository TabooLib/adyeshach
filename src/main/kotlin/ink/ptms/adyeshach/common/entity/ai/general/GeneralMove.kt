package ink.ptms.adyeshach.common.entity.ai.general

import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.ai.Pathfinder
import ink.ptms.adyeshach.common.path.PathResult
import ink.ptms.adyeshach.common.path.PathType

/**
 * 实体移动逻辑
 *
 * @Author sky
 * @Since 2020-08-19 22:09
 */
class GeneralMove(entity: EntityInstance) : Pathfinder(entity) {

    private var i = 0

    var speed = 0.1
    var pathType: PathType? = null
    var pathResult: PathResult? = null

    var isMoving = false
        private set

    override fun shouldExecute(): Boolean {
        return pathType != null && pathResult != null
    }

    override fun onTick() {
        when (pathType) {
            PathType.WALK_1, PathType.WALK_2, PathType.WALK_3 -> {
                val position = pathResult!!.pointList[i]

            }
            PathType.FLY -> {

            }
        }
    }

    fun getGravity(): GeneralGravity {
        return entity.pathfinder.first { it is GeneralGravity } as GeneralGravity
    }
}