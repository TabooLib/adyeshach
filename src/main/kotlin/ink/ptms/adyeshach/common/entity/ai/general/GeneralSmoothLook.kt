package ink.ptms.adyeshach.common.entity.ai.general

import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.ai.Pathfinder
import ink.ptms.adyeshach.common.util.Tasks
import org.bukkit.scheduler.BukkitTask

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
    var speed = 25
    var task: BukkitTask? = null

    override fun shouldExecute(): Boolean {
        return isLooking
    }

    override fun onTick() {
        if (shouldExecute() && task == null) {
            val deltaYaw = (yaw - entity.position.yaw) / speed
            val deltaPitch = (pitch - entity.position.pitch) / speed

            println("SmoothLook: {${entity.position.yaw}, ${entity.position.pitch}} to {$yaw, $pitch} with Delta: {$deltaYaw, $deltaPitch}")

            task = Tasks.timer(0, 1, true) {

                entity.setHeadRotation(entity.position.yaw + deltaYaw, entity.position.pitch + deltaPitch)
            }

            Tasks.delay(speed.toLong(), true) {
                isLooking = false
                entity.setHeadRotation(yaw, pitch)
                task?.cancel()
                task = null
            }
        }
    }
}