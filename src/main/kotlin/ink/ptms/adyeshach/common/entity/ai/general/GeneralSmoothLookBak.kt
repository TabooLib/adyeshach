package ink.ptms.adyeshach.common.entity.ai.general

import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.ai.Controller
import io.izzel.taboolib.module.lite.SimpleCounter

/**
 * 纪念 a老板的初代 SmoothLook 可以退休了
 *
 * @Author sky
 * @Since 2020-08-19 22:09
 */
private class GeneralSmoothLookBak(entity: EntityInstance) : Controller(entity) {

    /**
     * 平滑移动视角的周期 (ticks)
     */
    var speed = 6
        set(value) {
            field = value
            init()
        }

    var yaw = 0f
        set(value) {
            field = value
            init()
        }

    var pitch = 0f
        set(value) {
            field = value
            init()
        }

    var isLooking = false

    private var deltaYaw = 0f
    private var deltaPitch = 0f
    private var counterSmoothLook = SimpleCounter(speed, true)

    override fun shouldExecute(): Boolean {
        return isLooking
    }

    override fun onTick() {
        if (counterSmoothLook.next()) {
            isLooking = false
            counterSmoothLook.reset()
        } else {
            entity!!.setHeadRotation(entity.position.yaw + deltaYaw, entity.position.pitch + deltaPitch)
        }
    }

    private fun init() {
        deltaYaw = (yaw - entity!!.position.yaw) / speed
        deltaPitch = (pitch - entity.position.pitch) / speed
        counterSmoothLook = SimpleCounter(speed, true)
    }
}