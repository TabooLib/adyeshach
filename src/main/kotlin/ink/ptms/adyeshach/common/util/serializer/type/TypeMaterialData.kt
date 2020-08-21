package ink.ptms.adyeshach.common.util.serializer.type

import ink.ptms.adyeshach.common.util.serializer.SerializerType
import io.izzel.taboolib.internal.gson.*
import org.bukkit.Material
import org.bukkit.material.MaterialData
import java.lang.reflect.Type

/**
 * @Author sky
 * @Since 2020-08-20 20:10
 */
@SerializerType(baseClass = MaterialData::class)
class TypeMaterialData : JsonSerializer<MaterialData>, JsonDeserializer<MaterialData> {

    override fun serialize(a: MaterialData, p1: Type, p2: JsonSerializationContext): JsonElement {
        return JsonObject().run {
            addProperty("type", a.itemType.name)
            addProperty("data", a.data)
            this
        }
    }

    override fun deserialize(a: JsonElement, p1: Type?, p2: JsonDeserializationContext): MaterialData {
        return MaterialData(Material.getMaterial(a.asJsonObject.get("type").asString), a.asJsonObject.get("data").asByte)
    }
}