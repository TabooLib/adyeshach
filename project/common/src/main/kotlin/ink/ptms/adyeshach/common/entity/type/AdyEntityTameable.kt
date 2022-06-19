package ink.ptms.adyeshach.common.entity.type

/**
 * @author sky
 * @since 2020-08-04 18:28
 */
interface AdyEntityTameable : AdyEntityAgeable {

    /**
     * 切换坐着状态
     */
    fun setSitting(value: Boolean) {
        setMetadata("isSitting", value)
    }

    /**
     * 是否坐着
     */
    fun isSitting(): Boolean {
        return getMetadata("isSitting")
    }

    /**
     * 切换愤怒状态
     */
    fun setAngry(value: Boolean) {
        setMetadata("isAngry", value)
    }

    /**
     * 是否愤怒
     */
    fun isAngry(): Boolean {
        return getMetadata("isAngry")
    }

    /**
     * 设置驯服状态
     */
    fun setTamed(value: Boolean) {
        setMetadata("isTamed", value)
    }

    /**
     * 是否驯服
     */
    fun isTamed(): Boolean {
        return getMetadata("isTamed")
    }
}