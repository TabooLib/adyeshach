package ink.ptms.adyeshach.core.entity.type

/**
 * @author sky
 * @since 2020-08-04 19:30
 */
interface AdyBlaze : AdyMob {

    fun setFire(value: Boolean) {
        setMetadata("fire", value)
    }

    fun isFire(): Boolean {
        return getMetadata("fire")
    }
}