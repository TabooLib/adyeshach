package ink.ptms.adyeshach.core.entity.type

import ink.ptms.adyeshach.core.bukkit.BukkitOcelotType
import org.bukkit.entity.Ocelot

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
interface AdyOcelot : AdyEntityAgeable {

    @Deprecated("1.14 以下不支持")
    fun setTrusting(value: Boolean) {
        assert(minecraftVersion < 11400, "setTrusting")
        setMetadata("isTrusting", value)
    }

    @Deprecated("1.14 以下不支持")
    fun isTrusting(): Boolean {
        assert(minecraftVersion < 11400, "isTrusting")
        return getMetadata("isTrusting")
    }

    @Deprecated("1.14 以上不支持")
    fun setType(type: Ocelot.Type) {
        assert(minecraftVersion >= 11400, "setType")
        setMetadata("type", type.ordinal)
    }

    @Deprecated("1.14 以上不支持")
    fun getType(): BukkitOcelotType {
        assert(minecraftVersion >= 11400, "getType")
        return BukkitOcelotType.of(getMetadata("type"))
    }

    @Deprecated("1.14 以上不支持")
    fun setSitting(value: Boolean) {
        assert(minecraftVersion >= 11400, "setSitting", "CAT")
        setMetadata("isSitting", value)
    }

    @Deprecated("1.14 以上不支持")
    fun isSitting(): Boolean {
        assert(minecraftVersion >= 11400, "isSitting", "CAT")
        return getMetadata("isSitting")
    }

    @Deprecated("1.14 以上不支持")
    fun setAngry(value: Boolean) {
        assert(minecraftVersion >= 11400, "setAngry", "CAT")
        setMetadata("isAngry", value)
    }

    @Deprecated("1.14 以上不支持")
    fun isAngry(): Boolean {
        assert(minecraftVersion >= 11400, "isAngry", "CAT")
        return getMetadata("isAngry")
    }

    @Deprecated("1.14 以上不支持")
    fun setTamed(value: Boolean) {
        assert(minecraftVersion >= 11400, "setTamed", "CAT")
        setMetadata("isTamed", value)
    }

    @Deprecated("1.14 以上不支持")
    fun isTamed(): Boolean {
        assert(minecraftVersion >= 11400, "isTamed", "CAT")
        return getMetadata("isTamed")
    }
}