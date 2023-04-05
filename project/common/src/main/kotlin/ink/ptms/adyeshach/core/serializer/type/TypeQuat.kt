package ink.ptms.adyeshach.core.serializer.type

import com.google.gson.*
import ink.ptms.adyeshach.core.serializer.SerializerType
import taboolib.common5.Quat
import java.lang.reflect.Type

/**
 * @author sky
 * @since 2020-08-20 20:10
 */
@SerializerType(baseClass = Quat::class)
class TypeQuat : JsonSerializer<Quat>, JsonDeserializer<Quat> {

    override fun serialize(a: Quat, p1: Type, p2: JsonSerializationContext): JsonElement {
        return JsonObject().run {
            addProperty("x", a.x())
            addProperty("y", a.y())
            addProperty("z", a.z())
            addProperty("w", a.w())
            this
        }
    }

    override fun deserialize(a: JsonElement, p1: Type?, p2: JsonDeserializationContext): Quat {
        return Quat(a.asJsonObject.get("x").asDouble, a.asJsonObject.get("y").asDouble, a.asJsonObject.get("z").asDouble, a.asJsonObject.get("w").asDouble)
    }
}