package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityTypes
import ink.ptms.adyeshach.core.entity.type.AdyHuman
import org.bukkit.entity.Player

/**
 * @author sky
 * @since 2020-08-04 15:44
 */
@Deprecated("Outdated but usable")
class AdyHuman(v2: ink.ptms.adyeshach.core.entity.EntityInstance):  AdyEntityLiving(EntityTypes.PLAYER, v2) {

    private val playerUUID by lazy { normalizeUniqueId }

    var isHideFromTabList: Boolean
        get() {
            v2 as AdyHuman
            return v2.isHideFromTabList
        }
        set(value) {
            v2 as AdyHuman
            v2.isHideFromTabList = value
        }


    override fun visible(viewer: Player, visible: Boolean): Boolean {
        return v2.visible(viewer, visible)
    }

    fun setName(name: String) {
        v2 as AdyHuman
        v2.setName(name)
    }

    fun getName(): String {
        v2 as AdyHuman
        return v2.getName()
    }

    fun setPing(ping: Int) {
        v2 as AdyHuman
        v2.setPing(ping)
    }

    fun getPing(): Int {
        v2 as AdyHuman
        return v2.getPing()
    }

    fun setTexture(name: String) {
        v2 as AdyHuman
        v2.setTexture(name)
    }

    fun setTexture(texture: String, signature: String) {
        v2 as AdyHuman
        v2.setTexture(texture, signature)
    }

    fun getTexture(): Array<String> {
        v2 as AdyHuman
        return v2.getTexture()
    }

    fun getTextureName(): String {
        v2 as AdyHuman
        return v2.getTextureName()
    }

    fun resetTexture() {
        v2 as AdyHuman
        v2.resetTexture()
    }

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

    fun setSleeping(value: Boolean) {
        v2 as AdyHuman
        v2.setSleeping(value)
    }

    fun isSleeping(): Boolean {
        v2 as AdyHuman
        return v2.isSleeping()
    }

    fun refreshPlayerInfo(viewer: Player) {
        v2 as AdyHuman
        v2.refreshPlayerInfo(viewer)
    }
}