package ink.ptms.adyeshach.common.util.serializer.type

import ink.ptms.adyeshach.common.entity.element.PositionNull
import ink.ptms.adyeshach.common.util.serializer.SerializerType
import io.izzel.taboolib.internal.gson.*
import io.izzel.taboolib.module.nms.impl.Position
import java.lang.reflect.Type

/**
 * @Author sky
 * @Since 2020-08-20 20:10
 */
@SerializerType(baseClass = Position::class)
class TypePosition : JsonSerializer<Position>, JsonDeserializer<Position> {

    override fun serialize(a: Position, p1: Type, p2: JsonSerializationContext): JsonElement {
        return JsonObject().run {
            addProperty("x", a.x)
            addProperty("y", a.y)
            addProperty("z", a.z)
            addProperty("empty", a is PositionNull)
            this
        }
    }

    override fun deserialize(a: JsonElement, p1: Type?, p2: JsonDeserializationContext): Position {
        return if (a.asJsonObject.get("empty").asBoolean) {
            PositionNull()
        } else {
            Position(
                    a.asJsonObject.get("x").asInt,
                    a.asJsonObject.get("y").asInt,
                    a.asJsonObject.get("z").asInt
            )
        }
    }
}