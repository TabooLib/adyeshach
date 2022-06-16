package ink.ptms.adyeshach.common.entity.type

import org.bukkit.entity.Player

/**
 * @author sky
 * @since 2020-08-04 15:44
 */
abstract class AdyHuman : AdyEntityLiving() {

    abstract var isHideFromTabList: Boolean

    abstract fun refreshPlayerInfo(viewer: Player)

    abstract fun setName(name: String)

    abstract fun getName(): String

    abstract fun setPing(ping: Int)

    abstract fun getPing(): Int

    abstract fun setTexture(name: String)

    abstract fun setTexture(texture: String, signature: String)

    abstract fun getTexture(): Array<String>

    abstract fun getTextureName(): String

    abstract fun resetTexture()

    abstract fun setSleeping(value: Boolean)

    abstract fun isSleeping(): Boolean

    open fun setSkinCapeEnabled(value: Boolean) {
        setMetadata("skinCape", value)
    }

    open fun isSkinCapeEnabled() {
        return getMetadata("skinCape")
    }

    open fun setSkinJacketEnabled(value: Boolean) {
        setMetadata("skinJacket", value)
    }

    open fun isSkinJacketEnabled() {
        return getMetadata("skinJacket")
    }

    open fun setSkinLeftSleeveEnabled(value: Boolean) {
        setMetadata("skinLeftSleeve", value)
    }

    open fun isSkinLeftSleeveEnabled() {
        return getMetadata("skinLeftSleeve")
    }

    open fun setSkinRightSleeveEnabled(value: Boolean) {
        setMetadata("skinRightSleeve", value)
    }

    open fun isSkinRightSleeveEnabled() {
        return getMetadata("skinRightSleeve")
    }

    open fun setSkinLeftPantsEnabled(value: Boolean) {
        setMetadata("skinLeftPants", value)
    }

    open fun isSkinLeftPantsEnabled() {
        return getMetadata("skinLeftPants")
    }

    open fun setSkinRightPantsEnabled(value: Boolean) {
        setMetadata("skinRightPants", value)
    }

    open fun isSkinRightPantsEnabled() {
        return getMetadata("skinRightPants")
    }

    open fun setSkinHatEnabled(value: Boolean) {
        setMetadata("skinHat", value)
    }

    open fun isSkinHatEnabled() {
        return getMetadata("skinHat")
    }
}