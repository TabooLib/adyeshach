package ink.ptms.adyeshach.common.util.serializer.type

import com.google.common.base.Enums
import ink.ptms.adyeshach.common.entity.element.VillagerData
import ink.ptms.adyeshach.common.util.serializer.Serializer
import ink.ptms.adyeshach.common.util.serializer.SerializerType
import io.izzel.taboolib.internal.gson.*
import org.bukkit.entity.Villager
import org.bukkit.inventory.ItemStack
import java.lang.reflect.Type
import java.util.*

/**
 * @Author sky
 * @Since 2020-08-20 20:10
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
        return  VillagerData(
                Enums.getIfPresent(Villager.Type::class.java, a.asJsonObject.get("type").asString).get(),
                Enums.getIfPresent(Villager.Profession::class.java, a.asJsonObject.get("profession").asString).get()
        )
    }
}