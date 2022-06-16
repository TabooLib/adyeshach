package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.bukkit.BukkitOcelotType
import org.bukkit.entity.Ocelot

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
abstract class AdyOcelot : AdyEntityAgeable() {

    open fun setTrusting(value: Boolean) {
        assert(minecraftVersion < 11400, "setTrusting")
        setMetadata("isTrusting", value)
    }

    open fun isTrusting(): Boolean {
        assert(minecraftVersion < 11400, "isTrusting")
        return getMetadata("isTrusting")
    }

    open fun setType(type: Ocelot.Type) {
        assert(minecraftVersion >= 11400, "setType")
        setMetadata("type", type.ordinal)
    }

    open fun getType(): BukkitOcelotType {
        assert(minecraftVersion >= 11400, "getType")
        return BukkitOcelotType.of(getMetadata("type"))
    }

    open fun setSitting(value: Boolean) {
        assert(minecraftVersion >= 11400, "setSitting", "CAT")
        setMetadata("isSitting", value)
    }

    open fun isSitting(): Boolean {
        assert(minecraftVersion >= 11400, "isSitting", "CAT")
        return getMetadata("isSitting")
    }

    open fun setAngry(value: Boolean) {
        assert(minecraftVersion >= 11400, "setAngry", "CAT")
        setMetadata("isAngry", value)
    }

    open fun isAngry(): Boolean {
        assert(minecraftVersion >= 11400, "isAngry", "CAT")
        return getMetadata("isAngry")
    }

    open fun setTamed(value: Boolean) {
        assert(minecraftVersion >= 11400, "setTamed", "CAT")
        setMetadata("isTamed", value)
    }

    open fun isTamed(): Boolean {
        assert(minecraftVersion >= 11400, "isTamed", "CAT")
        return getMetadata("isTamed")
    }
}