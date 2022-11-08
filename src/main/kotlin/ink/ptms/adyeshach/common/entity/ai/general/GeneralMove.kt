package ink.ptms.adyeshach.common.entity.ai.general

import ink.ptms.adyeshach.api.nms.NMS
import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.ai.Controller
import ink.ptms.adyeshach.common.entity.path.PathType
import ink.ptms.adyeshach.common.entity.path.ResultNavigation
import ink.ptms.adyeshach.common.util.safeDistance
import ink.ptms.adyeshach.common.util.signal
import org.bukkit.Location
import taboolib.common5.Baffle
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CopyOnWriteArrayList

/**
 * 实体移动逻辑
 *
 * @author sky
 * @since 2020-08-19 22:09
 */
class GeneralMove(entity: EntityInstance) : Controller(entity) {

    private var i = 0
    private var counterLook = Baffle.of(2)
    private var counterJump = 0

    var speed = 0.0
        set(value) {
            field = value.coerceAtMost(1.0)
        }

    var target: Location? = null
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
        // 移动
        val positionEntity = entity.position.toLocation()
        val positionNext = resultNavigation!!.pointList[i].run {
            Location(entity.position.world, x + 0.5, y, z + 0.5)
        }
        // 看向移动位置
        if (counterLook.hasNext()) {
            val lookPos = positionNext.clone()
            lookPos.y = positionEntity.y + (entity.entityType.entitySize.height * 0.9)
            entity.controllerLook(lookPos)
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
            entity.teleportFuture(plan.x, plan.y, plan.z)
        } else {
            val diff = (plan.blockY + heightTo) - entity.position.y
            if (diff > 0.5) {
                counterJump = 3
                entity.setTag("isJumping", "true")
                getGravity().isGravity = false
            } else {
                entity.teleportFuture(plan.x, plan.y + heightTo, plan.z)
            }
        }
        if (entity.position.toLocation().safeDistance(next) < speed) {
            i++
        }
    }

    fun getGravity(): GeneralGravity {
        return entity!!.getController(GeneralGravity::class.java)!!
    }
}