package ink.ptms.adyeshach.common.entity.type

/**
 * @author sky
 * @since 2020-08-04 18:28
 */
abstract class AdyFish : AdyMob() {

    open fun setFromBucket(value: Boolean) {
        setMetadata("fromBucket", value)
    }

    open fun isFromBucket(): Boolean {
        return getMetadata("fromBucket")
    }
}