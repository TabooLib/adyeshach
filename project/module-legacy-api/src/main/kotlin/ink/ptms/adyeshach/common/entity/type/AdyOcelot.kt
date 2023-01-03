package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.bukkit.BukkitOcelotType

import ink.ptms.adyeshach.common.entity.EntityTypes
import org.bukkit.entity.Ocelot

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
@Deprecated("Outdated but usable")
class AdyOcelot(v2: ink.ptms.adyeshach.core.entity.EntityInstance):  AdyEntityAgeable(EntityTypes.OCELOT, v2) {

    fun setTrusting(value: Boolean) {
        if (minecraftVersion < 11400) {
            error("Metadata \"isTrusting\" not supported this minecraft version.")
        }
        setMetadata("isTrusting", value)
    }

    fun isTrusting(): Boolean {
        if (minecraftVersion < 11400) {
            error("Metadata \"isTrusting\" not supported this minecraft version.")
        }
        return getMetadata("isTrusting")
    }

    fun setType(type: Ocelot.Type) {
        if (minecraftVersion >= 11400) {
            error("Metadata \"type\" not supported this minecraft version.")
        }
        setMetadata("type", type.ordinal)
    }

    fun getType(): BukkitOcelotType {
        if (minecraftVersion >= 11400) {
            error("Metadata \"type\" not supported this minecraft version.")
        }
        return BukkitOcelotType.of(getMetadata("type"))
    }

    fun setSitting(value: Boolean) {
        if (minecraftVersion >= 11100) {
            error("Metadata \"isSitting\" not supported this minecraft version. Use \"AdyCat\" instead")
        }
        setMetadata("isSitting", value)
    }

    fun isSitting(): Boolean {
        if (minecraftVersion >= 11400) {
            error("Metadata \"isSitting\" not supported this minecraft version. Use \"AdyCat\" instead")
        }
        return getMetadata("isSitting")
    }

    fun setAngry(value: Boolean) {
        if (minecraftVersion >= 11400) {
            error("Metadata \"isAngry\" not supported this minecraft version. Use \"AdyCat\" instead")
        }
        setMetadata("isAngry", value)
    }

    fun isAngry(): Boolean {
        if (minecraftVersion >= 11400) {
            error("Metadata \"isAngry\" not supported this minecraft version. Use \"AdyCat\" instead")
        }
        return getMetadata("isAngry")
    }

    fun setTamed(value: Boolean) {
        if (minecraftVersion >= 11400) {
            error("Metadata \"isTamed\" not supported this minecraft version. Use \"AdyCat\" instead")
        }
        setMetadata("isTamed", value)
    }

    fun isTamed(): Boolean {
        if (minecraftVersion >= 11400) {
            error("Metadata \"isTamed\" not supported this minecraft version. Use \"AdyCat\" instead")
        }
        return getMetadata("isTamed")
    }
}