package ink.ptms.adyeshach.api.event

import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.Meta
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.platform.type.BukkitProxyEvent

/**
 * @author sky
 * @since 2020-08-14 19:21
 */
@Deprecated("Outdated and unavailable")
class AdyeshachMetaUpdateEvent : BukkitProxyEvent() {

    val entity: EntityInstance
        get() = error("Outdated api is being called, please contact the plugin author to update.")

    val meta: Meta<*>
        get() = error("Outdated api is being called, please contact the plugin author to update.")

    val key: String
        get() = error("Outdated api is being called, please contact the plugin author to update.")

    var value: Any
        get() = error("Outdated api is being called, please contact the plugin author to update.")
        set(value) = error("Outdated api is being called, please contact the plugin author to update.")

    companion object {

        @Awake(LifeCycle.ACTIVE)
        fun legacy() {
            AdyeshachMetaUpdateEvent().call()
        }
    }
}