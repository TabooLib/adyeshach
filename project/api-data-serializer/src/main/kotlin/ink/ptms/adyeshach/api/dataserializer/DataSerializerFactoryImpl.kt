package ink.ptms.adyeshach.api.dataserializer

import ink.ptms.adyeshach.core.MinecraftMeta
import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufOutputStream
import io.netty.buffer.Unpooled
import net.minecraft.server.v1_9_R2.DataWatcher
import net.minecraft.server.v1_9_R2.PacketDataSerializer
import java.io.DataOutput
import java.io.DataOutputStream

/**
 * Adyeshach
 * ink.ptms.adyeshach.api.dataserializer.DataSerializerFactoryImpl
 *
 * @author 坏黑
 * @since 2022/12/12 23:30
 */
@Suppress("UNCHECKED_CAST")
class DataSerializerFactoryImpl(val buf: ByteBuf) : DataSerializerFactory, DataSerializer {

    constructor() : this(Unpooled.buffer())

    override fun writeByte(byte: Byte): DataSerializer {
        return buf.writeByte(byte.toInt()).let { this }
    }

    override fun writeBytes(bytes: ByteArray): DataSerializer {
        return buf.writeBytes(bytes).let { this }
    }

    override fun writeShort(short: Short): DataSerializer {
        return buf.writeShort(short.toInt()).let { this }
    }

    override fun writeInt(int: Int): DataSerializer {
        return buf.writeInt(int).let { this }
    }

    override fun writeLong(long: Long): DataSerializer {
        return buf.writeLong(long).let { this }
    }

    override fun writeFloat(float: Float): DataSerializer {
        return buf.writeFloat(float).let { this }
    }

    override fun writeDouble(double: Double): DataSerializer {
        return buf.writeDouble(double).let { this }
    }

    override fun writeBoolean(boolean: Boolean): DataSerializer {
        return buf.writeBoolean(boolean).let { this }
    }

    override fun writeMetadata(meta: List<MinecraftMeta>): DataSerializer {
        return DataWatcher.a(meta.map { it.source() } as List<DataWatcher.Item<*>>, buf as PacketDataSerializer).let { this }
    }

    override fun toNMS(): Any {
        return buf
    }

    override fun dataOutput(): DataOutput {
        return ByteBufOutputStream(buf)
    }

    override fun newSerializer(): DataSerializer {
        return DataSerializerFactoryImpl(PacketDataSerializer(Unpooled.buffer()))
    }
}