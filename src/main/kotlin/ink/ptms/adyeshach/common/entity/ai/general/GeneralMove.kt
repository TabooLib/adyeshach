package ink.ptms.adyeshach.common.entity.ai.general

import ink.ptms.adyeshach.api.nms.NMS
import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.ai.Controller
import ink.ptms.adyeshach.common.entity.path.PathType
import ink.ptms.adyeshach.common.entity.path.ResultNavigation
import org.bukkit.Location
import taboolib.common5.Baffle

/**
 * 实体移动逻辑
 *
 * @Author sky
 * @Since 2020-08-19 22:09
 */
class GeneralMove(entity: EntityInstance) : Controller(entity) {

    private var i = 0
    private var counterLook = Baffle.of(2)
    private var counterJump = 0

    var speed = 0.0
        set(value) {
            field = value.coerceAtMost(1.0)
        }

    var pathType: PathType? = null
    var resultNavigation: ResultNavigation? = null

    override fun shouldExecute(): Boolean {
        return pathType != null && resultNavigation != null
    }

    override fun onTick() {
        if (entity!!.getTag("isFreeze") == "true") {
            return
        }
        if (entity.isControllerJumping()) {
            if (counterJump-- == 0) {
                entity.removeTag("isJumping")
                getGravity().isGravity = true
            } else {
                entity.teleport(entity.position.x, entity.position.y + 0.35, entity.position.z)
            }
            return
        }
        if (i >= resultNavigation!!.pointList.size) {
            i = 0
            pathType = null
            resultNavigation = null
            entity.removeTag("isMoving")
            return
        } else {
            entity.setTag("isMoving", "true")
        }
        val positionEntity = entity.position.toLocation()
        val positionNext = resultNavigation!!.pointList[i].run {
            Location(entity.position.world, x.toDouble() + 0.55, y.toDouble(), z.toDouble() + 0.5)
        }
        if (counterLook.hasNext()) {
            entity.controllerLook(positionNext.clone().run {
                y = positionEntity.y + (entity.entityType.entitySize.height * 0.9)
                this
            }, smooth = false, smoothInternal = 45f)
        }
        val next = positionNext.clone().also {
            if (pathType != PathType.FLY) {
                it.y = positionEntity.y
            }
        }
        val plan = positionEntity.clone().add(next.clone().subtract(positionEntity).toVector().normalize().multiply(speed))
        val heightFrom = NMS.INSTANCE.getBlockHeight(entity.getLocation().block)
        val heightTo = NMS.INSTANCE.getBlockHeight(plan.block)
        if (heightTo == 0.0 || heightTo == heightFrom) {
            entity.teleport(plan.x, plan.y, plan.z)
        } else {
            val diff = (plan.blockY + heightTo) - entity.position.y
            if (diff > 0.5) {
                counterJump = 3
                entity.setTag("isJumping", "true")
                getGravity().isGravity = false
            } else {
                entity.teleport(plan.x, plan.y + heightTo, plan.z)
            }
        }
        if (entity.position.toLocation().distance(next) < speed) {
            i++
        }
    }

    fun getGravity(): GeneralGravity {
        return entity!!.getController(GeneralGravity::class)!!
    }
}