package ink.ptms.adyeshach.common.entity.type

/**
 * @author Arasple
 * @date 2020/8/16 16:10
 * before 1.16 known as Insentient
 */
@Suppress("SpellCheckingInspection")
abstract class AdyMob : AdyEntityLiving() {

    /**
     * 设置为左撇子
     */
    open fun setLeftHanded(leftHanded: Boolean) {
        setMetadata("isLeftHanded", leftHanded)
    }

    /**
     * 是否为左撇子
     */
    open fun isLeftHanded(): Boolean {
        return getMetadata("isLeftHanded")
    }

    /**
     * 设置为好斗的
     */
    open fun setAgressive(agressive: Boolean) {
        setMetadata("isAgressive", agressive)
    }

    /**
     * 是否为好斗的
     */
    open fun isAgressive(): Boolean {
        return getMetadata("isAgressive")
    }
}