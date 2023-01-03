package ink.ptms.adyeshach.api.event

import ink.ptms.adyeshach.common.entity.EntityInstance
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.platform.type.BukkitProxyEvent

/**
 * @author sky
 * @since 2020-08-14 19:21
 */
@Deprecated("Outdated and unavailable")
class AdyeshachHeadRotationEvent : BukkitProxyEvent() {

    val entity: EntityInstance
        get() = error("Outdated api is being called, please contact the plugin author to update.")

    var yaw: Float
        get() = error("Outdated api is being called, please contact the plugin author to update.")
        set(value) = error("Outdated api is being called, please contact the plugin author to update.")

    var pitch: Float
        get() = error("Outdated api is being called, please contact the plugin author to update.")
        set(value) = error("Outdated api is being called, please contact the plugin author to update.")

    companion object {

        @Awake(LifeCycle.ACTIVE)
        fun legacy() {
            AdyeshachHeadRotationEvent().call()
        }
    }
}