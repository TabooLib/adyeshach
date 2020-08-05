package ink.ptms.adyeshach.common.entity.type.impl

import ink.ptms.adyeshach.common.entity.type.EntityTypes
import ink.ptms.adyeshach.api.nms.NMS
import io.izzel.taboolib.internal.gson.annotations.Expose
import org.bukkit.entity.Player

/**
 * @author Arasple
 * @date 2020/8/4 22:18
 */
class AdyBee(owner: Player) : AdyEntityLiving(owner, EntityTypes.BEE) {

    init {
        registerMetaByteMask(16, "unUsed", 0x01)
        registerMetaByteMask(16, "isFlip", 0x02)
        registerMetaByteMask(16, "hasStung", 0x04)
        registerMetaByteMask(16, "hasNectar", 0x08)
        registerMetaByteMask(17, "angerTicks", 0)
    }

    fun setUnUsed(unused: Boolean) {
        setMetadata("unUsed", unused)
    }

    fun isUnUsed(): Boolean {
        return getMetadata("unUsed")
    }

    fun setFlip(anger: Boolean) {
        setMetadata("isFlip", anger)
    }

    fun isFlip(): Boolean {
        return getMetadata("isFlip")
    }

    fun setStung(stung: Boolean) {
        setMetadata("hasStung", stung)
    }

    fun hasStung(): Boolean {
        return getMetadata("hasStung")
    }

    fun setNectar(nectar: Boolean) {
        setMetadata("hasNectar", nectar)
    }

    fun hasNectar(): Boolean {
        return getMetadata("hasNectar")
    }

    fun setAngered(value: Boolean) {
        setMetadata("angerTicks", if (value) 999 else 0)
    }

    fun isAngered(): Boolean {
        return getMetadata<Int>("angerTicks") > 0
    }


}