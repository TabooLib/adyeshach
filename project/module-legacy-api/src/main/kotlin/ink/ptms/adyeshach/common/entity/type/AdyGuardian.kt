package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityTypes

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
@Deprecated("Outdated but usable")
open class AdyGuardian(entityTypes: EntityTypes = EntityTypes.GUARDIAN, v2: ink.ptms.adyeshach.core.entity.EntityInstance) : AdyMob(entityTypes, v2) {

    fun setRetractingSpikes(value: Boolean) {
        setMetadata("isRetractingSpikes", value)
    }

    fun isRetractingSpikes(): Boolean {
        return getMetadata("isRetractingSpikes")
    }

    fun setTargetEntityId(id: Int) {
        setMetadata("targetEntityId", id)
    }

    fun getTargetEntityId(): Int {
        return getMetadata("targetEntityId")
    }

    fun setElderly(value: Boolean) {
        if (minecraftVersion >= 11100) {
            error("Metadata \"isElderly\" not supported this minecraft version. Use \"AdyElderGuardian\" instead")
        }
        setMetadata("isElderly", value)
    }

    fun isElderly(): Boolean {
        if (minecraftVersion >= 11100) {
            error("Metadata \"isElderly\" not supported this minecraft version. Use \"AdyElderGuardian\" instead")
        }
        return getMetadata("isElderly")
    }
}