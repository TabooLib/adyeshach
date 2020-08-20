package ink.ptms.adyeshach.common.util.serializer.type

import ink.ptms.adyeshach.common.util.serializer.Serializer
import ink.ptms.adyeshach.common.util.serializer.SerializerType
import io.izzel.taboolib.internal.gson.*
import io.izzel.taboolib.util.chat.TextComponent
import org.bukkit.inventory.ItemStack
import java.lang.reflect.Type
import java.util.*

/**
 * @Author sky
 * @Since 2020-08-20 20:10
 */
@SerializerType(baseClass = TextComponent::class)
class TypeTextComponent : JsonSerializer<TextComponent>, JsonDeserializer<TextComponent> {

    override fun serialize(a: TextComponent, p1: Type, p2: JsonSerializationContext): JsonElement {
        return JsonPrimitive(a.text)
    }

    override fun deserialize(a: JsonElement, p1: Type?, p2: JsonDeserializationContext): TextComponent {
        return TextComponent(a.asString)
    }
}