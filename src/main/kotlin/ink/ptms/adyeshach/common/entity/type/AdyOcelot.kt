package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.bukkit.BukkitOcelotType

import ink.ptms.adyeshach.common.entity.EntityTypes
import org.bukkit.entity.Ocelot

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
class AdyOcelot : AdyEntityAgeable(EntityTypes.OCELOT) {

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
//        mask(at(11400 to -1, 11000 to 13, 10900 to 12), "isSitting", 0x01)
//        mask(at(11400 to -1, 11000 to 13, 10900 to 12), "isAngry", 0x02)
//        mask(at(11400 to -1, 11000 to 13, 10900 to 12), "isTamed", 0x04)
//
//        if (minecraftVersion >= 11400) {
//            natural(at(11700 to 17, 11500 to 16, 11400 to 15), "isTrusting", false)
//        } else {
//            natural(at(11000 to 15, 10900 to 14), "type", BukkitOcelotType.UNTAMED.ordinal)
//                    .from(Editors.enums(BukkitOcelotType::class) { _, entity, meta, index, _ -> "/adyeshachapi edit int ${entity.uniqueId} ${meta.key} $index" })
//                    .display { _, entity, _ ->
//                        BukkitOcelotType.values()[entity.getMetadata("type")].name
//                    }.build()
//        }
    }

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