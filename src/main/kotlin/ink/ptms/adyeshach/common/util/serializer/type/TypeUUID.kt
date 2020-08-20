package ink.ptms.adyeshach.common.util.serializer.type

import ink.ptms.adyeshach.common.util.serializer.SerializerType
import io.izzel.taboolib.internal.gson.*
import java.lang.reflect.Type
import java.util.*

/**
 * @Author sky
 * @Since 2020-08-20 20:10
 */
@SerializerType(baseClass = UUID::class)
class TypeUUID : JsonSerializer<UUID>, JsonDeserializer<UUID> {

    override fun serialize(a: UUID, p1: Type, p2: JsonSerializationContext): JsonElement {
        return JsonPrimitive(a.toString())
    }

    override fun deserialize(a: JsonElement, p1: Type?, p2: JsonDeserializationContext): UUID {
        return UUID.fromString(a.asString)
    }
}