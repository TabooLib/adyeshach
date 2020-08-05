package ink.ptms.adyeshach.common.entity

import ink.ptms.adyeshach.api.nms.NMS
import ink.ptms.adyeshach.common.bukkit.BukkitParticles
import ink.ptms.adyeshach.common.entity.element.DataWatcher
import io.izzel.taboolib.internal.gson.annotations.Expose
import io.izzel.taboolib.util.chat.TextComponent
import org.bukkit.util.EulerAngle
import java.lang.RuntimeException

/**
 * @Author sky
 * @Since 2020-08-05 13:07
 */
abstract class EntityMetaable {

    val meta = ArrayList<Meta>()
        get() = ArrayList(field)

    @Expose
    val properties = HashMap<Int, Any>()
        get() = HashMap(field)

    protected fun registerMeta(index: Int, key: String, def: Any) {
        meta.add(MetaNatural(index, key, def))
        properties[index] = def
    }

    protected fun registerMetaByteMask(index: Int, key: String, mask: Byte, def: Boolean = false) {
        meta.add(MetaMasked(index, key, mask))
        val byteMask = properties.computeIfAbsent(index) { ByteMask() } as ByteMask
        byteMask.mask[key] = def
    }

    protected fun updateMetadata() {
        if (this is EntityInstance) {
            meta.forEach {
                it.update(this)
            }
        }
    }

    fun setMetadata(key: String, value: Any) {
        val registerMeta = meta.firstOrNull { it.key == key } ?: throw RuntimeException("Metadata \"$key\" not registered.")
        when (registerMeta) {
            is MetaMasked -> {
                (properties.computeIfAbsent(registerMeta.index) { ByteMask() } as ByteMask).mask[key] = value as Boolean
            }
            is MetaNatural<*> -> {
                properties[registerMeta.index] = value
            }
        }
        if (this is EntityInstance) {
            registerMeta.update(this)
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> getMetadata(key: String): T {
        val registerMeta = meta.firstOrNull { it.key == key } ?: throw RuntimeException("Metadata \"$key\" not registered.")
        val obj = properties[registerMeta.index]
        return if (obj is ByteMask) {
            obj.mask[key]!! as T
        } else {
            obj as T
        }
    }

    class ByteMask {

        @Expose
        val mask = HashMap<String, Boolean>()
    }

    abstract class Meta(val index: Int, val key: String) {

        var dataWatcher: DataWatcher? = null
            protected set

        abstract fun update(entityInstance: EntityInstance)
    }

    open class MetaMasked(index: Int, key: String, val mask: Byte) : Meta(index, key) {

        init {
            dataWatcher = DataWatcher.DataByte()
        }

        override fun update(entityInstance: EntityInstance) {
            var bits = 0
            val byteMask = (entityInstance.properties[index] ?: return) as ByteMask
            entityInstance.meta.filter { it.index == index }.map { it as MetaMasked }.forEach {
                if (byteMask.mask[it.key] == true) {
                    bits += it.mask
                }
            }
            val metadata = dataWatcher?.getMetadata(index, bits) ?: return
            NMS.INSTANCE.updateEntityMetadata(entityInstance.owner, entityInstance.index, metadata)
        }
    }

    open class MetaNatural<T>(index: Int, key: String, val def: T) : Meta(index, key) {

        init {
            dataWatcher = when (def) {
                is Int -> DataWatcher.DataInt()
                is Float -> DataWatcher.DataFloat()
                is String -> DataWatcher.DataString()
                is Boolean -> DataWatcher.DataBoolean()
                is BukkitParticles -> DataWatcher.DataParticle()
                is EulerAngle -> DataWatcher.DataVector()
                is TextComponent -> DataWatcher.DataIChatBaseComponent()
                else -> null
            }
        }

        override fun update(entityInstance: EntityInstance) {
            val obj = entityInstance.properties[index] ?: return
            val metadata = dataWatcher?.getMetadata(index, obj) ?: return
            NMS.INSTANCE.updateEntityMetadata(entityInstance.owner, entityInstance.index, metadata)
        }
    }
}