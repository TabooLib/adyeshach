package ink.ptms.adyeshach.common.entity.ai.general

import ink.ptms.adyeshach.api.nms.NMS
import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.ai.Controller
import ink.ptms.adyeshach.common.entity.path.PathType
import ink.ptms.adyeshach.common.entity.path.ResultNavigation
import io.izzel.taboolib.module.lite.SimpleCounter
import io.izzel.taboolib.util.lite.Effects
import org.bukkit.Location
import org.bukkit.Particle

/**
 * 实体移动逻辑
 *
 * @Author sky
 * @Since 2020-08-19 22:09
 */
class GeneralMove(entity: EntityInstance) : Controller(entity) {

    private var i = 0
    private var counterLook = SimpleCounter(2)
    private var counterJump = SimpleCounter(5, true)

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
        if (entity!!.isControllerJumping()) {
            if (counterJump.next()) {
                entity.removeTag("isJumping")
                getGravity().isGravity = true
            } else {
                entity.teleport(entity.position.x, entity.position.y + 0.25, entity.position.z)
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
            Location(entity.position.world, x.toDouble() + 0.5, y.toDouble(), z.toDouble() + 0.5)
        }
        if (counterLook.next()) {
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
        if (NMS.INSTANCE.getBlockHeight(plan.block) == 0.0) {
            entity.teleport(plan.x, plan.y, plan.z)
        } else if (pathType != PathType.FLY) {
            entity.setTag("isJumping", "true")
            getGravity().isGravity = false
        }
        if (entity.position.toLocation().distance(next) < speed) {
            i++
        }
    }

    fun getGravity(): GeneralGravity {
        return entity!!.getController(GeneralGravity::class)!!
    }
}