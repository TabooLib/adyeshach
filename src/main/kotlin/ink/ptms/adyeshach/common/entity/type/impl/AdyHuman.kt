package ink.ptms.adyeshach.common.entity.type.impl

import ink.ptms.adyeshach.api.nms.NMS
import ink.ptms.adyeshach.common.entity.element.GameProfile
import ink.ptms.adyeshach.common.entity.type.EntityTypes
import io.izzel.taboolib.internal.gson.annotations.Expose
import org.bukkit.GameMode
import org.bukkit.Location
import java.util.*

/**
 * @Author sky
 * @Since 2020-08-04 15:44
 */
class AdyHuman() : AdyEntityLiving(EntityTypes.PLAYER) {

    private val uuid = UUID.randomUUID()!!
    @Expose
    private val gameProfile = GameProfile()

    init {
        /**
         * 1.15 -> 16
         * 1.14 -> 15
         * 1.10 -> 13
         * 1.9 -> 12
         */
        registerMetaByteMask(at(11500 to 16, 11400 to 15, 11000 to 13, 10900 to 12), "skinCapeEnabled", 0x01, true)
        registerMetaByteMask(at(11500 to 16, 11400 to 15, 11000 to 13, 10900 to 12), "skinJacketEnabled", 0x02, true)
        registerMetaByteMask(at(11500 to 16, 11400 to 15, 11000 to 13, 10900 to 12), "skinLeftSleeveEnabled", 0x04, true)
        registerMetaByteMask(at(11500 to 16, 11400 to 15, 11000 to 13, 10900 to 12), "skinRightSleeveEnabled", 0x08, true)
        registerMetaByteMask(at(11500 to 16, 11400 to 15, 11000 to 13, 10900 to 12), "skinLeftPantsEnabled", 0x10, true)
        registerMetaByteMask(at(11500 to 16, 11400 to 15, 11000 to 13, 10900 to 12), "skinRightPantsEnabled", 0x20, true)
        registerMetaByteMask(at(11500 to 16, 11400 to 15, 11000 to 13, 10900 to 12), "skinHatEnabled", 0x40, true)
    }

    override fun spawn(location: Location) {
        super.spawn(location)
        addPlayerInfo()
        NMS.INSTANCE.spawnNamedEntity(owner!!, EntityTypes.PLAYER.getEntityTypeNMS(), index, uuid, location)
    }

    override fun destroy() {
        super.destroy()
        removePlayerInfo()
    }

    fun setName(name: String) {
        gameProfile.name = name
        respawn()
    }

    fun getName(): String {
        return gameProfile.name
    }

    fun setPing(ping: Int) {
        gameProfile.ping = ping
        respawn()
    }

    fun getPing(): Int {
        return gameProfile.ping
    }

    fun setGameMode(gameMode: GameMode) {
        gameProfile.gameMode = gameMode
        respawn()
    }

    fun getGameMode(): GameMode {
        return gameProfile.gameMode
    }

    fun setTexture(texture: String, signature: String) {
        gameProfile.texture = arrayOf(texture, signature)
        respawn()
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
        NMS.INSTANCE.addPlayerInfo(owner!!, uuid, gameProfile.name, gameProfile.ping, gameProfile.gameMode, gameProfile.texture)
    }

    fun removePlayerInfo() {
        NMS.INSTANCE.removePlayerInfo(owner!!, uuid)
    }
}