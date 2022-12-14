package ink.ptms.adyeshach.core.serializer.type

import com.google.gson.*
import ink.ptms.adyeshach.core.serializer.SerializerType
import org.bukkit.Material
import org.bukkit.material.MaterialData
import taboolib.library.xseries.XMaterial
import java.lang.reflect.Type

/**
 * @author sky
 * @since 2020-08-20 20:10
 */
@SerializerType(baseClass = MaterialData::class)
class TypeMaterialData : JsonSerializer<MaterialData>, JsonDeserializer<MaterialData> {

    override fun serialize(a: MaterialData, p1: Type, p2: JsonSerializationContext): JsonElement {
        return JsonObject().run {
            addProperty("type", XMaterial.matchXMaterial(a.itemType).name)
            addProperty("data", a.data)
            this
        }
    }

    override fun deserialize(a: JsonElement, p1: Type?, p2: JsonDeserializationContext): MaterialData {
        return MaterialData(Material.getMaterial(a.asJsonObject.get("type").asString), a.asJsonObject.get("data").asByte)
    }
}