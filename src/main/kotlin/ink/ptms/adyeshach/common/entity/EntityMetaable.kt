package ink.ptms.adyeshach.common.entity

import ink.ptms.adyeshach.api.nms.NMS
import ink.ptms.adyeshach.common.bukkit.BukkitParticles
import ink.ptms.adyeshach.common.entity.element.DataWatcher
import io.izzel.taboolib.Version
import io.izzel.taboolib.internal.gson.annotations.Expose
import io.izzel.taboolib.module.nms.impl.Position
import io.izzel.taboolib.util.Strings
import io.izzel.taboolib.util.chat.TextComponent
import org.bukkit.inventory.ItemStack
import org.bukkit.util.EulerAngle

/**
 * @Author sky
 * @Since 2020-08-05 13:07
 */
abstract class EntityMetaable {

    protected val meta = ArrayList<Meta>()
    protected val version = Version.getCurrentVersionInt()

    @Expose
    protected val metadata = HashMap<String, Any>()

    @Expose
    protected val metadataMask = HashMap<String, HashMap<String, Boolean>>()

    @Suppress("UNCHECKED_CAST")
    protected fun <T> at(vararg index: Pair<Int, T>): T {
        return (index.firstOrNull { version >= it.first }?.second ?: -1) as T
    }

    protected fun Int.to(index: Int): Pair<Int, Int> {
        return this to index
    }

    protected fun registerMeta(index: Int, key: String, def: Any) {
        meta.add(MetaNatural(index, key, def))
        metadata[key] = def
    }

    protected fun registerMetaByteMask(index: Int, key: String, mask: Byte, def: Boolean = false) {
        meta.add(MetaMasked(index, key, mask))
        val byteMask = metadataMask.computeIfAbsent(getByteMaskKey(index)) { HashMap() }
        byteMask[key] = def
    }

    protected fun getByteMaskKey(index: Int): String {
        return "\$${Strings.hashKeyForDisk(meta.firstOrNull { it.index == index }!!.key).substring(0, 8)}"
    }

    fun listMetadata(): List<String> {
        return meta.map { it.key }
    }

    fun updateMetadata() {
        if (this is EntityInstance) {
            meta.forEach { it.update(this) }
        }
    }

    fun setMetadata(key: String, value: Any) {
        val registerMeta = meta.firstOrNull { it.key == key } ?: throw RuntimeException("Metadata \"$key\" not registered.")
        if (registerMeta.index == -1) {
            throw RuntimeException("Metadata \"$key\" not supported this minecraft version.")
        }
        if (registerMeta is MetaMasked) {
            metadataMask.computeIfAbsent(getByteMaskKey(registerMeta.index)) { HashMap() }[key] = value as Boolean
        } else {
            metadata[key] = value
        }
        if (this is EntityInstance) {
            registerMeta.update(this)
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> getMetadata(key: String): T {
        val registerMeta = meta.firstOrNull { it.key == key } ?: throw RuntimeException("Metadata \"$key\" not registered.")
        if (registerMeta.index == -1) {
            throw RuntimeException("Metadata \"$key\" not supported this minecraft version.")
        }
        return if (registerMeta is MetaMasked) {
            metadataMask[getByteMaskKey(registerMeta.index)]!![key] as T
        } else {
            metadata[key] as T
        }
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
            if (index == -1) {
                return
            }
            var bits = 0
            val byteMask = entityInstance.metadataMask[entityInstance.getByteMaskKey(index)] ?: return
            entityInstance.meta.filter { it.index == index }.map { it as MetaMasked }.forEach {
                if (byteMask[it.key] == true) {
                    bits += it.mask
                }
            }
            val metadata = dataWatcher?.getMetadata(index, bits.toByte()) ?: return
            entityInstance.forViewers {
                NMS.INSTANCE.updateEntityMetadata(it, entityInstance.index, metadata)
            }
        }
    }

    open class MetaNatural<T>(index: Int, key: String, val def: T) : Meta(index, key) {

        init {
            dataWatcher = when (def) {
                is Int -> DataWatcher.DataInt()
                is Float -> DataWatcher.DataFloat()
                is String -> DataWatcher.DataString()
                is Boolean -> DataWatcher.DataBoolean()
                is Position -> DataWatcher.DataPosition()
                is ItemStack -> DataWatcher.DataItemStack()
                is EulerAngle -> DataWatcher.DataVector()
                is TextComponent -> DataWatcher.DataIChatBaseComponent()
                is BukkitParticles -> DataWatcher.DataParticle()
                else -> null
            }
        }

        override fun update(entityInstance: EntityInstance) {
            if (index == -1) {
                return
            }
            val obj = entityInstance.metadata[key] ?: return
            val metadata = dataWatcher?.getMetadata(index, obj) ?: return
            entityInstance.forViewers {
                NMS.INSTANCE.updateEntityMetadata(it, entityInstance.index, metadata)
            }
        }
    }
}