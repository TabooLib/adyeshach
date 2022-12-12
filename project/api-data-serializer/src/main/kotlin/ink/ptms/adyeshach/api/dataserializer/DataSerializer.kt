package ink.ptms.adyeshach.api.dataserializer

import com.google.common.base.Charsets
import ink.ptms.adyeshach.common.api.MinecraftMeta
import java.util.*

/**
 * Adyeshach
 * ink.ptms.adyeshach.api.dataserializer.DataSerializer
 *
 * @author 坏黑
 * @since 2022/12/12 23:00
 */
interface DataSerializer {

    fun writeByte(byte: Byte): DataSerializer

    fun writeBytes(bytes: ByteArray): DataSerializer

    fun writeShort(short: Short): DataSerializer

    fun writeInt(int: Int): DataSerializer

    fun writeLong(long: Long): DataSerializer

    fun writeFloat(float: Float): DataSerializer

    fun writeDouble(double: Double): DataSerializer

    fun writeBoolean(boolean: Boolean): DataSerializer

    fun writeMetadata(meta: List<MinecraftMeta>): DataSerializer

    fun writeUUID(uuid: UUID): DataSerializer {
        writeLong(uuid.mostSignificantBits)
        writeLong(uuid.leastSignificantBits)
        return this
    }

    fun writeString(string: String): DataSerializer {
        val arr: ByteArray = string.toByteArray(Charsets.UTF_8)
        if (arr.size > 32767) {
            error("String too big (was ${string.length} bytes encoded, max 32767)")
        } else {
            writeVarInt(arr.size)
            writeBytes(arr)
        }
        return this
    }

    fun writeVarInt(int: Int): DataSerializer {
        var i = int
        while (i and -128 != 0) {
            writeByte((i and 127 or 128).toByte())
            i = i ushr 7
        }
        writeByte(i.toByte())
        return this
    }

    fun writeVarIntArray(intArray: IntArray): DataSerializer {
        writeVarInt(intArray.size)
        intArray.forEach { writeVarInt(it) }
        return this
    }

    fun writeBlockPosition(x: Int, y: Int, z: Int): DataSerializer {
        writeLong((x.toLong() and 67108863 shl 38) or (y.toLong() and 4095 shl 26) or (z.toLong() and 67108863 shl 0))
        return this
    }

    fun toNMS(): Any
}

fun createDataSerializer(builder: DataSerializer.() -> Unit): DataSerializer {
    return DataSerializerFactory.instance.newSerializer().also(builder)
}