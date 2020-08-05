package ink.ptms.adyeshach.common.entity.type.impl

import ink.ptms.adyeshach.common.entity.type.EntityTypes
import ink.ptms.adyeshach.api.nms.NMS
import ink.ptms.adyeshach.common.entity.element.GameProfile
import io.izzel.taboolib.internal.gson.annotations.Expose
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.*

/**
 * @Author sky
 * @Since 2020-08-04 15:44
 */
class AdyHuman(owner: Player) : AdyEntityLiving(owner, EntityTypes.PLAYER) {

    private val uuid = UUID.randomUUID()!!
    @Expose
    private val gameProfile = GameProfile()

    init {
        registerMetaByteMask(16, "skinCapeEnabled", 0x01, true)
        registerMetaByteMask(16, "skinJacketEnabled", 0x02, true)
        registerMetaByteMask(16, "skinLeftSleeveEnabled", 0x04, true)
        registerMetaByteMask(16, "skinRightSleeveEnabled", 0x08, true)
        registerMetaByteMask(16, "skinLeftPantsEnabled", 0x10, true)
        registerMetaByteMask(16, "skinRightPantsEnabled", 0x20, true)
        registerMetaByteMask(16, "skinHatEnabled", 0x40, true)
    }

    override fun spawn(location: Location) {
        super.spawn(location)
        addPlayerInfo()
        NMS.INSTANCE.spawnNamedEntity(owner, EntityTypes.PLAYER.getEntityTypeNMS(), index, uuid, location)
    }

    override fun destroy() {
        super.destroy()
        removePlayerInfo()
    }

    fun setName(name: String) {
        gameProfile.name = name
        refreshPlayerInfo()
    }

    fun getName(): String {
        return gameProfile.name
    }

    fun setPing(ping: Int) {
        gameProfile.ping = ping
        refreshPlayerInfo()
    }

    fun getPing(): Int {
        return gameProfile.ping
    }

    fun setGameMode(gameMode: GameMode) {
        gameProfile.gameMode = gameMode
        refreshPlayerInfo()
    }

    fun getGameMode(): GameMode {
        return gameProfile.gameMode
    }

    fun setTexture(texture: String, signature: String) {
        gameProfile.texture = arrayOf(texture, signature)
        refreshPlayerInfo()
    }

    fun getTexture(): Array<String> {
        return gameProfile.texture
    }

    fun setSkinCapeEnabled(value: Boolean) {
        setMetadata("skinCapeEnabled", value)
    }

    fun isSkinCapeEnabled() {
        return getMetadata("skinCapeEnabled")
    }

    fun setSkinJacketEnabled(value: Boolean) {
        setMetadata("skinJacketEnabled", value)
    }

    fun isSkinJacketEnabled() {
        return getMetadata("skinJacketEnabled")
    }

    fun setSkinLeftSleeveEnabled(value: Boolean) {
        setMetadata("skinLeftSleeveEnabled", value)
    }

    fun isSkinLeftSleeveEnabled() {
        return getMetadata("skinLeftSleeveEnabled")
    }

    fun setSkinRightSleeveEnabled(value: Boolean) {
        setMetadata("skinRightSleeveEnabled", value)
    }

    fun isSkinRightSleeveEnabled() {
        return getMetadata("skinRightSleeveEnabled")
    }

    fun setSkinLeftPantsEnabled(value: Boolean) {
        setMetadata("skinLeftPantsEnabled", value)
    }

    fun isSkinLeftPantsEnabled() {
        return getMetadata("skinLeftPantsEnabled")
    }

    fun setSkinRightPantsEnabled(value: Boolean) {
        setMetadata("skinRightPantsEnabled", value)
    }

    fun isSkinRightPantsEnabled() {
        return getMetadata("skinRightPantsEnabled")
    }

    fun setSkinHatEnabled(value: Boolean) {
        setMetadata("skinHatEnabled", value)
    }

    fun isSkinHatEnabled() {
        return getMetadata("skinHatEnabled")
    }

    fun addPlayerInfo() {
        NMS.INSTANCE.addPlayerInfo(owner, uuid, gameProfile.name, gameProfile.ping, gameProfile.gameMode, gameProfile.texture)
    }

    fun removePlayerInfo() {
        NMS.INSTANCE.removePlayerInfo(owner, uuid)
    }

    fun refreshPlayerInfo() {
        spawn(getLatestLocation())
        updateMetadata()
    }
}