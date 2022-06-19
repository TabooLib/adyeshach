package ink.ptms.adyeshach.common.entity.type

import org.bukkit.entity.Player

/**
 * @author sky
 * @since 2020-08-04 15:44
 */
interface AdyHuman : AdyEntityLiving {

    var isHideFromTabList: Boolean

    fun refreshPlayerInfo(viewer: Player)

    fun setName(name: String)

    fun getName(): String

    fun setPing(ping: Int)

    fun getPing(): Int

    fun setTexture(name: String)

    fun setTexture(texture: String, signature: String)

    fun getTexture(): Array<String>

    fun getTextureName(): String

    fun resetTexture()

    fun setSleeping(value: Boolean)

    fun isSleeping(): Boolean

    fun setSkinCapeEnabled(value: Boolean) {
        setMetadata("skinCape", value)
    }

    fun isSkinCapeEnabled() {
        return getMetadata("skinCape")
    }

    fun setSkinJacketEnabled(value: Boolean) {
        setMetadata("skinJacket", value)
    }

    fun isSkinJacketEnabled() {
        return getMetadata("skinJacket")
    }

    fun setSkinLeftSleeveEnabled(value: Boolean) {
        setMetadata("skinLeftSleeve", value)
    }

    fun isSkinLeftSleeveEnabled() {
        return getMetadata("skinLeftSleeve")
    }

    fun setSkinRightSleeveEnabled(value: Boolean) {
        setMetadata("skinRightSleeve", value)
    }

    fun isSkinRightSleeveEnabled() {
        return getMetadata("skinRightSleeve")
    }

    fun setSkinLeftPantsEnabled(value: Boolean) {
        setMetadata("skinLeftPants", value)
    }

    fun isSkinLeftPantsEnabled() {
        return getMetadata("skinLeftPants")
    }

    fun setSkinRightPantsEnabled(value: Boolean) {
        setMetadata("skinRightPants", value)
    }

    fun isSkinRightPantsEnabled() {
        return getMetadata("skinRightPants")
    }

    fun setSkinHatEnabled(value: Boolean) {
        setMetadata("skinHat", value)
    }

    fun isSkinHatEnabled() {
        return getMetadata("skinHat")
    }
}