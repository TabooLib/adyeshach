package ink.ptms.adyeshach.common.entity.type

/**
 * @author sky
 * @since 2020-08-04 19:30
 */
abstract class AdyBlaze : AdyMob() {

    open fun setFire(value: Boolean) {
        setMetadata("fire", value)
    }

    open fun isFire(): Boolean {
        return getMetadata("fire")
    }
}