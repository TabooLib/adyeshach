package ink.ptms.adyeshach.core.serializer.type

import com.google.gson.*
import ink.ptms.adyeshach.core.bukkit.data.VillagerData
import ink.ptms.adyeshach.core.serializer.SerializerType
import ink.ptms.adyeshach.core.util.getEnum
import java.lang.reflect.Type

/**
 * @author sky
 * @since 2020-08-20 20:10
 */
@SerializerType(baseClass = VillagerData::class)
class TypeVillagerData : JsonSerializer<VillagerData>, JsonDeserializer<VillagerData> {

    override fun serialize(a: VillagerData, p1: Type, p2: JsonSerializationContext): JsonElement {
        return JsonObject().run {
            addProperty("type", a.type.name)
            addProperty("profession", a.profession.name)
            this
        }
    }

    override fun deserialize(a: JsonElement, p1: Type?, p2: JsonDeserializationContext): VillagerData {
        return VillagerData(
            VillagerData.Type::class.java.getEnum(a.asJsonObject.get("type").asString),
            VillagerData.Profession::class.java.getEnum(a.asJsonObject.get("profession").asString)
        )
    }
}