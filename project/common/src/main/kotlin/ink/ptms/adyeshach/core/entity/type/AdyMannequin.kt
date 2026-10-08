package ink.ptms.adyeshach.core.entity.type

/**
 * 假人（1.21.9+）
 */
interface AdyMannequin : AdyEntityLiving {

    /**
     * 设置皮肤来源玩家名，客户端会按名称解析皮肤
     */
    fun setProfileName(name: String) {
        setMetadata("profileName", name)
    }

    fun getProfileName(): String {
        return getMetadata("profileName")
    }

    fun setImmovable(value: Boolean) {
        setMetadata("immovable", value)
    }

    fun isImmovable(): Boolean {
        return getMetadata("immovable")
    }

    fun setDescription(value: String) {
        setMetadata("description", value)
    }

    fun getDescription(): String {
        return getMetadata("description")
    }

    fun setSkinCapeEnabled(value: Boolean) {
        setMetadata("skinCape", value)
    }

    fun setSkinJacketEnabled(value: Boolean) {
        setMetadata("skinJacket", value)
    }

    fun setSkinLeftSleeveEnabled(value: Boolean) {
        setMetadata("skinLeftSleeve", value)
    }

    fun setSkinRightSleeveEnabled(value: Boolean) {
        setMetadata("skinRightSleeve", value)
    }

    fun setSkinLeftPantsEnabled(value: Boolean) {
        setMetadata("skinLeftPants", value)
    }

    fun setSkinRightPantsEnabled(value: Boolean) {
        setMetadata("skinRightPants", value)
    }

    fun setSkinHatEnabled(value: Boolean) {
        setMetadata("skinHat", value)
    }
}
