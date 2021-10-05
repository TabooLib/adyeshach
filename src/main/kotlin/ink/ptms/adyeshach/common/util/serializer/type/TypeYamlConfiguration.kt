package ink.ptms.adyeshach.common.util.serializer.type

import com.google.gson.*
import ink.ptms.adyeshach.common.util.serializer.SerializerType
import taboolib.library.configuration.FileConfiguration
import taboolib.module.configuration.SecuredFile
import java.lang.reflect.Type

/**
 * @author sky
 * @since 2020-08-20 20:10
 */
@SerializerType(baseClass = FileConfiguration::class)
class TypeYamlConfiguration : JsonSerializer<FileConfiguration>, JsonDeserializer<FileConfiguration> {

    override fun serialize(a: FileConfiguration, p1: Type, p2: JsonSerializationContext): JsonElement {
        return JsonPrimitive(a.saveToString())
    }

    override fun deserialize(a: JsonElement, p1: Type?, p2: JsonDeserializationContext): FileConfiguration {
        return SecuredFile.loadConfiguration(a.asString)
    }
}