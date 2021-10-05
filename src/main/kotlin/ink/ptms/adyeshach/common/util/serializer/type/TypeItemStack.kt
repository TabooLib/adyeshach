package ink.ptms.adyeshach.common.util.serializer.type

import ink.ptms.adyeshach.common.util.serializer.Serializer
import ink.ptms.adyeshach.common.util.serializer.SerializerType
import com.google.gson.*
import org.bukkit.inventory.ItemStack
import java.lang.reflect.Type

/**
 * @author sky
 * @since 2020-08-20 20:10
 */
@SerializerType(baseClass = ItemStack::class)
class TypeItemStack : JsonSerializer<ItemStack>, JsonDeserializer<ItemStack> {

    override fun serialize(a: ItemStack, p1: Type, p2: JsonSerializationContext): JsonElement {
        return JsonPrimitive(Serializer.fromItemStack(a))
    }

    override fun deserialize(a: JsonElement, p1: Type?, p2: JsonDeserializationContext): ItemStack {
        return Serializer.toItemStack(a.asString)
    }
}