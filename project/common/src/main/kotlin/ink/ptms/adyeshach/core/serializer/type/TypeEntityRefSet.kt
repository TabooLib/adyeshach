package ink.ptms.adyeshach.core.serializer.type

import com.google.gson.*
import ink.ptms.adyeshach.core.entity.EntityRefSet
import ink.ptms.adyeshach.core.serializer.SerializerType
import java.lang.reflect.Type

/**
 * Adyeshach
 * ink.ptms.adyeshach.core.serializer.type.TypeEntityRefSet
 *
 * EntityRefSet 的序列化/反序列化适配器
 * - 序列化：将 EntityInstance 集合转换为 UUID 字符串数组
 * - 反序列化：将 UUID 字符串数组存入 pendingUuids，等待后续 resolve
 */
@SerializerType(baseClass = EntityRefSet::class)
class TypeEntityRefSet : JsonSerializer<EntityRefSet>, JsonDeserializer<EntityRefSet> {

    override fun serialize(src: EntityRefSet, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        val array = JsonArray()
        src.getUuids().forEach { array.add(it) }
        return array
    }

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): EntityRefSet {
        val set = EntityRefSet()
        if (json.isJsonArray) {
            json.asJsonArray.forEach { element ->
                if (element.isJsonPrimitive && element.asJsonPrimitive.isString) {
                    set.pendingUuids.add(element.asString)
                }
            }
        }
        return set
    }
}
