package ink.ptms.adyeshach.common.entity.ai.general

import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.ai.Pathfinder
import ink.ptms.adyeshach.common.path.PathResult
import ink.ptms.adyeshach.common.path.PathType
import io.izzel.taboolib.module.lite.SimpleCounter
import org.bukkit.Location

/**
 * 实体移动逻辑
 *
 * @Author sky
 * @Since 2020-08-19 22:09
 */
class GeneralMove(entity: EntityInstance) : Pathfinder(entity) {

    private var i = 0
    private var counterLook = SimpleCounter(2, true)
    private var counterJump = SimpleCounter(5, true)

    var speed = 0.0
        set(value) {
            field = value.coerceAtMost(1.0)
        }

    var pathType: PathType? = null
    var pathResult: PathResult? = null

    var isMoving = false
        private set
    var isJumping = false
        private set

    override fun shouldExecute(): Boolean {
        return pathType != null && pathResult != null
    }

    override fun onTick() {
        if (isJumping) {
            if (counterJump.next()) {
                isJumping = false
                getGravity().isGravity = true
            } else {
                entity.teleport(entity.position.x, entity.position.y + 0.25, entity.position.z)
            }
            return
        }
        if (i >= pathResult!!.pointList.size) {
            i = 0
            pathType = null
            pathResult = null
            return
        }
        val positionEntity = entity.position.toLocation()
        val positionNext = pathResult!!.pointList[i].run {
            Location(entity.position.world, x.toDouble() + 0.5, y.toDouble(), z.toDouble() + 0.5)
        }
        if (counterLook.next()) {
            entity.controllerLook(positionNext.clone().run {
                y = positionEntity.y + (entity.entityType.entitySize.height * 0.9)
                this
            })
        }
        val next = positionNext.clone().also {
            if (pathType != PathType.FLY) {
                it.y = positionEntity.y
            }
        }
        val plan = positionEntity.clone().add(next.clone().subtract(positionEntity).toVector().normalize().multiply(speed))
        if (plan.block.isPassable) {
            entity.teleport(plan.x, plan.y, plan.z)
        } else if (pathType != PathType.FLY) {
            isJumping = true
            getGravity().isGravity = false
        }
        if (entity.position.toLocation().distance(next) < speed) {
            i++
        }
    }

    fun getGravity(): GeneralGravity {
        return entity.pathfinder.first { it is GeneralGravity } as GeneralGravity
    }
}