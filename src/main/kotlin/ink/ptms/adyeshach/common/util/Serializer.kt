package ink.ptms.adyeshach.common.util

import io.izzel.taboolib.internal.gson.GsonBuilder
import io.izzel.taboolib.internal.gson.JsonDeserializer
import io.izzel.taboolib.internal.gson.JsonPrimitive
import io.izzel.taboolib.internal.gson.JsonSerializer
import io.izzel.taboolib.module.db.local.SecuredFile
import io.izzel.taboolib.util.item.Items
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.inventory.ItemStack
import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.*

object Serializer {

    val serializer = GsonBuilder().excludeFieldsWithoutExposeAnnotation()
            .registerTypeAdapter(ItemStack::class.java, JsonSerializer<ItemStack> { a, _, _ -> JsonPrimitive(fromItemStack(a)) })
            .registerTypeAdapter(ItemStack::class.java, JsonDeserializer<ItemStack> { a, _, _ -> toItemStack(a.asString) })
            .registerTypeAdapter(YamlConfiguration::class.java, JsonSerializer<YamlConfiguration> { a, _, _ -> JsonPrimitive(a.saveToString()) })
            .registerTypeAdapter(YamlConfiguration::class.java, JsonDeserializer<YamlConfiguration> { a, _, _ -> SecuredFile.loadConfiguration(a.asString) }).create()

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