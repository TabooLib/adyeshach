package ink.ptms.adyeshach.common.entity.type.impl

import ink.ptms.adyeshach.common.entity.type.EntityTypes
import ink.ptms.adyeshach.api.nms.NMS
import ink.ptms.adyeshach.common.bukkit.BukkitCreeperState
import io.izzel.taboolib.internal.gson.annotations.Expose
import org.bukkit.entity.Player

/**
 * @Author sky
 * @Since 2020-08-04 19:30
 */
class AdyDolphin(owner: Player) : AdyEntityLiving(owner, EntityTypes.DOLPHIN) {

    init {
        registerMeta(16, "findTreasure", false)
        registerMeta(17, "hasFish", false)
    }

    fun setFindTreasure(value: Boolean) {
        setMetadata("findTreasure", value)
    }

    fun isFindTreasure(): Boolean {
        return getMetadata("findTreasure")
    }

    fun setHasFish(value: Boolean) {
        setMetadata("hasFish", value)
    }

    fun isHasFish(): Boolean {
        return getMetadata("hasFish")
    }
}