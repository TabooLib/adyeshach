package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.bukkit.BukkitOcelotType
import org.bukkit.entity.Ocelot

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
interface AdyOcelot : AdyEntityAgeable {

    fun setTrusting(value: Boolean) {
        assert(minecraftVersion < 11400, "setTrusting")
        setMetadata("isTrusting", value)
    }

    fun isTrusting(): Boolean {
        assert(minecraftVersion < 11400, "isTrusting")
        return getMetadata("isTrusting")
    }

    fun setType(type: Ocelot.Type) {
        assert(minecraftVersion >= 11400, "setType")
        setMetadata("type", type.ordinal)
    }

    fun getType(): BukkitOcelotType {
        assert(minecraftVersion >= 11400, "getType")
        return BukkitOcelotType.of(getMetadata("type"))
    }

    fun setSitting(value: Boolean) {
        assert(minecraftVersion >= 11400, "setSitting", "CAT")
        setMetadata("isSitting", value)
    }

    fun isSitting(): Boolean {
        assert(minecraftVersion >= 11400, "isSitting", "CAT")
        return getMetadata("isSitting")
    }

    fun setAngry(value: Boolean) {
        assert(minecraftVersion >= 11400, "setAngry", "CAT")
        setMetadata("isAngry", value)
    }

    fun isAngry(): Boolean {
        assert(minecraftVersion >= 11400, "isAngry", "CAT")
        return getMetadata("isAngry")
    }

    fun setTamed(value: Boolean) {
        assert(minecraftVersion >= 11400, "setTamed", "CAT")
        setMetadata("isTamed", value)
    }

    fun isTamed(): Boolean {
        assert(minecraftVersion >= 11400, "isTamed", "CAT")
        return getMetadata("isTamed")
    }
}