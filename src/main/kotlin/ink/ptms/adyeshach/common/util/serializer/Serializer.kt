package ink.ptms.adyeshach.common.util.serializer

import com.google.gson.GsonBuilder
import org.bukkit.inventory.ItemStack
import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream
import taboolib.common.io.runningClasses
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.*

object Serializer {

    val gson = GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().run {
        runningClasses.forEach {
            if (it.isAnnotationPresent(SerializerType::class.java)) {
                registerTypeHierarchyAdapter(it.getAnnotation(SerializerType::class.java).baseClass.java, it.newInstance())
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