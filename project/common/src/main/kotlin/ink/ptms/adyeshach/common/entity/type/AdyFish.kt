package ink.ptms.adyeshach.common.entity.type

/**
 * @author sky
 * @since 2020-08-04 18:28
 */
interface AdyFish : AdyMob {

    fun setFromBucket(value: Boolean) {
        setMetadata("fromBucket", value)
    }

    fun isFromBucket(): Boolean {
        return getMetadata("fromBucket")
    }
}