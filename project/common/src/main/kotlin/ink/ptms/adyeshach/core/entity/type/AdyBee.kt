package ink.ptms.adyeshach.core.entity.type

/**
 * @author Arasple
 * @date 2020/8/4 22:18
 */
interface AdyBee : AdyEntityAgeable {

    fun setUnUsed(unused: Boolean) {
        setMetadata("unUsed", unused)
    }

    fun isUnUsed(): Boolean {
        return getMetadata("unUsed")
    }

    fun setFlip(anger: Boolean) {
        setMetadata("isFlip", anger)
    }

    fun isFlip(): Boolean {
        return getMetadata("isFlip")
    }

    fun setStung(stung: Boolean) {
        setMetadata("hasStung", stung)
    }

    fun hasStung(): Boolean {
        return getMetadata("hasStung")
    }

    fun setNectar(nectar: Boolean) {
        setMetadata("hasNectar", nectar)
    }

    fun hasNectar(): Boolean {
        return getMetadata("hasNectar")
    }

    fun setAngered(value: Boolean) {
        setMetadata("angerTicks", if (value) 999 else 0)
    }

    fun isAngered(): Boolean {
        return getMetadata<Int>("angerTicks") > 0
    }
}