package ink.ptms.adyeshach.core.serializer.type

import com.eatthepath.uuid.FastUUID
import com.google.gson.*
import ink.ptms.adyeshach.core.serializer.SerializerType
import java.lang.reflect.Type
import java.util.*

/**
 * @author sky
 * @since 2020-08-20 20:10
 */
@SerializerType(baseClass = UUID::class)
class TypeUUID : JsonSerializer<UUID>, JsonDeserializer<UUID> {

    override fun serialize(a: UUID, p1: Type, p2: JsonSerializationContext): JsonElement {
        return JsonPrimitive(a.toString())
    }

    override fun deserialize(a: JsonElement, p1: Type?, p2: JsonDeserializationContext): UUID {
        return FastUUID.parseUUID(a.asString)
    }
}