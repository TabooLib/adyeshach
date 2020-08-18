package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityTypes

/**
 * @author Arasple
 * @date 2020/8/16 16:10
 * before 1.16 known as Insentient
 */
abstract class AdyMob(entityTypes: EntityTypes) : AdyEntityLiving(entityTypes) {

    init {
        registerMeta(at(11500 to 14, 11400 to 13, 11000 to 11), "isLeftHanded", 0x02)
        registerMeta(at(11600 to 14), "isAgressive", 0x04)
    }

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