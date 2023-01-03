package ink.ptms.adyeshach.api.event

import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.MetaNatural
import org.bukkit.entity.Player
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.platform.type.BukkitProxyEvent

/**
 * @author sky
 * @since 2020-08-14 19:21
 */
@Deprecated("Outdated and unavailable")
class AdyeshachNaturalMetaGenerateEvent : BukkitProxyEvent() {

    override val allowCancelled: Boolean
        get() = false

    val entity: EntityInstance
        get() = error("Outdated api is being called, please contact the plugin author to update.")

    val player: Player
        get() = error("Outdated api is being called, please contact the plugin author to update.")

    val meta: MetaNatural<*, EntityInstance>
        get() = error("Outdated api is being called, please contact the plugin author to update.")

    var value: Any
        get() = error("Outdated api is being called, please contact the plugin author to update.")
        set(value) = error("Outdated api is being called, please contact the plugin author to update.")

    companion object {

        @Awake(LifeCycle.ACTIVE)
        fun legacy() {
            AdyeshachNaturalMetaGenerateEvent().call()
        }
    }
}