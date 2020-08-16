package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityTypes

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
open class AdyGuardian(entityTypes: EntityTypes = EntityTypes.GUARDIAN) : AdyMob(entityTypes) {

    init {
        /**
         * 1.9, 1.10 -> no Elder Guardian entityType, require `isElderly` meta
         */
        val index = at(11500 to 15, 11400 to 14, 11000 to 12, 10900 to 11)

        registerMeta(index, "isRetractingSpikes", at(11100 to false, 0 to 0x02))
        registerMeta(at(11100 to -1, 0 to index), "isElderly", 0x04)
        registerMeta(index + 1, "targetEntityId", -1)
    }

    fun setRetractingSpikes(value: Boolean) {
        setMetadata("isRetractingSpikes", value)
    }

    fun isRetractingSpikes(): Boolean {
        return getMetadata("isRetractingSpikes")
    }

    fun setElderly(value: Boolean) {
        if (version >= 11100) {
            throw RuntimeException("Metadata \"isElderly\" not supported this minecraft version. Use \"AdyElderGuardian\" instead")
        }
        setMetadata("isElderly", value)
    }

    fun isElderly(): Boolean {
        if (version >= 11100) {
            throw RuntimeException("Metadata \"isElderly\" not supported this minecraft version. Use \"AdyElderGuardian\" instead")
        }
        return getMetadata("isElderly")
    }

    fun setTargetEntityId(id: Int) {
        setMetadata("targetEntityId", id)
    }

    fun getTargetEntityId(): Int {
        return getMetadata("targetEntityId")
    }

}