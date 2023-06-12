package ink.ptms.adyeshach.core.entity.type

/**
 * @author sky
 * @since 2020-08-04 19:30
 */
interface AdyCamel : AdyHorseBase {

    fun setDashing(dashing: Boolean) {
        setMetadata("isDashing", dashing)
    }

    fun isDashing(): Boolean {
        return getMetadata("isDashing")
    }

    fun setLastPoseChangeTick(lastPoseChangeTick: Int) {
        setMetadata("lastPoseChangeTick", lastPoseChangeTick)
    }

    fun getLastPoseChangeTick(): Int {
        return getMetadata("lastPoseChangeTick")
    }
}