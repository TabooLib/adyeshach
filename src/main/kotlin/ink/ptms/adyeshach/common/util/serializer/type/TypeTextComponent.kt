package ink.ptms.adyeshach.common.util.serializer.type

import ink.ptms.adyeshach.common.util.serializer.SerializerType
import com.google.gson.*
import net.md_5.bungee.api.chat.TextComponent
import java.lang.reflect.Type

/**
 * @author sky
 * @since 2020-08-20 20:10
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