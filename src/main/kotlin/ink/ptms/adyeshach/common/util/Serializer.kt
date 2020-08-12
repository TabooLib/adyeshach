package ink.ptms.adyeshach.common.util

import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.type.EntityTypes
import io.izzel.taboolib.internal.gson.*
import io.izzel.taboolib.internal.gson.stream.JsonReader
import io.izzel.taboolib.internal.gson.stream.JsonWriter
import io.izzel.taboolib.module.db.local.SecuredFile
import io.izzel.taboolib.util.chat.TextComponent
import org.bukkit.Bukkit
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.util.EulerAngle
import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.*

object Serializer {

    val serializerEntity = GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation()
            .registerTypeAdapter(YamlConfiguration::class.java, JsonSerializer<YamlConfiguration> { a, _, _ -> JsonPrimitive(a.saveToString()) })
            .registerTypeAdapter(YamlConfiguration::class.java, JsonDeserializer<YamlConfiguration> { a, _, _ -> SecuredFile.loadConfiguration(a.asString) })
            .registerTypeAdapter(TextComponent::class.java, JsonSerializer<TextComponent> { a, _, _ -> JsonPrimitive(a.text) })
            .registerTypeAdapter(TextComponent::class.java, JsonDeserializer<TextComponent> { a, _, _ -> TextComponent(a.asString) })
            .registerTypeAdapter(ItemStack::class.java, JsonSerializer<ItemStack> { a, _, _ -> JsonPrimitive(fromItemStack(a)) })
            .registerTypeAdapter(ItemStack::class.java, JsonDeserializer<ItemStack> { a, _, _ -> toItemStack(a.asString) })
            .registerTypeAdapter(EulerAngle::class.java, JsonSerializer<EulerAngle> { a, _, _ ->
                JsonObject().run {
                    addProperty("x", a.x)
                    addProperty("y", a.y)
                    addProperty("z", a.z)
                    this
                }
            })
            .registerTypeAdapter(EulerAngle::class.java, JsonDeserializer<EulerAngle> { a, _, _ ->
                EulerAngle(a.asJsonObject.get("x").asDouble, a.asJsonObject.get("y").asDouble, a.asJsonObject.get("z").asDouble)
            }).create()

    @Suppress("UNCHECKED_CAST")
    fun toItemStack(data: String): ItemStack {
        ByteArrayInputStream(Base64.getDecoder().decode(data)).use { byteArrayInputStream ->
            BukkitObjectInputStream(byteArrayInputStream).use { bukkitObjectInputStream ->
                return bukkitObjectInputStream.readObject() as ItemStack
            }
        }
    }

    fun fromItemStack(itemStack: ItemStack): String {
        ByteArrayOutputStream().use { byteArrayOutputStream ->
            BukkitObjectOutputStream(byteArrayOutputStream).use { bukkitObjectOutputStream ->
                bukkitObjectOutputStream.writeObject(itemStack)
                return Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray())
            }
        }
    }
}