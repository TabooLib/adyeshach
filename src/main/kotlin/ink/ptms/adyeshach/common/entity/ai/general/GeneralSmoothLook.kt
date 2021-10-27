package ink.ptms.adyeshach.common.entity.ai.general

import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.ai.Controller
import taboolib.common5.Baffle

/**
 * @author sky
 * @since 2020-08-19 22:09
 */
class GeneralSmoothLook(entity: EntityInstance) : Controller(entity) {

    /*
        -180  -135  -90  -45  0  +45  +90  +135  +180
     */

    var yaw = 0f
        set(value) {
            field = normalizeYaw(value)
        }

    var pitch = 0f
    var interval = 22.5f

    var isReset = true
    var isLooking = false

    private var t = 0f
    private var y = 0f
    private var i = true
    private var counter = Baffle.of(2)

    override fun shouldExecute(): Boolean {
        return isLooking && !entity!!.isControllerMoving()
    }

    override fun onTick() {
        if (counter.hasNext()) {
            t = entity!!.position.yaw
            if (isReset) {
                isReset = false
                if (yaw.coerceAtLeast(t) - yaw.coerceAtMost(t) > 180) {
                    y = if (yaw > t) {
                        if (t > 0) {
                            180f * 2 + t
                        } else {
                            -180f * 2 - t
                        }
                    } else {
                        yaw
                    }
                } else {
                    y = yaw
                    i = yaw > t
                }
            }
            t = if (i) {
                if (t + interval >= y) {
                    isReset = true
                    isLooking = false
                    y
                } else {
                    t + interval
                }
            } else {
                if (t - interval <= y) {
                    isReset = true
                    isLooking = false
                    y
                } else {
                    t - interval
                }
            }
            // 防止大陀螺
            // 写了9个小时还是有陀螺bug，爷佛了
            val normalizeYaw = normalizeYaw(t)
            if (normalizeYaw.coerceAtLeast(yaw) - normalizeYaw.coerceAtMost(yaw) < interval) {
                t = yaw
                isReset = true
                isLooking = false
            }
            entity.setHeadRotation(normalizeYaw(t), pitch)
        }
    }

    fun normalizeYaw(yaw: Float): Float {
        var y = yaw
        y %= 360.0f
        if (y >= 180.0f) {
            y -= 360.0f
        } else if (y < -180.0f) {
            y += 360.0f
        }
        return y
    }
}