package ink.ptms.adyeshach.impl.nmsj17

import ink.ptms.adyeshach.api.dataserializer.createDataSerializer
import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.MinecraftMeta
import ink.ptms.adyeshach.core.bukkit.data.GameProfile
import ink.ptms.adyeshach.core.bukkit.data.GameProfileAction
import net.minecraft.core.IRegistry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.PacketDataSerializer
import net.minecraft.network.chat.IChatBaseComponent
import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata
import net.minecraft.network.syncher.DataWatcher
import net.minecraft.network.syncher.DataWatcherObject
import net.minecraft.network.syncher.DataWatcherRegistry
import net.minecraft.world.entity.EntityTypes
import net.minecraft.world.entity.animal.CatVariant
import net.minecraft.world.level.EnumGamemode
import org.bukkit.entity.Cat
import java.util.*

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.nmsj17.NMSJ17
 *
 * @author 坏黑
 * @since 2022/12/13 02:59
 */
@Suppress("UNCHECKED_CAST")
class NMSJ17Impl : NMSJ17() {

    override fun entityTypeGetId(any: Any): Int {
        /*
            TODO TabooLib NMSProxy 已知问题：
            因 RegistryBlocks 类型变更为 interface，且反混淆文件中不包含 RegistryBlocks 的 getId 方法：

            net/minecraft/core/RegistryBlocks a ()Lnet/minecraft/resources/MinecraftKey; getDefaultKey
            net/minecraft/core/RegistryBlocks a (I)Ljava/lang/Object; byId
            net/minecraft/core/RegistryBlocks a (Lnet/minecraft/resources/MinecraftKey;)Ljava/lang/Object; get
            net/minecraft/core/RegistryBlocks b (Ljava/lang/Object;)Lnet/minecraft/resources/MinecraftKey; getKey

            需转换为 IRegistry 类型：
            net/minecraft/core/IRegistry a (Ljava/lang/Object;)I getId

            结论：
            调用对象中「仅在父类」声明的方法或字段无法被 TabooLib NMSProxy 重定向
         */
        val ir = BuiltInRegistries.ENTITY_TYPE as IRegistry<EntityTypes<*>>
        return ir.getId(any as EntityTypes<*>)
    }

    override fun createCatVariantMeta(index: Int, type: Cat.Type): MinecraftMeta {
        val item = DataWatcher.Item(
            DataWatcherObject(index, DataWatcherRegistry.CAT_VARIANT),
            CatVariant(when (type) {
                Cat.Type.TABBY -> CatVariant.TABBY
                Cat.Type.BLACK -> CatVariant.BLACK
                Cat.Type.RED -> CatVariant.RED
                Cat.Type.SIAMESE -> CatVariant.SIAMESE
                Cat.Type.BRITISH_SHORTHAIR -> CatVariant.BRITISH_SHORTHAIR
                Cat.Type.CALICO -> CatVariant.CALICO
                Cat.Type.PERSIAN -> CatVariant.PERSIAN
                Cat.Type.RAGDOLL -> CatVariant.RAGDOLL
                Cat.Type.WHITE -> CatVariant.WHITE
                Cat.Type.JELLIE -> CatVariant.JELLIE
                Cat.Type.ALL_BLACK -> CatVariant.ALL_BLACK
            }.location())
        )
        return object : MinecraftMeta {

            override fun source() = item
        }
    }

    override fun createPacketPlayOutEntityMetadata(entityId: Int, packedItems: List<MinecraftMeta>): Any {
        return PacketPlayOutEntityMetadata(entityId, packedItems.map { (it.source() as DataWatcher.Item<*>).value() })
    }

    override fun createClientboundPlayerInfoAddPacket(uuid: UUID, gameProfile: GameProfile): Any {
        return createClientboundPlayerInfoUpdatePacket(uuid, gameProfile, GameProfileAction.addAction11903())
    }

    override fun createClientboundPlayerInfoUpdatePacket(uuid: UUID, gameProfile: GameProfile, actions: List<GameProfileAction>): Any {
        // 转换为数据包对象
        val a = EnumSet.copyOf(actions.map { createClientboundPlayerInfoUpdatePacketAction(it) as ClientboundPlayerInfoUpdatePacket.a })
        // 创建数据包
        return ClientboundPlayerInfoUpdatePacket(createDataSerializer {
            writeEnumSet(a, ClientboundPlayerInfoUpdatePacket.a::class.java)
            writeVarInt(1)
            writeUUID(uuid)
            a.forEach { action ->
                when (action!!) {
                    ClientboundPlayerInfoUpdatePacket.a.ADD_PLAYER -> {
                        writeUtf(gameProfile.name, 16)
                        writeGameProfileProperties(gameProfile.toMojang(uuid).properties)
                    }
                    ClientboundPlayerInfoUpdatePacket.a.INITIALIZE_CHAT -> {}
                    ClientboundPlayerInfoUpdatePacket.a.UPDATE_GAME_MODE -> {
                        writeVarInt(if (gameProfile.spectator) 3 else 1)
                    }
                    ClientboundPlayerInfoUpdatePacket.a.UPDATE_LISTED -> {
                        writeBoolean(gameProfile.listed)
                    }
                    ClientboundPlayerInfoUpdatePacket.a.UPDATE_LATENCY -> {
                        writeVarInt(gameProfile.ping)
                    }
                    ClientboundPlayerInfoUpdatePacket.a.UPDATE_DISPLAY_NAME -> {
                        writeNullable(gameProfile.name) {
                            val component = Adyeshach.api().getMinecraftAPI().getHelper().craftChatMessageFromString(it)
                            val json = Adyeshach.api().getMinecraftAPI().getHelper().craftChatSerializerToJson(component)
                            writeUtf(json, 262144)
                        }
                    }
                }
            }
        }.toNMS() as PacketDataSerializer)
    }

    override fun createClientboundPlayerInfoRemovePacket(uuid: UUID): Any {
        return ClientboundPlayerInfoRemovePacket(listOf(uuid))
    }

    override fun createClientboundPlayerInfoUpdatePacketAction(action: GameProfileAction): Any {
        return when (action) {
            GameProfileAction.ADD_PLAYER -> ClientboundPlayerInfoUpdatePacket.a.ADD_PLAYER
            GameProfileAction.UPDATE_GAME_MODE -> ClientboundPlayerInfoUpdatePacket.a.UPDATE_GAME_MODE
            GameProfileAction.UPDATE_LISTED -> ClientboundPlayerInfoUpdatePacket.a.UPDATE_LISTED
            GameProfileAction.UPDATE_LATENCY -> ClientboundPlayerInfoUpdatePacket.a.UPDATE_LATENCY
            GameProfileAction.UPDATE_DISPLAY_NAME -> ClientboundPlayerInfoUpdatePacket.a.UPDATE_DISPLAY_NAME
            else -> error("Unsupported action: $action")
        }
    }

    override fun createClientboundPlayerInfoUpdatePacketProfile(uuid: UUID, gameProfile: GameProfile): Any {
        val listed = gameProfile.listed
        val latency = gameProfile.ping
        val gameMode = if (gameProfile.spectator) EnumGamemode.SPECTATOR else EnumGamemode.CREATIVE
        val displayName = Adyeshach.api().getMinecraftAPI().getHelper().craftChatMessageFromString(gameProfile.name) as IChatBaseComponent
        return ClientboundPlayerInfoUpdatePacket.b(uuid, gameProfile.toMojang(uuid), listed, latency, gameMode, displayName, null)
    }
}