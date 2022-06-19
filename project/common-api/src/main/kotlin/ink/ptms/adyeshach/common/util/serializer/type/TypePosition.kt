package ink.ptms.adyeshach.common.util.serializer.type

import com.google.gson.*
import ink.ptms.adyeshach.common.bukkit.data.EmptyVector
import ink.ptms.adyeshach.common.util.serializer.SerializerType
import org.bukkit.util.Vector
import java.lang.reflect.Type

/**
 * @author sky
 * @since 2020-08-20 20:10
 */
@SerializerType(baseClass = Vector::class)
class TypePosition : JsonSerializer<Vector>, JsonDeserializer<Vector> {

    override fun serialize(a: Vector, p1: Type, p2: JsonSerializationContext): JsonElement {
        return JsonObject().run {
            addProperty("x", a.x)
            addProperty("y", a.y)
            addProperty("z", a.z)
            addProperty("empty", a is EmptyVector)
            this
        }
    }

    override fun deserialize(a: JsonElement, p1: Type?, p2: JsonDeserializationContext): Vector {
        return if (!a.asJsonObject.get("empty").asBoolean) {
            Vector(a.asJsonObject.get("x").asInt, a.asJsonObject.get("y").asInt, a.asJsonObject.get("z").asInt)
        } else {
            EmptyVector()
        }
    }
}