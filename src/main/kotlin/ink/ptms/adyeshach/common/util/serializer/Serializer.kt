package ink.ptms.adyeshach.common.util.serializer

import ink.ptms.adyeshach.common.entity.element.NullPosition
import io.izzel.taboolib.internal.gson.*
import io.izzel.taboolib.module.db.local.SecuredFile
import io.izzel.taboolib.module.nms.impl.Position
import io.izzel.taboolib.util.chat.TextComponent
import org.bukkit.Material
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.inventory.ItemStack
import org.bukkit.material.MaterialData
import org.bukkit.util.EulerAngle
import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.*

object Serializer {

    val gson = GsonBuilder().setPrettyPrinting()
            .excludeFieldsWithoutExposeAnnotation()
            .registerTypeAdapter(UUID::class.java, JsonSerializer<UUID> { a, _, _ -> JsonPrimitive(a.toString()) })
            .registerTypeAdapter(UUID::class.java, JsonDeserializer { a, _, _ -> UUID.fromString(a.asString) })
            .registerTypeAdapter(YamlConfiguration::class.java, JsonSerializer<YamlConfiguration> { a, _, _ -> JsonPrimitive(a.saveToString()) })
            .registerTypeAdapter(YamlConfiguration::class.java, JsonDeserializer<YamlConfiguration> { a, _, _ -> SecuredFile.loadConfiguration(a.asString) })
            .registerTypeAdapter(TextComponent::class.java, JsonSerializer<TextComponent> { a, _, _ -> JsonPrimitive(a.text) })
            .registerTypeAdapter(TextComponent::class.java, JsonDeserializer { a, _, _ -> TextComponent(a.asString) })
            .registerTypeAdapter(ItemStack::class.java, JsonSerializer<ItemStack> { a, _, _ -> JsonPrimitive(fromItemStack(a)) })
            .registerTypeAdapter(ItemStack::class.java, JsonDeserializer { a, _, _ -> toItemStack(a.asString) })
            .registerTypeAdapter(MaterialData::class.java, JsonSerializer<MaterialData> { a, _, _ ->
                JsonObject().run {
                    addProperty("type", a.itemType.name)
                    addProperty("data", a.data)
                    this
                }
            })
            .registerTypeAdapter(MaterialData::class.java, JsonDeserializer { a, _, _ ->
                MaterialData(Material.getMaterial(a.asJsonObject.get("type").asString), a.asJsonObject.get("data").asByte)
            })
            .registerTypeAdapter(EulerAngle::class.java, JsonSerializer<EulerAngle> { a, _, _ ->
                JsonObject().run {
                    addProperty("x", a.x)
                    addProperty("y", a.y)
                    addProperty("z", a.z)
                    this
                }
            })
            .registerTypeAdapter(EulerAngle::class.java, JsonDeserializer { a, _, _ ->
                EulerAngle(a.asJsonObject.get("x").asDouble, a.asJsonObject.get("y").asDouble, a.asJsonObject.get("z").asDouble)
            })
            .registerTypeAdapter(Position::class.java, JsonSerializer<Position> { a, _, _ ->
                JsonObject().run {
                    addProperty("x", a.x)
                    addProperty("y", a.y)
                    addProperty("z", a.z)
                    addProperty("empty", a is NullPosition)
                    this
                }
            })
            .registerTypeAdapter(Position::class.java, JsonDeserializer { a, _, _ ->
                if (a.asJsonObject.get("empty").asBoolean) {
                    NullPosition()
                } else {
                    Position(a.asJsonObject.get("x").asInt, a.asJsonObject.get("y").asInt, a.asJsonObject.get("z").asInt)
                }
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