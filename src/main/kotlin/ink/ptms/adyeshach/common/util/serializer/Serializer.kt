package ink.ptms.adyeshach.common.util.serializer

import ink.ptms.adyeshach.Adyeshach
import io.izzel.taboolib.TabooLibLoader
import io.izzel.taboolib.internal.gson.*
import org.bukkit.inventory.ItemStack
import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.*

object Serializer {

    val gson = GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().run {
        TabooLibLoader.getPluginClassSafely(Adyeshach.plugin).forEach { clazz: Class<*> ->
            if (clazz.isAnnotationPresent(SerializerType::class.java)) {
                registerTypeHierarchyAdapter(clazz.getAnnotation(SerializerType::class.java).baseClass.java, clazz.newInstance())
            }
        }
        create()
    }!!

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