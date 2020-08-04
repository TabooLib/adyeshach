package ink.ptms.adyeshach.common.entity.type.impl

import ink.ptms.adyeshach.common.entity.MetadataExtend
import ink.ptms.adyeshach.common.entity.element.EntityProperties
import ink.ptms.adyeshach.common.entity.type.EntityTypes
import ink.ptms.adyeshach.api.nms.NMS
import io.izzel.taboolib.internal.gson.annotations.Expose
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.*

/**
 * @Author sky
 * @Since 2020-08-04 15:44
 */
class AdyHuman(owner: Player) : AdyHumanLike(owner, EntityTypes.PLAYER), MetadataExtend {

    init {
        properties = HumanProperties()
    }

    val uuid = UUID.randomUUID()!!

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
        getProperties().name = name
        updatePlayerInfo()
    }

    fun getName(): String {
        return getProperties().name
    }

    fun setPing(ping: Int) {
        getProperties().ping = ping
        updatePlayerInfo()
    }

    fun getPing(): Int {
        return getProperties().ping
    }

    fun setGameMode(gameMode: GameMode) {
        getProperties().gameMode = gameMode
        updatePlayerInfo()
    }

    fun getGameMode(): GameMode {
        return getProperties().gameMode
    }

    fun setTexture(texture: String, signature: String) {
        getProperties().texture = arrayOf(texture, signature)
        updatePlayerInfo()
    }

    fun setSkinData(cape: Boolean, jacket: Boolean, leftSleeve: Boolean, rightSleeve: Boolean, leftPants: Boolean, rightPants: Boolean, hat: Boolean, unused: Boolean) {
        getProperties().skinCapeEnabled = cape
        getProperties().skinJacketEnabled = jacket
        getProperties().skinLeftSleeveEnabled = leftSleeve
        getProperties().skinRightSleeveEnabled = rightSleeve
        getProperties().skinLeftPantsEnabled = leftPants
        getProperties().skinRightPantsEnabled = rightPants
        getProperties().skinHatEnabled = hat
        getProperties().skinUnusedEnabled = unused
        NMS.INSTANCE.updateEntityMetadata(owner, index, NMS.INSTANCE.getMetaEntityByte(16, skinData()))
    }

    fun getTexture(): Array<String> {
        return getProperties().texture
    }

    fun updatePlayerInfo() {
        spawn(getLatestLocation())
        NMS.INSTANCE.updateEntityMetadata(owner, index, NMS.INSTANCE.getMetaEntityByte(16, skinData()))
    }

    fun addPlayerInfo() {
        NMS.INSTANCE.addPlayerInfo(owner, uuid, getProperties().name, getProperties().ping, getProperties().gameMode, getProperties().texture)
    }

    fun removePlayerInfo() {
        NMS.INSTANCE.removePlayerInfo(owner, uuid)
    }

    fun hideInTabList() {
        removePlayerInfo()
    }

    fun skinData(): Byte {
        return getProperties().run {
            var bits = 0
            if (skinCapeEnabled) bits += 0x01
            if (skinJacketEnabled) bits += 0x02
            if (skinLeftSleeveEnabled) bits += 0x04
            if (skinRightSleeveEnabled) bits += 0x08
            if (skinLeftPantsEnabled) bits += 0x10
            if (skinRightPantsEnabled) bits += 0x20
            if (skinHatEnabled) bits += 0x40
            if (skinUnusedEnabled) bits += 0x80
            bits.toByte()
        }
    }

    override fun metadata(): List<Any> {
        return getProperties().run {
            listOf(
                    NMS.INSTANCE.getMetaEntityByte(16, skinData())
            )
        }
    }

    private fun getProperties(): HumanProperties {
        return properties as HumanProperties
    }

    private class HumanProperties : EntityProperties() {

        @Expose
        var name = "AdyHuman"

        /**
         * A ping that negative (i.e. not known to the server yet) will result in the no connection icon.
         * A ping under 150 milliseconds will result in 5 bars
         * A ping under 300 milliseconds will result in 4 bars
         * A ping under 600 milliseconds will result in 3 bars
         * A ping under 1000 milliseconds (1 second) will result in 2 bars
         * A ping greater than or equal to 1 second will result in 1 bar.
         */
        @Expose
        var ping = 60

        @Expose
        var gameMode = GameMode.SURVIVAL

        @Expose
        var texture = arrayOf("")

        @Expose
        var skinCapeEnabled = false

        @Expose
        var skinJacketEnabled = false

        @Expose
        var skinLeftSleeveEnabled = false

        @Expose
        var skinRightSleeveEnabled = false

        @Expose
        var skinLeftPantsEnabled = false

        @Expose
        var skinRightPantsEnabled = false

        @Expose
        var skinHatEnabled = false

        @Expose
        var skinUnusedEnabled = false
    }
}