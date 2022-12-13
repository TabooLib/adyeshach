package ink.ptms.adyeshach.impl.nms

import ink.ptms.adyeshach.api.dataserializer.createDataSerializer
import ink.ptms.adyeshach.common.api.Adyeshach
import ink.ptms.adyeshach.common.api.MinecraftEntityPlayerHandler
import ink.ptms.adyeshach.common.api.MinecraftPacketHandler
import ink.ptms.adyeshach.common.bukkit.data.GameProfile
import ink.ptms.adyeshach.common.bukkit.data.GameProfileAction
import ink.ptms.adyeshach.impl.nmsj17.NMSJ17
import net.minecraft.network.chat.IChatBaseComponent
import net.minecraft.world.level.EnumGamemode
import org.bukkit.entity.Player
import taboolib.module.nms.MinecraftVersion
import taboolib.module.nms.nmsClass
import java.lang.reflect.Constructor
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

    /** 1.9 ~ 1.19.2 */
    val classPlayerInfoData: Class<*>? = kotlin.runCatching { nmsClass("PacketPlayOutPlayerInfo\$PlayerInfoData") }.getOrNull()

    /** 1.9 ~ 1.19.2 */
    val classPlayerInfoDataConstructor: Constructor<*>? = classPlayerInfoData?.getDeclaredConstructor(
        com.mojang.authlib.GameProfile::class.java,
        Int::class.javaPrimitiveType,
        NMS16EnumGameMode::class.java,
        NMS16IChatBaseComponent::class.java
    )?.also { it.isAccessible = true }

    val packetHandler: MinecraftPacketHandler
        get() = Adyeshach.api().getMinecraftAPI().getPacketHandler()

    @Suppress("DuplicatedCode")
    override fun addPlayerInfo(player: Player, uuid: UUID, gameProfile: GameProfile) {
        if (majorLegacy >= 11903) {
            updatePlayerInfo(player, uuid, gameProfile, GameProfileAction.addAction11903())
        } else {
            updatePlayerInfo(player, uuid, gameProfile, GameProfileAction.addAction11900())
        }
    }

    override fun updatePlayerInfo(player: Player, uuid: UUID, gameProfile: GameProfile, actions: List<GameProfileAction>) {
        // 1.19.3
        // PacketPlayOutPlayerInfo 变更为 ClientboundPlayerInfoPacket
        if (majorLegacy >= 11903) {
            packetHandler.sendPacket(player, NMSJ17.instance.createClientboundPlayerInfoUpdatePacket(uuid, gameProfile, actions))
        }
        // 1.17, 1.18, 1.19
        else if (isUniversal) {
            packetHandler.sendPacket(player, NMSPacketPlayOutPlayerInfo(createDataSerializer {

            }.toNMS() as NMSPacketDataSerializer))
        }
        // 1.9 ~ 1.16
        else {
            packetHandler.sendPacket(player, NMS16PacketPlayOutPlayerInfo().also {
                it.a(createDataSerializer {

                }.toNMS() as NMS16PacketDataSerializer)
            })
        }

//        val infoData = classPlayerInfoData.unsafeInstance()
//        if (isUniversal) {
//            val info = NMSPacketPlayOutPlayerInfo::class.java.unsafeInstance()
//            infoData.setProperty("a", ping)
//            infoData.setProperty("b", NMSEnumGameMode.values()[0])
//            infoData.setProperty("c", profile)
//            infoData.setProperty("d", craftChatMessageFromString(name))
//            packetHandler.sendPacket(
//                player,
//                info,
//                "a" to NMSPacketPlayOutPlayerInfoEnumPlayerInfoAction.ADD_PLAYER,
//                "b" to listOf(infoData)
//            )
//        } else if (majorLegacy >= 11000) {
//            val info = NMS11PacketPlayOutPlayerInfo()
//            infoData.setProperty("b", ping)
//            infoData.setProperty("c", NMS11EnumGameMode.values()[0])
//            infoData.setProperty("d", profile)
//            infoData.setProperty("e", craftChatMessageFromString(name))
//            packetHandler.sendPacket(
//                player,
//                info,
//                "a" to NMS11PacketPlayOutPlayerInfoEnumPlayerInfoAction.ADD_PLAYER,
//                "b" to listOf(infoData)
//            )
//        } else {
//            val info = NMS9PacketPlayOutPlayerInfo()
//            infoData.setProperty("b", ping)
//            infoData.setProperty("c", NMS9EnumGameMode.values()[0])
//            infoData.setProperty("d", profile)
//            infoData.setProperty("e", craftChatMessageFromString(name))
//            packetHandler.sendPacket(
//                player,
//                info,
//                "a" to NMS9PacketPlayOutPlayerInfoEnumPlayerInfoAction.ADD_PLAYER,
//                "b" to listOf(infoData)
//            )
//        }
    }

    override fun removePlayerInfo(player: Player, uuid: UUID) {
        // 1.19.3
        // PacketPlayOutPlayerInfo 变更为 ClientboundPlayerInfoPacket
        if (majorLegacy >= 11903) {
            packetHandler.sendPacket(player, NMSJ17.instance.createClientboundPlayerInfoRemovePacket(uuid))
        }

//        val infoData = classPlayerInfoData.unsafeInstance()
//        if (isUniversal) {
//            val info = NMSPacketPlayOutPlayerInfo::class.java.unsafeInstance()
//            infoData.setProperty("a", -1)
//            infoData.setProperty("c", GameProfile(uuid, ""))
//            packetHandler.sendPacket(
//                player,
//                info,
//                "a" to NMSPacketPlayOutPlayerInfoEnumPlayerInfoAction.REMOVE_PLAYER,
//                "b" to listOf(infoData)
//            )
//        } else {
//            val info = NMS16PacketPlayOutPlayerInfo()
//            infoData.setProperty("b", -1)
//            infoData.setProperty("d", GameProfile(uuid, ""))
//            packetHandler.sendPacket(
//                player,
//                info,
//                "a" to NMS16PacketPlayOutPlayerInfoEnumPlayerInfoAction.REMOVE_PLAYER,
//                "b" to listOf(infoData)
//            )
//        }
    }

    fun craftChatMessageFromString(message: String): Any? {
        return CraftChatMessage19.fromString(message)[0]
    }

    fun createPlayerInfoUpdatePacketAction(action: GameProfileAction): NMSPacketPlayOutPlayerInfoEnumPlayerInfoAction {
        return when (action) {
            GameProfileAction.ADD_PLAYER -> NMSPacketPlayOutPlayerInfoEnumPlayerInfoAction.ADD_PLAYER
            GameProfileAction.UPDATE_GAME_MODE -> NMSPacketPlayOutPlayerInfoEnumPlayerInfoAction.UPDATE_GAME_MODE
            GameProfileAction.UPDATE_LATENCY -> NMSPacketPlayOutPlayerInfoEnumPlayerInfoAction.UPDATE_LATENCY
            GameProfileAction.UPDATE_DISPLAY_NAME -> NMSPacketPlayOutPlayerInfoEnumPlayerInfoAction.UPDATE_DISPLAY_NAME
            else -> NMSPacketPlayOutPlayerInfoEnumPlayerInfoAction.REMOVE_PLAYER
        }
    }

    fun createPlayerInfoUpdatePacketProfile(uuid: UUID, gameProfile: GameProfile): Any {
        val latency = gameProfile.ping
        // 1.19, 1.19.1, 1.19.2 版本包含 ProfilePublicKey 参数
        return if (majorLegacy >= 11900) {
            val displayName = Adyeshach.api().getMinecraftAPI().getHelper().craftChatMessageFromString(gameProfile.name) as IChatBaseComponent
            val gameMode = if (gameProfile.spectator) EnumGamemode.SPECTATOR else EnumGamemode.CREATIVE
            NMSPacketPlayOutPlayerInfoPlayerInfoData(gameProfile.toMojang(uuid), latency, gameMode, displayName, null)
        } else {
            val displayName = Adyeshach.api().getMinecraftAPI().getHelper().craftChatMessageFromString(gameProfile.name) as NMS16IChatBaseComponent
            val gameMode = if (gameProfile.spectator) NMS16EnumGameMode.SPECTATOR else NMS16EnumGameMode.CREATIVE
            classPlayerInfoDataConstructor!!.newInstance(gameProfile.toMojang(uuid), latency, gameMode, displayName)
        }
    }
}