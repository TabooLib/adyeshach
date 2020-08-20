package ink.ptms.adyeshach.common.util.serializer.type

import ink.ptms.adyeshach.common.util.serializer.Serializer
import ink.ptms.adyeshach.common.util.serializer.SerializerType
import io.izzel.taboolib.internal.gson.*
import org.bukkit.inventory.ItemStack
import org.bukkit.util.EulerAngle
import java.lang.reflect.Type
import java.util.*

/**
 * @Author sky
 * @Since 2020-08-20 20:10
 */
@SerializerType(baseClass = EulerAngle::class)
class TypeEulerAngle : JsonSerializer<EulerAngle>, JsonDeserializer<EulerAngle> {

    override fun serialize(a: EulerAngle, p1: Type, p2: JsonSerializationContext): JsonElement {
        return JsonObject().run {
            addProperty("x", a.x)
            addProperty("y", a.y)
            addProperty("z", a.z)
            this
        }
    }

    override fun deserialize(a: JsonElement, p1: Type?, p2: JsonDeserializationContext): EulerAngle {
        return EulerAngle(
                a.asJsonObject.get("x").asDouble,
                a.asJsonObject.get("y").asDouble,
                a.asJsonObject.get("z").asDouble
        )
    }
}