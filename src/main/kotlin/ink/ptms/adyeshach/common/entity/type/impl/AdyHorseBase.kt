package ink.ptms.adyeshach.common.entity.type.impl

import ink.ptms.adyeshach.common.entity.type.EntityTypes
import ink.ptms.adyeshach.api.nms.NMS
import org.bukkit.entity.Player
import java.lang.RuntimeException

/**
 * @Author sky
 * @Since 2020-08-04 18:28
 */
abstract class AdyHorseBase(owner: Player, entityTypes: EntityTypes) : AdyEntityAgeable(owner, entityTypes) {

    init {
        /**
         * 1.15 -> 16
         * 1.14 -> 15
         * 1.13 -> 13
         * -- (no chested horse)
         * 1.12 -> 13
         * 1.11 -> 13
         * -- (no abstract horse)
         * 1.10 -> 13
         * 1.9 -> 12
         */
        if (version >= 11300) {
            registerMetaByteMask(at(11500 to 16, 11400 to 15, 11300 to 13), "isTamed", 0x02)
            registerMetaByteMask(at(11500 to 16, 11400 to 15, 11300 to 13), "isSaddled", 0x04)
            registerMetaByteMask(at(11500 to 16, 11400 to 15, 11300 to 13), "hasBred", 0x08)
            registerMetaByteMask(at(11500 to 16, 11400 to 15, 11300 to 13), "isEating", 0x10)
            registerMetaByteMask(at(11500 to 16, 11400 to 15, 11300 to 13), "isRearing", 0x20)
            registerMetaByteMask(at(11500 to 16, 11400 to 15, 11300 to 13), "isMouthOpen", 0x40)
        } else {
            registerMetaByteMask(at(11100 to 13, 10900 to 12), "isTamed", 0x02)
            registerMetaByteMask(at(11100 to 13, 10900 to 12), "isSaddled", 0x04)
            registerMetaByteMask(at(11100 to 13, 10900 to 12), "hasChest", 0x08)
            registerMetaByteMask(at(11100 to 13, 10900 to 12), "hasBred", 0x10)
            registerMetaByteMask(at(11100 to 13, 10900 to 12), "isEating", 0x20)
            registerMetaByteMask(at(11100 to 13, 10900 to 12), "isRearing", 0x40)
            registerMetaByteMask(at(11100 to 13, 10900 to 12), "isMouthOpen", 0x80.toByte())
        }
    }

    fun setTamed(value: Boolean) {
        setMetadata("isTamed", value)
    }

    fun isTamed(): Boolean {
        return getMetadata("isTamed")
    }

    fun setSaddled(value: Boolean) {
        setMetadata("isSaddled", value)
    }

    fun isSaddled(): Boolean {
        return getMetadata("isSaddled")
    }

    fun setHasBred(value: Boolean) {
        setMetadata("hasBred", value)
    }

    fun isHasBred(): Boolean {
        return getMetadata("hasBred")
    }

    fun setEating(value: Boolean) {
        setMetadata("isEating", value)
    }

    fun isEating(): Boolean {
        return getMetadata("isEating")
    }

    fun setRearing(value: Boolean) {
        setMetadata("isRearing", value)
    }

    fun isRearing(): Boolean {
        return getMetadata("isRearing")
    }

    fun setMouthOpen(value: Boolean) {
        setMetadata("isMouthOpen", value)
    }

    fun isMouthOpen(): Boolean {
        return getMetadata("isMouthOpen")
    }

    open fun setHasChest(value: Boolean) {
        if (version >= 11300) {
            throw RuntimeException("Metadata \"hasChest\" not supported this minecraft version.")
        }
        setMetadata("hasChest", value)
    }

    open fun isHasChest(): Boolean {
        if (version >= 11300) {
            throw RuntimeException("Metadata \"hasChest\" not supported this minecraft version.")
        }
        return getMetadata("hasChest")
    }
}