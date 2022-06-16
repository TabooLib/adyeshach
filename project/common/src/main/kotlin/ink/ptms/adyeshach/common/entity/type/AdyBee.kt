package ink.ptms.adyeshach.common.entity.type

/**
 * @author Arasple
 * @date 2020/8/4 22:18
 */
abstract class AdyBee : AdyEntityAgeable() {

    open fun setUnUsed(unused: Boolean) {
        setMetadata("unUsed", unused)
    }

    open fun isUnUsed(): Boolean {
        return getMetadata("unUsed")
    }

    open fun setFlip(anger: Boolean) {
        setMetadata("isFlip", anger)
    }

    open fun isFlip(): Boolean {
        return getMetadata("isFlip")
    }

    open fun setStung(stung: Boolean) {
        setMetadata("hasStung", stung)
    }

    open fun hasStung(): Boolean {
        return getMetadata("hasStung")
    }

    open fun setNectar(nectar: Boolean) {
        setMetadata("hasNectar", nectar)
    }

    open fun hasNectar(): Boolean {
        return getMetadata("hasNectar")
    }

    open fun setAngered(value: Boolean) {
        setMetadata("angerTicks", if (value) 999 else 0)
    }

    open fun isAngered(): Boolean {
        return getMetadata<Int>("angerTicks") > 0
    }
}