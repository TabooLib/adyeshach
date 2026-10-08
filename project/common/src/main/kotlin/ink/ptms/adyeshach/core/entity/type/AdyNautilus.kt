package ink.ptms.adyeshach.core.entity.type

/**
 * 鹦鹉螺（1.21.11+）
 */
interface AdyNautilus : AdyEntityTameable {

    fun setDashing(value: Boolean) {
        setMetadata("isDashing", value)
    }

    fun isDashing(): Boolean {
        return getMetadata("isDashing")
    }
}
