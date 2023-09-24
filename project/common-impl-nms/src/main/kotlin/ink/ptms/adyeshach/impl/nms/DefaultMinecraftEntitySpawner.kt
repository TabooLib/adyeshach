package ink.ptms.adyeshach.impl.nms

import com.github.benmanes.caffeine.cache.Caffeine
import ink.ptms.adyeshach.api.dataserializer.createDataSerializer
import ink.ptms.adyeshach.core.*
import ink.ptms.adyeshach.core.bukkit.BukkitDirection
import ink.ptms.adyeshach.core.bukkit.BukkitPaintings
import ink.ptms.adyeshach.core.entity.EntityTypes
import ink.ptms.adyeshach.impl.nmsj17.NMSJ17
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.material.MaterialData
import taboolib.common.platform.function.info
import taboolib.common.util.unsafeLazy
import taboolib.common5.cshort
import taboolib.library.reflex.Reflex.Companion.getProperty
import taboolib.library.reflex.Reflex.Companion.invokeMethod
import taboolib.library.reflex.UnsafeAccess
import taboolib.module.nms.MinecraftVersion
import java.lang.invoke.MethodHandle
import java.util.*

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.nms.DefaultMinecraftEntitySpawner
 *
 * @author 坏黑
 * @since 2022/6/28 00:08
 */
@Suppress("DuplicatedCode")
class DefaultMinecraftEntitySpawner : MinecraftEntitySpawner {

    val isUniversal = MinecraftVersion.isUniversal
    val major = MinecraftVersion.major
    val majorLegacy = MinecraftVersion.majorLegacy
    val minor = MinecraftVersion.minor

    val helper: MinecraftHelper
        get() = Adyeshach.api().getMinecraftAPI().getHelper()

    val typeHandler: AdyeshachEntityTypeRegistry
        get() = Adyeshach.api().getEntityTypeRegistry()

    val packetHandler: MinecraftPacketHandler
        get() = Adyeshach.api().getMinecraftAPI().getPacketHandler()

    val nms16Motive: NMS16RegistryBlocks<NMS16Paintings>
        get() = NMS16IRegistry::class.java.getProperty("MOTIVE", isStatic = true)!!

    val motiveCache = Caffeine.newBuilder()
        .expireAfterAccess(30, java.util.concurrent.TimeUnit.MINUTES)
        .build<BukkitPaintings, Int> {
            NMS16IRegistry::class.java.getProperty<Any>("MOTIVE", isStatic = true)!!.invokeMethod<Int>("a", helper.adapt(it))
        }

    val livingDataWatcherSetterM: MethodHandle by unsafeLazy {
        val field = NMS9PacketPlayOutSpawnEntityLiving::class.java.getDeclaredField("m")
        field.isAccessible = true
        UnsafeAccess.lookup.unreflectSetter(field)
    }

    val livingDataWatcherSetterH: MethodHandle by unsafeLazy {
        val field = NMS9PacketPlayOutSpawnEntityLiving::class.java.getDeclaredField("h")
        field.isAccessible = true
        UnsafeAccess.lookup.unreflectSetter(field)
    }

    val namedDataWatcherSetterH: MethodHandle by unsafeLazy {
        val field = NMS9PacketPlayOutSpawnEntityPlayer::class.java.getDeclaredField("h")
        field.isAccessible = true
        UnsafeAccess.lookup.unreflectSetter(field)
    }

    override fun spawnEntity(player: Player, entityType: EntityTypes, entityId: Int, uuid: UUID, location: Location, data: Int) {
        // 修复视角
        val yf = Adyeshach.api().getEntityFinder().getEntityFromClientEntityId(entityId, player)?.entityType.fixYaw(location.yaw)
        // 计算视角
        val yaw = (yf * 256.0f / 360.0f).toInt().toByte()
        val pitch = (location.pitch * 256.0f / 360.0f).toInt().toByte()
        // 版本判断
        val packet: Any = when (major) {
            // 1.9, 1.10, 1.11, 1.12, 1.13
            1, 2, 3, 4, 5 -> NMS9PacketPlayOutSpawnEntity().also {
                it.a(createDataSerializer {
                    writeVarInt(entityId)
                    writeUUID(uuid)
                    // 1.13 以下版本使用 Bukkit 的实体 ID
                    if (major != 5) {
                        writeByte(typeHandler.getBukkitEntityId(entityType).toByte())
                    } else {
                        // 1.13 使用 NMS 的实体 ID, 同时 1.13 版本的 IRegistry.ENTITY_TYPE 无法与 1.14, 1.15, 1.16 版本兼容
                        // 1.13 -> interface IRegistry<T>
                        writeByte(NMS13IRegistry.ENTITY_TYPE.a(helper.adapt(entityType) as NMS13EntityTypes<*>).toByte())
                    }
                    writeDouble(location.x)
                    writeDouble(location.y)
                    writeDouble(location.z)
                    writeByte(pitch)
                    writeByte(yaw)
                    writeInt(data)
                    writeShort(0)
                    writeShort(0)
                    writeShort(0)
                }.toNMS() as NMS9PacketDataSerializer)
            }
            // 1.14, 1.15, 1.16
            6, 7, 8 -> NMS16PacketPlayOutSpawnEntity().also {
                it.a(createDataSerializer {
                    writeVarInt(entityId)
                    writeUUID(uuid)
                    // 1.14, 1.15, 1.16 -> abstract class IRegistry<T> -> IRegistry 类型发生变化
                    writeVarInt(NMS16IRegistry.ENTITY_TYPE.a(helper.adapt(entityType) as NMS16EntityTypes<*>))
                    writeDouble(location.x)
                    writeDouble(location.y)
                    writeDouble(location.z)
                    writeByte(pitch)
                    writeByte(yaw)
                    writeInt(data)
                    writeShort(0)
                    writeShort(0)
                    writeShort(0)
                }.toNMS() as NMS16PacketDataSerializer)
            }
            // 1.17, 1.18, 1.19, 1.12
            9, 10, 11, 12 -> NMSPacketPlayOutSpawnEntity(createDataSerializer {
                writeVarInt(entityId)
                writeUUID(uuid)
                // 类型
                when (major) {
                    // 1.17, 1.18 写法相同
                    // 1.17 -> this.type = (EntityTypes)IRegistry.ENTITY_TYPE.fromId(var0.j());
                    // 1.18 -> this.type = (EntityTypes)IRegistry.ENTITY_TYPE.byId(var0.readVarInt());
                    9, 10 -> writeVarInt(NMSIRegistry.ENTITY_TYPE.getId(helper.adapt(entityType) as NMSEntityTypes<*>))
                    // 1.19 写法不同
                    11 -> {
                        when (minor) {
                            // 1.19, 1.19.1, 1.19.2 -> this.type = (EntityTypes)var0.readById(IRegistry.ENTITY_TYPE);
                            0, 1, 2 -> writeVarInt(NMSIRegistry.ENTITY_TYPE.getId(helper.adapt(entityType) as NMSEntityTypes<*>))
                            // 1.19.3, 1.19.4       -> this.type = (EntityTypes)var0.readById(BuiltInRegistries.ENTITY_TYPE);
                            // 注意从该版本开始 RegistryBlocks 的类型发生变化，无法在同一个模块内向下兼容
                            3, 4 -> writeVarInt(NMSJ17.instance.entityTypeGetId(helper.adapt(entityType)))
                            // 其他版本 -> error
                            else -> error("Unsupported version.")
                        }
                    }
                    // 1.12
                    12 -> writeVarInt(NMSJ17.instance.entityTypeGetId(helper.adapt(entityType)))
                }
                writeDouble(location.x)
                writeDouble(location.y)
                writeDouble(location.z)
                // xRot     -> pitch -> 纵向视角
                writeByte(pitch)
                // yRot     -> yaw -> 普通实体没效果
                writeByte(yaw)
                // yHeadRot -> yaw -> 横向视角
                // 1.19 才有这个
                if (major >= 11) {
                    writeByte(yaw)
                    writeVarInt(data)
                } else {
                    writeInt(data)
                }
                writeShort(0)
                writeShort(0)
                writeShort(0)
            }.toNMS() as NMSPacketDataSerializer)
            // 不支持
            else -> error("Unsupported version.")
        }
        // 发送数据包
        packetHandler.sendPacket(player, packet)
    }

    override fun spawnEntityLiving(player: Player, entityType: EntityTypes, entityId: Int, uuid: UUID, location: Location) {
        // 1.13 以下版本盔甲架子不是 EntityLiving 类型，1.19 以上版本所有实体使用 PacketPlayOutSpawnEntity 数据包生成
        if ((entityType == EntityTypes.ARMOR_STAND && majorLegacy < 11300) || majorLegacy >= 11900) {
            return spawnEntity(player, entityType, entityId, uuid, location)
        }
        // 修复视角
        val yf = Adyeshach.api().getEntityFinder().getEntityFromClientEntityId(entityId, player)?.entityType.fixYaw(location.yaw)
        // 计算视角
        val yaw = (yf * 256.0f / 360.0f).toInt().toByte()
        val pitch = (location.pitch * 256.0f / 360.0f).toInt().toByte()
        // 版本判断
        val packet: Any = when (major) {
            // 1.9, 1.10
            1, 2 -> NMS9PacketPlayOutSpawnEntityLiving().also {
                it.a(createDataSerializer {
                    writeVarInt(entityId)
                    writeUUID(uuid)
                    // 转 -> this.c = (byte)EntityTypes.a(var1);
                    // 读 -> this.c = var1.readByte() & 255;
                    // 写 -> var1.writeByte(this.c & 255);
                    writeByte((typeHandler.getBukkitEntityId(entityType) and 255).toByte())
                    writeDouble(location.x)
                    writeDouble(location.y)
                    writeDouble(location.z)
                    writeByte(yaw)
                    writeByte(pitch)
                    writeByte(yaw)
                    writeShort(0)
                    writeShort(0)
                    writeShort(0)
                    // 不反射写进去会崩客户端，米哈游真有你的。
                    NMS9DataWatcher(null).also { dw -> livingDataWatcherSetterM.bindTo(it).invokeWithArguments(dw) }.a(toNMS() as NMS9PacketDataSerializer)
                }.toNMS() as NMS9PacketDataSerializer)
            }
            // 1.11, 1.12, 1.13
            3, 4, 5 -> NMS11PacketPlayOutSpawnEntityLiving().also {
                it.a(createDataSerializer {
                    writeVarInt(entityId)
                    writeUUID(uuid)
                    // 1.13 以下版本使用 Bukkit 的实体 ID
                    if (major != 5) {
                        writeVarInt(typeHandler.getBukkitEntityId(entityType))
                    } else {
                        // 1.13 使用 NMS 的实体 ID, 同时 1.13 版本的 IRegistry.ENTITY_TYPE 无法与 1.14, 1.15, 1.16 版本兼容
                        // 1.13 -> interface IRegistry<T> -> 从 Bukkit 实体 ID 转变为 NMS 实体 ID
                        writeVarInt(NMS13IRegistry.ENTITY_TYPE.a(helper.adapt(entityType) as NMS13EntityTypes<*>))
                    }
                    writeDouble(location.x)
                    writeDouble(location.y)
                    writeDouble(location.z)
                    writeByte(yaw)
                    writeByte(pitch)
                    writeByte(yaw)
                    writeShort(0)
                    writeShort(0)
                    writeShort(0)
                    NMS11DataWatcher(null).also { dw -> livingDataWatcherSetterM.bindTo(it).invokeWithArguments(dw) }.a(toNMS() as NMS11PacketDataSerializer)
                }.toNMS() as NMS11PacketDataSerializer)
            }
            // 1.14, 1.15, 1.16
            6, 7, 8 -> NMS16PacketPlayOutSpawnEntityLiving().also {
                it.a(createDataSerializer {
                    writeVarInt(entityId)
                    writeUUID(uuid)
                    // 1.14, 1.15, 1.16 -> abstract class IRegistry<T> -> IRegistry 类型发生变化
                    writeVarInt(NMS16IRegistry.ENTITY_TYPE.a(helper.adapt(entityType) as NMS16EntityTypes<*>))
                    writeDouble(location.x)
                    writeDouble(location.y)
                    writeDouble(location.z)
                    writeByte(yaw)
                    writeByte(pitch)
                    writeByte(yaw)
                    writeShort(0)
                    writeShort(0)
                    writeShort(0)
                    // 1.14, 1.15 仍需要读取 DataWatcher
                    if (major != 8) {
                        NMS14DataWatcher(null).also { dw -> livingDataWatcherSetterM.bindTo(it).invokeWithArguments(dw) }.a(toNMS() as NMS14PacketDataSerializer)
                    }
                }.toNMS() as NMS16PacketDataSerializer)
            }
            // 1.17, 1.18
            // 使用带有 DataSerializer 的构造函数生成数据包
            9, 10 -> NMSPacketPlayOutSpawnEntityLiving(createDataSerializer {
                writeVarInt(entityId)
                writeUUID(uuid)
                // 1.17, 1.18 写法相同
                // 1.17 -> this.type = (EntityTypes)IRegistry.ENTITY_TYPE.fromId(var0.j());
                // 1.18 -> this.type = (EntityTypes)IRegistry.ENTITY_TYPE.byId(var0.readVarInt());
                writeVarInt(NMSIRegistry.ENTITY_TYPE.getId(helper.adapt(entityType) as NMSEntityTypes<*>))
                writeDouble(location.x)
                writeDouble(location.y)
                writeDouble(location.z)
                // yRot -> yaw
                writeByte(yaw)
                // xRot -> pitch
                writeByte(pitch)
                // yHeadRot -> yaw
                writeByte(yaw)
                writeShort(0)
                writeShort(0)
                writeShort(0)
            }.toNMS() as NMSPacketDataSerializer)
            // 不支持
            else -> error("Unsupported version.")
        }
        // 发送数据包
        packetHandler.sendPacket(player, packet)
    }

    override fun spawnNamedEntity(player: Player, entityId: Int, uuid: UUID, location: Location) {
        // 1.20.2 开始，玩家实体使用 PacketPlayOutSpawnEntity 数据包生成
        if (majorLegacy >= 12002) {
            return spawnEntity(player, EntityTypes.PLAYER, entityId, uuid, location)
        }
        // 计算视角
        val yaw = (location.yaw * 256.0f / 360.0f).toInt().toByte()
        val pitch = (location.pitch * 256.0f / 360.0f).toInt().toByte()
        // 判断版本
        val packet: Any = when (major) {
            // 1.9, 1.10, 1.11, 1.12, 1.13, 1.14
            in 1..6 -> NMS9PacketPlayOutSpawnEntityPlayer().also {
                it.a(createDataSerializer {
                    writeVarInt(entityId)
                    writeUUID(uuid)
                    writeDouble(location.x)
                    writeDouble(location.y)
                    writeDouble(location.z)
                    writeByte(yaw)
                    writeByte(pitch)
                    NMS9DataWatcher(null).also { dw -> namedDataWatcherSetterH.bindTo(it).invokeWithArguments(dw) }.a(toNMS() as NMS9PacketDataSerializer)
                }.toNMS() as NMS9PacketDataSerializer)
            }
            // 1.15, 1.16
            7, 8 -> NMS16PacketPlayOutSpawnEntityPlayer().also {
                it.a(createDataSerializer {
                    writeVarInt(entityId)
                    writeUUID(uuid)
                    writeDouble(location.x)
                    writeDouble(location.y)
                    writeDouble(location.z)
                    writeByte(yaw)
                    writeByte(pitch)
                }.toNMS() as NMS16PacketDataSerializer)
            }
            // 1.17, 1.18, 1.19, 1.20
            // 使用带有 DataSerializer 的构造函数生成数据包
            9, 10, 11, 12 -> NMSPacketPlayOutSpawnEntityPlayer(createDataSerializer {
                writeVarInt(entityId)
                writeUUID(uuid)
                writeDouble(location.x)
                writeDouble(location.y)
                writeDouble(location.z)
                writeByte(yaw)
                writeByte(pitch)
            }.toNMS() as NMSPacketDataSerializer)
            // 不支持
            else -> error("Unsupported version.")
        }
        // 发送数据包
        packetHandler.sendPacket(player, packet)
    }

    override fun spawnEntityFallingBlock(player: Player, entityId: Int, uuid: UUID, location: Location, material: Material, data: Byte) {
        if (majorLegacy >= 11300) {
            spawnEntity(player, EntityTypes.FALLING_BLOCK, entityId, uuid, location, helper.getBlockId(MaterialData(material, data)))
        } else {
            spawnEntity(player, EntityTypes.FALLING_BLOCK, entityId, uuid, location, material.id + (data.toInt() shl 12))
        }
    }

    override fun spawnEntityExperienceOrb(player: Player, entityId: Int, location: Location, amount: Int) {
        if (isUniversal) {
            packetHandler.sendPacket(player, NMSPacketPlayOutSpawnEntityExperienceOrb(createDataSerializer {
                writeVarInt(entityId)
                writeDouble(location.x)
                writeDouble(location.y)
                writeDouble(location.z)
                writeShort(amount.toShort())
            }.toNMS() as NMSPacketDataSerializer))
        } else {
            packetHandler.sendPacket(player, NMS16PacketPlayOutSpawnEntityExperienceOrb().also {
                it.a(createDataSerializer {
                    writeVarInt(entityId)
                    writeDouble(location.x)
                    writeDouble(location.y)
                    writeDouble(location.z)
                    writeShort(amount.toShort())
                }.toNMS() as NMS16PacketDataSerializer)
            })
        }
    }

    override fun spawnEntityPainting(player: Player, entityId: Int, uuid: UUID, location: Location, direction: BukkitDirection, painting: BukkitPaintings) {
        if (MinecraftVersion.majorLegacy >= 11900) {
            error("spawnEntityPainting() is not supported in this version")
        }
        // 使用带有 DataSerializer 的构造函数生成数据包
        // 使用 IRegistry.MOTIVE
        if (isUniversal) {
            packetHandler.sendPacket(player, NMSPacketPlayOutSpawnEntityPainting(createDataSerializer {
                writeVarInt(entityId)
                writeUUID(uuid)
                writeVarInt(motiveCache.get(painting)!!)
                writeBlockPosition(location.blockX, location.blockY, location.blockZ)
                writeByte(direction.get2DRotationValue().toByte())
            }.toNMS() as NMSPacketDataSerializer))
        }
        // 使用 IRegistry.MOTIVE
        else if (majorLegacy > 11300) {
            packetHandler.sendPacket(player, NMS16PacketPlayOutSpawnEntityPainting().also {
                it.a(createDataSerializer {
                    writeVarInt(entityId)
                    writeUUID(uuid)
                    writeVarInt(motiveCache.get(painting)!!)
                    writeBlockPosition(location.blockX, location.blockY, location.blockZ)
                    writeByte(direction.get2DRotationValue().toByte())
                }.toNMS() as NMS16PacketDataSerializer)
            })
        }
        // 使用字符串
        else {
            packetHandler.sendPacket(player, NMS9PacketPlayOutSpawnEntityPainting().also {
                it.a(createDataSerializer {
                    writeVarInt(entityId)
                    writeUUID(uuid)
                    writeString(painting.name)
                    writeBlockPosition(location.blockX, location.blockY, location.blockZ)
                    writeByte(direction.get2DRotationValue().toByte())
                }.toNMS() as NMS9PacketDataSerializer)
            })
        }
    }

    fun BukkitDirection.get2DRotationValue(): Int {
        return when (this) {
            BukkitDirection.SOUTH -> 0
            BukkitDirection.WEST -> 1
            BukkitDirection.NORTH -> 2
            BukkitDirection.EAST -> 3
            else -> error("Unsupported direction.")
        }
    }

    fun clamp(value: Double, min: Double, max: Double): Double {
        return if (value < min) min else if (value > max) max else value
    }
}