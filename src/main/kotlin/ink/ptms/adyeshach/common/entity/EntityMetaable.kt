package ink.ptms.adyeshach.common.entity

import ink.ptms.adyeshach.api.nms.NMS
import ink.ptms.adyeshach.common.bukkit.BukkitParticles
import ink.ptms.adyeshach.common.entity.element.DataWatcher
import ink.ptms.adyeshach.common.entity.element.VillagerData
import io.izzel.taboolib.Version
import io.izzel.taboolib.internal.gson.annotations.Expose
import io.izzel.taboolib.module.nms.impl.Position
import io.izzel.taboolib.util.Strings
import io.izzel.taboolib.util.chat.TextComponent
import org.bukkit.entity.Player
import org.bukkit.entity.Pose
import org.bukkit.inventory.ItemStack
import org.bukkit.material.MaterialData
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

    protected fun getByteMaskKey(index: Int): String {
        return "\$${Strings.hashKeyForDisk(meta.firstOrNull { it.index == index }!!.key).substring(0, 8)}"
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
        if (registerMeta.index == -2) {
            throw RuntimeException("Metadata \"$key\" not allowed.")
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
            registerMeta.dataWatcher!!.parse(metadata[key]!!) as T
        }
    }

    abstract class Meta(val index: Int, val key: String, val def: Any) {

        var editor: MetaEditor? = null
        var dataWatcher: DataWatcher? = null
            protected set

        abstract fun update(entityInstance: EntityInstance)
    }

    open class MetaMasked(index: Int, key: String, val mask: Byte, def: Boolean) : Meta(index, key, def) {

        init {
            dataWatcher = DataWatcher.DataByte()
        }

        override fun update(entityInstance: EntityInstance) {
            if (index == -1) {
                return
            }
            var bits = 0
            val byteMask = entityInstance.metadataMask[entityInstance.getByteMaskKey(index)] ?: return
            entityInstance.meta.filter { it.index == index && it is MetaMasked }.forEach {
                if (byteMask[it.key] == true) {
                    bits += (it as MetaMasked).mask
                }
            }
            val metadata = dataWatcher?.getMetadata(index, bits.toByte()) ?: return
            entityInstance.forViewers {
                NMS.INSTANCE.updateEntityMetadata(it, entityInstance.index, metadata)
            }
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
                is Position -> DataWatcher.DataPosition()
                is ItemStack -> DataWatcher.DataItemStack()
                is EulerAngle -> DataWatcher.DataVector()
                is MaterialData -> DataWatcher.DataBlockData()
                is VillagerData -> DataWatcher.DataVillagerData()
                is TextComponent -> DataWatcher.DataIChatBaseComponent()
                is BukkitParticles -> DataWatcher.DataParticle()
                is Pose -> DataWatcher.DataPose()
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

    class MetaEditor(val meta: Meta? = null, val custom: Boolean = false) {

        var edit = true
        var onReset: ((Player, EntityInstance, Meta) -> (Unit))? = null
        var onModify: ((Player, EntityInstance, Meta) -> (Unit))? = null
        var onDisplay: ((Player, EntityInstance, Meta) -> (String))? = null

        fun canEdit(edit: Boolean): MetaEditor {
            this.edit = edit
            return this
        }

        fun reset(onReset: ((Player, EntityInstance, Meta) -> (Unit))): MetaEditor {
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