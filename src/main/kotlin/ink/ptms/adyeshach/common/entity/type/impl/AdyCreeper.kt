package ink.ptms.adyeshach.common.entity.type.impl

import ink.ptms.adyeshach.common.entity.type.EntityTypes
import ink.ptms.adyeshach.api.nms.NMS
import ink.ptms.adyeshach.common.bukkit.BukkitCreeperState
import io.izzel.taboolib.internal.gson.annotations.Expose
import org.bukkit.entity.Creeper
import org.bukkit.entity.Player

/**
 * @Author sky
 * @Since 2020-08-04 19:30
 */
class AdyCreeper(owner: Player) : AdyEntityLiving(owner, EntityTypes.CREEPER) {

    init {
        /**
         * 1.12 -> 12,13,14
         * 1.9 -> 11,12,13
         */
        registerMeta(15, "state", BukkitCreeperState.IDLE.ordinal)
        registerMeta(16, "isCharged", false)
        registerMeta(17, "isIgnited", false)
    }

    fun setState(value: BukkitCreeperState) {
        setMetadata("state", value.value)
    }

    fun getState(): BukkitCreeperState {
        return BukkitCreeperState.of(getMetadata("state"))
    }

    fun setCharged(value: Boolean) {
        setMetadata("isCharged", value)
    }

    fun isCharged(): Boolean {
        return getMetadata("isCharged")
    }

    fun setIgnited(value: Boolean) {
        setMetadata("isIgnited", value)
    }

    fun isIgnited(): Boolean {
        return getMetadata("isIgnited")
    }
}