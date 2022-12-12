package ink.ptms.adyeshach.impl.nms

import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import ink.ptms.adyeshach.common.api.Adyeshach
import ink.ptms.adyeshach.common.api.MinecraftEntityPlayerHandler
import ink.ptms.adyeshach.common.api.MinecraftPacketHandler
import org.bukkit.entity.Player
import taboolib.common.util.unsafeLazy
import taboolib.library.reflex.Reflex.Companion.invokeMethod
import taboolib.library.reflex.Reflex.Companion.setProperty
import taboolib.library.reflex.Reflex.Companion.unsafeInstance
import taboolib.module.nms.MinecraftVersion
import taboolib.module.nms.nmsClass
import taboolib.module.nms.obcClass
import java.util.*

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.nms.DefaultMinecraftEntityPlayerHandler
 *
 * @author 坏黑
 * @since 2022/6/28 00:10
 */
class DefaultMinecraftEntityPlayerHandler : MinecraftEntityPlayerHandler {

    val isUniversal = MinecraftVersion.isUniversal

    val majorLegacy = MinecraftVersion.majorLegacy

    val classPlayerInfoData by unsafeLazy { nmsClass("PacketPlayOutPlayerInfo\$PlayerInfoData") }

    val packetHandler: MinecraftPacketHandler
        get() = Adyeshach.api().getMinecraftAPI().getPacketHandler()

    @Suppress("DuplicatedCode")
    override fun addPlayerInfo(player: Player, uuid: UUID, name: String, ping: Int, texture: Array<String>) {
        val profile = GameProfile(uuid, name)
        if (texture.size == 2) {
            profile.properties.put("textures", Property("textures", texture[0], texture[1]))
        }
        val infoData = classPlayerInfoData.unsafeInstance()
        if (isUniversal) {
            val info = NMSPacketPlayOutPlayerInfo::class.java.unsafeInstance()
            infoData.setProperty("a", ping)
            infoData.setProperty("b", NMSEnumGameMode.values()[0])
            infoData.setProperty("c", profile)
            infoData.setProperty("d", craftChatMessageFromString(name))
            packetHandler.sendPacket(
                player,
                info,
                "a" to NMSPacketPlayOutPlayerInfoEnumPlayerInfoAction.ADD_PLAYER,
                "b" to listOf(infoData)
            )
        } else if (majorLegacy >= 11000) {
            val info = NMS11PacketPlayOutPlayerInfo()
            infoData.setProperty("b", ping)
            infoData.setProperty("c", NMS11EnumGameMode.values()[0])
            infoData.setProperty("d", profile)
            infoData.setProperty("e", craftChatMessageFromString(name))
            packetHandler.sendPacket(
                player,
                info,
                "a" to NMS11PacketPlayOutPlayerInfoEnumPlayerInfoAction.ADD_PLAYER,
                "b" to listOf(infoData)
            )
        } else {
            val info = NMS9PacketPlayOutPlayerInfo()
            infoData.setProperty("b", ping)
            infoData.setProperty("c", NMS9EnumGameMode.values()[0])
            infoData.setProperty("d", profile)
            infoData.setProperty("e", craftChatMessageFromString(name))
            packetHandler.sendPacket(
                player,
                info,
                "a" to NMS9PacketPlayOutPlayerInfoEnumPlayerInfoAction.ADD_PLAYER,
                "b" to listOf(infoData)
            )
        }
    }

    override fun removePlayerInfo(player: Player, uuid: UUID) {
        val infoData = classPlayerInfoData.unsafeInstance()
        if (isUniversal) {
            val info = NMSPacketPlayOutPlayerInfo::class.java.unsafeInstance()
            infoData.setProperty("a", -1)
            infoData.setProperty("c", GameProfile(uuid, ""))
            packetHandler.sendPacket(
                player,
                info,
                "a" to NMSPacketPlayOutPlayerInfoEnumPlayerInfoAction.REMOVE_PLAYER,
                "b" to listOf(infoData)
            )
        } else {
            val info = NMS16PacketPlayOutPlayerInfo()
            infoData.setProperty("b", -1)
            infoData.setProperty("d", GameProfile(uuid, ""))
            packetHandler.sendPacket(
                player,
                info,
                "a" to NMS16PacketPlayOutPlayerInfoEnumPlayerInfoAction.REMOVE_PLAYER,
                "b" to listOf(infoData)
            )
        }
    }

    fun craftChatMessageFromString(message: String): Any? {
        return obcClass("util.CraftChatMessage").invokeMethod<Array<*>>("fromString", message, isStatic = true)!![0]
    }
}