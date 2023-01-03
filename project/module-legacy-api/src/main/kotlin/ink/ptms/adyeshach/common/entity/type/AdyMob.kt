package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityTypes

/**
 * @author Arasple
 * @date 2020/8/16 16:10
 * before 1.16 known as Insentient
 */
@Deprecated("Outdated but usable")
abstract class AdyMob(entityTypes: EntityTypes, v2: ink.ptms.adyeshach.core.entity.EntityInstance) : AdyEntityLiving(entityTypes, v2) {

    fun setLeftHanded(leftHanded: Boolean) {
        setMetadata("isLeftHanded", leftHanded)
    }

    fun isLeftHanded(): Boolean {
        return getMetadata("isLeftHanded")
    }

    fun setAgressive(agressive: Boolean) {
        setMetadata("isAgressive", agressive)
    }

    fun isAgressive(): Boolean {
        return getMetadata("isAgressive")
    }
}