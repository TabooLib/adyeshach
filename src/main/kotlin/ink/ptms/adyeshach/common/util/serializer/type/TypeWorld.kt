package ink.ptms.adyeshach.common.util.serializer.type

import ink.ptms.adyeshach.common.util.serializer.SerializerType
import ink.ptms.adyeshach.common.util.serializer.UnknownWorldException
import io.izzel.taboolib.internal.gson.*
import org.bukkit.Bukkit
import org.bukkit.World
import java.lang.reflect.Type

/**
 * @Author sky
 * @Since 2020-08-20 20:10
 */
@SerializerType(baseClass = World::class)
class TypeWorld : JsonSerializer<World>, JsonDeserializer<World> {

    override fun serialize(a: World, p1: Type, p2: JsonSerializationContext): JsonElement {
        return JsonPrimitive(a.name)
    }

    override fun deserialize(a: JsonElement, p1: Type?, p2: JsonDeserializationContext): World {
        return Bukkit.getWorld(a.asString) ?: throw UnknownWorldException(a.asString)
    }
}