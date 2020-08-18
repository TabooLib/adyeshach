package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.bukkit.BukkitOcelotType
import ink.ptms.adyeshach.common.entity.EntityTypes
import org.bukkit.entity.Ocelot

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
class AdyOcelot() : AdyEntityAgeable(EntityTypes.OCELOT) {

    init {
        /*
        1.16,1.15
        16 ->Is trusting
        1.14
        15 ->Is trusting
        1.13,1.12,1.11
        15 ->Type (0 = untamed, 1 = tuxedo, 2 = tabby, 3 = siamese). Used to render regardless as to whether it is tamed or not.
        1.10
        15 ->Type
        1.9
        14 ->Type
         */
        registerMetaByteMask(at(11400 to -1, 11000 to 13, 10900 to 12), "isSitting", 0x01)
        registerMetaByteMask(at(11400 to -1, 11000 to 13, 10900 to 12), "isAngry", 0x02)
        registerMetaByteMask(at(11400 to -1, 11000 to 13, 10900 to 12), "isTamed", 0x04)

        if (version >= 11400) {
            registerMeta(at(11500 to 16, 11400 to 15), "isTrusting", false)
        } else {
            registerMeta(at(11000 to 15, 10900 to 14), "type", BukkitOcelotType.UNTAMED.ordinal)
        }
    }

    fun setTrusting(value: Boolean) {
        if (version < 11400) {
            throw RuntimeException("Metadata \"isTrusting\" not supported this minecraft version.")
        }
        setMetadata("isTrusting", value)
    }

    fun isTrusting(): Boolean {
        if (version < 11400) {
            throw RuntimeException("Metadata \"isTrusting\" not supported this minecraft version.")
        }
        return getMetadata("isTrusting")
    }

    fun setType(type: Ocelot.Type) {
        if (version >= 11400) {
            throw RuntimeException("Metadata \"type\" not supported this minecraft version.")
        }
        setMetadata("type", type.ordinal)
    }

    fun getType(): BukkitOcelotType {
        if (version >= 11400) {
            throw RuntimeException("Metadata \"type\" not supported this minecraft version.")
        }
        return BukkitOcelotType.of(getMetadata("type"))
    }

    fun setSitting(value: Boolean) {
        if (version >= 11100) {
            throw RuntimeException("Metadata \"isSitting\" not supported this minecraft version. Use \"AdyCat\" instead")
        }
        setMetadata("isSitting", value)
    }

    fun isSitting(): Boolean {
        if (version >= 11400) {
            throw RuntimeException("Metadata \"isSitting\" not supported this minecraft version. Use \"AdyCat\" instead")
        }
        return getMetadata("isSitting")
    }

    fun setAngry(value: Boolean) {
        if (version >= 11400) {
            throw RuntimeException("Metadata \"isAngry\" not supported this minecraft version. Use \"AdyCat\" instead")
        }
        setMetadata("isAngry", value)
    }

    fun isAngry(): Boolean {
        if (version >= 11400) {
            throw RuntimeException("Metadata \"isAngry\" not supported this minecraft version. Use \"AdyCat\" instead")
        }
        return getMetadata("isAngry")
    }

    fun setTamed(value: Boolean) {
        if (version >= 11400) {
            throw RuntimeException("Metadata \"isTamed\" not supported this minecraft version. Use \"AdyCat\" instead")
        }
        setMetadata("isTamed", value)
    }

    fun isTamed(): Boolean {
        if (version >= 11400) {
            throw RuntimeException("Metadata \"isTamed\" not supported this minecraft version. Use \"AdyCat\" instead")
        }
        return getMetadata("isTamed")
    }
}