package ink.ptms.adyeshach.impl.nmsj17

import ink.ptms.adyeshach.api.dataserializer.createDataSerializer
import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.MinecraftMeta
import ink.ptms.adyeshach.core.bukkit.data.GameProfile
import ink.ptms.adyeshach.core.bukkit.data.GameProfileAction
import ink.ptms.adyeshach.core.entity.type.AdySniffer
import ink.ptms.adyeshach.impl.nms.CraftMagicNumbers19
import ink.ptms.adyeshach.impl.nms.NMSDataWatcherItem
import ink.ptms.adyeshach.impl.nms.NMSDataWatcherObject
import io.netty.handler.codec.EncoderException
import net.minecraft.SystemUtils
import net.minecraft.core.IRegistry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.DynamicOpsNBT
import net.minecraft.nbt.NBTBase
import net.minecraft.nbt.NBTCompressedStreamTools
import net.minecraft.network.PacketDataSerializer
import net.minecraft.network.chat.ComponentSerialization
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
import org.bukkit.material.MaterialData
import org.bukkit.util.Vector
import org.joml.Quaternionf
import org.joml.Vector3f
import taboolib.common.platform.function.info
import taboolib.common5.Quat
import taboolib.common5.cfloat
import taboolib.module.nms.MinecraftVersion
import java.util.*

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.nmsj17.NMSJ17
 *
 * @author 坏黑
 * @since 2022/12/13 02:59
 */
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

    override fun createVector3Meta(index: Int, value: Vector): Any {
        return NMSDataWatcherItem(
            NMSDataWatcherObject(index, DataWatcherRegistry.VECTOR3),
            Vector3f(value.x.cfloat, value.y.cfloat, value.z.cfloat)
        )
    }

    override fun createQuaternionMeta(index: Int, quat: Quat): Any {
        return NMSDataWatcherItem(
            NMSDataWatcherObject(index, DataWatcherRegistry.QUATERNION),
            Quaternionf(quat.x(), quat.y(), quat.z(), quat.w())
        )
    }

    override fun createBlockStateMeta(index: Int, materialData: MaterialData): Any {
        return NMSDataWatcherItem(
            NMSDataWatcherObject(index, DataWatcherRegistry.BLOCK_STATE),
            CraftMagicNumbers19.getBlock(materialData)
        )
    }

    override fun createOptBlockStateMeta(index: Int, materialData: MaterialData?): Any {
        return NMSDataWatcherItem(
            NMSDataWatcherObject(index, DataWatcherRegistry.OPTIONAL_BLOCK_STATE),
            Optional.ofNullable(if (materialData == null) null else CraftMagicNumbers19.getBlock(materialData))
        )
    }

    override fun createCatVariantMeta(index: Int, type: Cat.Type): Any {
        val ir = BuiltInRegistries.CAT_VARIANT as IRegistry<CatVariant>
        val texture = "textures/entity/cat/${type.name.lowercase()}.png"
        val variant = ir.first { it.texture.path == texture }
        return DataWatcher.Item(DataWatcherObject(index, DataWatcherRegistry.CAT_VARIANT), variant)
    }

    override fun createSnifferStateMeta(index: Int, type: AdySniffer.State): Any {
        return NMSDataWatcherItem(
            NMSDataWatcherObject(index, DataWatcherRegistry.SNIFFER_STATE),
            net.minecraft.world.entity.animal.sniffer.Sniffer.State.values()[type.ordinal]
        )
    }

    override fun createPacketPlayOutEntityMetadata(entityId: Int, packedItems: List<MinecraftMeta>): Any {
        return PacketPlayOutEntityMetadata(entityId, packedItems.map { (it.source() as DataWatcher.Item<*>).value() })
    }

    override fun createClientboundPlayerInfoAddPacket(uuid: UUID, gameProfile: GameProfile): Any {
        return createClientboundPlayerInfoUpdatePacket(uuid, gameProfile, GameProfileAction.initActions())
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
                    // 添加玩家
                    ClientboundPlayerInfoUpdatePacket.a.ADD_PLAYER -> {
                        writeUtf(gameProfile.name, 16)
                        writeGameProfileProperties(gameProfile.toMojang(uuid).properties)
                    }
                    // 游戏模式
                    ClientboundPlayerInfoUpdatePacket.a.UPDATE_GAME_MODE -> {
                        writeVarInt(if (gameProfile.spectator) 3 else 1)
                    }
                    // 是否显示在列表
                    ClientboundPlayerInfoUpdatePacket.a.UPDATE_LISTED -> {
                        writeBoolean(gameProfile.listed)
                    }
                    // 延迟
                    ClientboundPlayerInfoUpdatePacket.a.UPDATE_LATENCY -> {
                        writeVarInt(gameProfile.ping)
                    }
                    // 玩家名称
                    ClientboundPlayerInfoUpdatePacket.a.UPDATE_DISPLAY_NAME -> {
                        writeNullable(gameProfile.name) {
                            // 1.20.4+
                            // writeWithCodec(DynamicOpsNBT.INSTANCE, ComponentSerialization.CODEC, var0);
                            if (MinecraftVersion.majorLegacy >= 12004) {
                                val component = Adyeshach.api().getMinecraftAPI().getHelper().craftChatMessageFromString(it)
                                val nbt = SystemUtils.getOrThrow(ComponentSerialization.CODEC.encodeStart(DynamicOpsNBT.INSTANCE, component as IChatBaseComponent)) {
                                    EncoderException("Failed to encode: $it")
                                }
                                NBTCompressedStreamTools.writeAnyTag(nbt, dataOutput())
                            }
                            // 1.20.4-
                            // writeUtf(ChatSerializer.toJson(var0), 262144);
                            else {
                                val component = Adyeshach.api().getMinecraftAPI().getHelper().craftChatMessageFromString(it)
                                val json = Adyeshach.api().getMinecraftAPI().getHelper().craftChatSerializerToJson(component)
                                writeUtf(json, 262144)
                            }
                        }
                    }
                    else -> error("Unsupported action: $action")
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