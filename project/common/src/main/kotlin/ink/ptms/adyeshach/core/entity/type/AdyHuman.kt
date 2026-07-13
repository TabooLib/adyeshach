package ink.ptms.adyeshach.core.entity.type

import ink.ptms.adyeshach.core.bukkit.data.PingBar
import org.bukkit.entity.Player

/**
 * @author sky
 * @since 2020-08-04 15:44
 */
interface AdyHuman : AdyEntityLiving {

    var isHideFromTabList: Boolean

    /**
     * 实体身上存在的箭矢，经过测试这个属性只对玩家类型有效
     */
    var arrowsInEntity: Int
        get() = getMetadata("arrowsInEntity")
        set(value) {
            setMetadata("arrowsInEntity", value)
        }

    /**
     * 实体身上存在的蜂刺，经过测试这个属性只对玩家类型有效
     */
    var beeStingersInEntity: Int
        get() = getMetadata("beeStingersInEntity")
        set(value) {
            setMetadata("beeStingersInEntity", value)
        }


    /**
     * 实体大小
     */
    var score: Int
        get() = getMetadata("score")
        set(value) {
            setMetadata("score", value)
        }

    fun refreshPlayerInfo(viewer: Player)

    fun setName(name: String)

    fun getName(): String

    fun setPing(ping: Int)

    fun setPingBar(pingBar: PingBar)

    fun getPing(): Int

    fun setSpectator(value: Boolean)

    fun isSpectator(): Boolean

    fun setListed(value: Boolean)

    fun isListed(): Boolean

    fun setTexture(name: String)

    fun setTexture(texture: String, signature: String)

    fun getTexture(): Array<String>

    fun getTextureName(): String

    fun resetTexture()

    fun setSleeping(value: Boolean)

    fun isSleeping(): Boolean

    fun setSize(size: Int)

    fun getSize(): Int

    fun initSkin() {
        setSkinCapeEnabled(isSkinCapeEnabled())
        setSkinHatEnabled(isSkinHatEnabled())
        setSkinJacketEnabled(isSkinJacketEnabled())
        setSkinLeftSleeveEnabled(isSkinLeftSleeveEnabled())
        setSkinRightSleeveEnabled(isSkinRightSleeveEnabled())
        setSkinLeftPantsEnabled(isSkinLeftPantsEnabled())
        setSkinRightPantsEnabled(isSkinRightPantsEnabled())
    }

    fun setSkinEnabled(value: Boolean) {
        setMetadata("skinCape", value)
        setMetadata("skinHat", value)
        setMetadata("skinJacket", value)
        setMetadata("skinLeftSleeve", value)
        setMetadata("skinRightSleeve", value)
        setMetadata("skinLeftPants", value)
        setMetadata("skinRightPants", value)
    }

    fun setSkinCapeEnabled(value: Boolean) {
        setMetadata("skinCape", value)
    }

    fun isSkinCapeEnabled(): Boolean {
        return getMetadata("skinCape")
    }

    fun setSkinJacketEnabled(value: Boolean) {
        setMetadata("skinJacket", value)
    }

    fun isSkinJacketEnabled(): Boolean {
        return getMetadata("skinJacket")
    }

    fun setSkinLeftSleeveEnabled(value: Boolean) {
        setMetadata("skinLeftSleeve", value)
    }

    fun isSkinLeftSleeveEnabled(): Boolean {
        return getMetadata("skinLeftSleeve")
    }

    fun setSkinRightSleeveEnabled(value: Boolean) {
        setMetadata("skinRightSleeve", value)
    }

    fun isSkinRightSleeveEnabled(): Boolean {
        return getMetadata("skinRightSleeve")
    }

    fun setSkinLeftPantsEnabled(value: Boolean) {
        setMetadata("skinLeftPants", value)
    }

    fun isSkinLeftPantsEnabled(): Boolean {
        return getMetadata("skinLeftPants")
    }

    fun setSkinRightPantsEnabled(value: Boolean) {
        setMetadata("skinRightPants", value)
    }

    fun isSkinRightPantsEnabled(): Boolean {
        return getMetadata("skinRightPants")
    }

    fun setSkinHatEnabled(value: Boolean) {
        setMetadata("skinHat", value)
    }

    fun isSkinHatEnabled(): Boolean {
        return getMetadata("skinHat")
    }
}