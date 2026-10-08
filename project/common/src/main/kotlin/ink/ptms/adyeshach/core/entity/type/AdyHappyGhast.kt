package ink.ptms.adyeshach.core.entity.type

/**
 * 快乐恶魂（1.21.6+）
 */
interface AdyHappyGhast : AdyEntityAgeable {

    fun setLeashHolder(value: Boolean) {
        setMetadata("isLeashHolder", value)
    }

    fun isLeashHolder(): Boolean {
        return getMetadata("isLeashHolder")
    }

    fun setStaysStill(value: Boolean) {
        setMetadata("staysStill", value)
    }

    fun isStaysStill(): Boolean {
        return getMetadata("staysStill")
    }
}
