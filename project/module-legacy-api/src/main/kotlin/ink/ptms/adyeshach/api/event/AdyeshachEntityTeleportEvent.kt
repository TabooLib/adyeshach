package ink.ptms.adyeshach.api.event

import ink.ptms.adyeshach.common.entity.EntityInstance
import org.bukkit.Location
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.platform.type.BukkitProxyEvent

/**
 * @author sky
 * @since 2020-08-14 19:21
 */
@Deprecated("Outdated and unavailable")
class AdyeshachEntityTeleportEvent : BukkitProxyEvent() {

    val entity: EntityInstance
        get() = error("Outdated api is being called, please contact the plugin author to update.")

    var location: Location
        get() = error("Outdated api is being called, please contact the plugin author to update.")
        set(value) = error("Outdated api is being called, please contact the plugin author to update.")

    companion object {

        @Awake(LifeCycle.ACTIVE)
        fun legacy() {
            AdyeshachEntityTeleportEvent().call()
        }
    }
}