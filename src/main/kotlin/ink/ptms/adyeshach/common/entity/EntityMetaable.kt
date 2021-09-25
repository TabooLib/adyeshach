package ink.ptms.adyeshach.common.entity

import com.google.gson.annotations.Expose
import ink.ptms.adyeshach.api.event.AdyeshachMaskedMetaGenerateEvent
import ink.ptms.adyeshach.api.event.AdyeshachMetaUpdateEvent
import ink.ptms.adyeshach.api.event.AdyeshachNaturalMetaGenerateEvent
import ink.ptms.adyeshach.api.event.AdyeshachTagUpdateEvent
import ink.ptms.adyeshach.api.nms.NMS
import ink.ptms.adyeshach.common.bukkit.BukkitParticles
import ink.ptms.adyeshach.common.bukkit.BukkitPose
import ink.ptms.adyeshach.common.bukkit.data.DataWatcher
import ink.ptms.adyeshach.common.bukkit.data.VillagerData
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.material.MaterialData
import org.bukkit.util.EulerAngle
import org.bukkit.util.Vector
import taboolib.common.io.digest
import taboolib.module.nms.MinecraftVersion
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.reflect.KClass

/**
 * @Author sky
 * @Since 2020-08-05 13:07
 */
abstract class EntityMetaable {

    protected val meta = CopyOnWriteArrayList<Meta>()
    protected val version = MinecraftVersion.majorLegacy

    protected val tag = ConcurrentHashMap<String, String>()

    @Expose
    protected val metadata = ConcurrentHashMap<String, Any>()

    @Expose
    protected val metadataMask = ConcurrentHashMap<String, HashMap<String, Boolean>>()

    @Suppress("UNCHECKED_CAST")
    protected fun <T> at(vararg index: Pair<Int, T>): T {
        return (index.firstOrNull { version >= it.first }?.second ?: -1) as T
    }

    protected fun getByteMaskKey(index: Int): String {
        return "\$${meta.firstOrNull { it.index == index }!!.key.digest("md5").substring(0, 8)}"
    }

    fun registerEditor(id: String): MetaEditor {
        return MetaEditor(MetaNatural(-2, id, 0).run {
            meta.add(this)
            this
        }, true).build()
    }

    fun registerMeta(index: Int, key: String, def: Any): MetaEditor {
        return MetaEditor(MetaNatural(index, key, def).run {
            meta.add(this)
            metadata[key] = def
            this
        })
    }

    fun registerMetaByteMask(index: Int, key: String, mask: Byte, def: Boolean = false): MetaEditor {
        return MetaEditor(MetaMasked(index, key, mask, def).run {
            meta.add(this)
            metadataMask.computeIfAbsent(getByteMaskKey(index)) { HashMap() }[key] = def
            this
        })
    }

    fun listMetadata(): List<Meta> {
        return meta.filter { it.index != -1 }.toList()
    }

    fun getMetadata(player: Player): Array<Any> {
        if (this is EntityInstance) {
            return meta.mapNotNull { it.getMetadata(player, this) }.toTypedArray()
        }
        return arrayOf()
    }

    fun setMetadata(key: String, value: Any) {
        val registerMeta = meta.firstOrNull { it.key == key } ?: error("Metadata \"$key\" not registered.")
        if (registerMeta.index == -1) {
            error("Metadata \"$key\" not supported this minecraft version.")
        }
        if (registerMeta.index == -2) {
            error("Metadata \"$key\" not allowed.")
        }
        val event = AdyeshachMetaUpdateEvent(this, registerMeta, key, value)
        if (event.call()) {
            if (registerMeta is MetaMasked) {
                metadataMask.computeIfAbsent(getByteMaskKey(registerMeta.index)) { HashMap() }[key] = event.value as Boolean
            } else {
                metadata[key] = event.value
            }
            if (this is EntityInstance) {
                registerMeta.update(this)
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> getMetadata(key: String): T {
        val registerMeta = meta.firstOrNull { it.key == key } ?: error("Metadata \"$key\" not registered.")
        if (registerMeta.index == -1) {
            error("Metadata \"$key\" not supported this minecraft version.")
        }
        return if (registerMeta is MetaMasked) {
            (metadataMask[getByteMaskKey(registerMeta.index)]?.get(key) ?: registerMeta.def) as T
        } else {
            registerMeta.dataWatcher!!.parse(metadata[key] ?: registerMeta.def) as T
        }
    }

    fun updateMetadata() {
        if (this is EntityInstance) {
            forViewers {
                updateMetadata(it)
            }
        }
    }

    fun updateMetadata(viewer: Player) {
        if (this is EntityInstance) {
            val metadata = getMetadata(viewer)
            if (metadata.isNotEmpty()) {
                NMS.INSTANCE.updateEntityMetadata(viewer, this.index, *metadata)
            }
        }
    }

    fun getTags(): Set<Map.Entry<String, String>> {
        return tag.entries
    }

    fun getTag(key: String): String? {
        return tag[key]
    }

    fun hasTag(key: String): Boolean {
        return tag.containsKey(key)
    }

    fun setTag(key: String, value: String) {
        val event = AdyeshachTagUpdateEvent(this, key, value)
        if (event.call()) {
            if (event.value != null) {
                tag[key] = event.value!!
            } else {
                tag.remove(key)
            }
        }
    }

    fun removeTag(key: String) {
        val event = AdyeshachTagUpdateEvent(this, key, null)
        if (event.call()) {
            if (event.value != null) {
                tag[key] = event.value!!
            } else {
                tag.remove(key)
            }
        }
    }

    abstract class Meta(val index: Int, val key: String, val def: Any) {

        var editor: MetaEditor? = null
        var dataWatcher: DataWatcher? = null
            protected set

        abstract fun getMetadata(player: Player, entityInstance: EntityInstance): Any?

        fun update(entityInstance: EntityInstance) {
            entityInstance.forViewers {
                NMS.INSTANCE.updateEntityMetadata(it, entityInstance.index, getMetadata(it, entityInstance) ?: return@forViewers)
            }
        }

        override fun toString(): String {
            return "Meta(index=$index, key='$key', def=$def, editor=$editor, dataWatcher=$dataWatcher)"
        }
    }

    open class MetaMasked(index: Int, key: String, val mask: Byte, def: Boolean) : Meta(index, key, def) {

        init {
            dataWatcher = DataWatcher.DataByte()
        }

        override fun getMetadata(player: Player, entityInstance: EntityInstance): Any? {
            if (index == -1) {
                return null
            }
            val event = AdyeshachMaskedMetaGenerateEvent(entityInstance, player, this, HashMap())
            var bits = 0
            val byteMask = entityInstance.metadataMask[entityInstance.getByteMaskKey(index)] ?: return null
            entityInstance.meta.filter { it.index == index && it is MetaMasked }.forEach {
                event.byteMask[it as MetaMasked] = byteMask[it.key] == true
            }
            event.call()
            event.byteMask.filter { it.value }.forEach { (k, _) ->
                bits += k.mask
            }
            return dataWatcher?.getMetadata(index, bits.toByte())
        }

        override fun toString(): String {
            return "MetaMasked(mask=$mask) ${super.toString()}"
        }
    }

    open class MetaNatural<T>(index: Int, key: String, def: T) : Meta(index, key, def!!) {

        init {
            dataWatcher = when (def) {
                is Int -> DataWatcher.DataInt()
                is Byte -> DataWatcher.DataByte()
                is Float -> DataWatcher.DataFloat()
                is String -> DataWatcher.DataString()
                is Boolean -> DataWatcher.DataBoolean()
                is Vector -> DataWatcher.DataPosition()
                is ItemStack -> DataWatcher.DataItemStack()
                is EulerAngle -> DataWatcher.DataVector()
                is MaterialData -> DataWatcher.DataBlockData()
                is VillagerData -> DataWatcher.DataVillagerData()
                is TextComponent -> DataWatcher.DataIChatBaseComponent()
                is BukkitParticles -> DataWatcher.DataParticle()
                is BukkitPose -> DataWatcher.DataPose()
                else -> null
            }
        }

        override fun getMetadata(player: Player, entityInstance: EntityInstance): Any? {
            if (index == -1) {
                return null
            }
            val obj = entityInstance.metadata[key] ?: return null
            val event = AdyeshachNaturalMetaGenerateEvent(entityInstance, player, this, obj)
            event.call()
            return dataWatcher?.getMetadata(index, event.value)
        }

        override fun toString(): String {
            return "MetaNatural() ${super.toString()}"
        }
    }

    class MetaEditor(val meta: Meta? = null, val custom: Boolean = false) {

        var edit = true
        var enum: KClass<*>? = null
        var onReset: ((EntityInstance, Meta) -> (Unit))? = null
        var onModify: ((Player, EntityInstance, Meta) -> (Unit))? = null
        var onDisplay: ((Player, EntityInstance, Meta) -> (String))? = null

        fun toEnum(enum: KClass<*>): MetaEditor {
            this.enum = enum
            return this
        }

        fun canEdit(edit: Boolean): MetaEditor {
            this.edit = edit
            return this
        }

        fun reset(onReset: ((EntityInstance, Meta) -> (Unit))): MetaEditor {
            this.onReset = onReset
            return this
        }

        fun modify(onModify: ((Player, EntityInstance, Meta) -> (Unit))): MetaEditor {
            this.onModify = onModify
            return this
        }

        fun display(onDisplay: (Player, EntityInstance, Meta) -> (String)): MetaEditor {
            this.onDisplay = onDisplay
            return this
        }

        fun from(editor: MetaEditor): MetaEditor {
            enum = editor.enum
            onReset = editor.onReset
            onModify = editor.onModify
            onDisplay = editor.onDisplay
            return this
        }

        fun build(): MetaEditor {
            meta?.editor = this
            return this
        }
    }
}