package ink.ptms.adyeshach.common.util.serializer.type

import ink.ptms.adyeshach.common.util.serializer.Serializer
import ink.ptms.adyeshach.common.util.serializer.SerializerType
import io.izzel.taboolib.internal.gson.*
import io.izzel.taboolib.module.db.local.SecuredFile
import io.izzel.taboolib.util.chat.TextComponent
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.inventory.ItemStack
import java.lang.reflect.Type
import java.util.*

/**
 * @Author sky
 * @Since 2020-08-20 20:10
 */
@SerializerType(baseClass = YamlConfiguration::class)
class TypeYamlConfiguration : JsonSerializer<YamlConfiguration>, JsonDeserializer<YamlConfiguration> {

    override fun serialize(a: YamlConfiguration, p1: Type, p2: JsonSerializationContext): JsonElement {
        return JsonPrimitive(a.saveToString())
    }

    override fun deserialize(a: JsonElement, p1: Type?, p2: JsonDeserializationContext): YamlConfiguration {
        return SecuredFile.loadConfiguration(a.asString)
    }
}