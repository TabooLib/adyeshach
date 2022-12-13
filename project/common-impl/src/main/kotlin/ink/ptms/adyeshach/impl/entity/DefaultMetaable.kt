package ink.ptms.adyeshach.impl.entity

import ink.ptms.adyeshach.api.event.AdyeshachMetaUpdateEvent
import ink.ptms.adyeshach.common.api.Adyeshach
import ink.ptms.adyeshach.common.api.MinecraftMeta
import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.Meta
import ink.ptms.adyeshach.common.entity.MetaMasked
import ink.ptms.adyeshach.common.entity.Metaable
import ink.ptms.adyeshach.common.util.errorBy
import ink.ptms.adyeshach.impl.DefaultAdyeshachEntityMetadataHandler.Companion.metaKeyLookup
import ink.ptms.adyeshach.impl.DefaultAdyeshachEntityMetadataHandler.Companion.metaTypeLookup
import ink.ptms.adyeshach.impl.DefaultAdyeshachEntityMetadataHandler.Companion.registeredEntityMeta
import org.bukkit.entity.Player
import taboolib.common.io.digest
import java.util.concurrent.ConcurrentHashMap

/**
 * Adyeshach
 * ink.ptms.adyeshach.internal.entity.DefaultMetaable
 *
 * @author 坏黑
 * @since 2022/6/19 16:11
 */
@Suppress("SpellCheckingInspection", "UNCHECKED_CAST")
interface DefaultMetaable : Metaable {

    override fun <T> getMetadata(key: String): T {
        val meta = getAvailableEntityMeta().firstOrNull { it.key == key } ?: errorBy("error-meta-not-found", key)
        if (meta.index == -1) {
            errorBy("error-meta-not-supported", key)
        }
        this as DefaultEntityBase
        return if (meta is MetaMasked) {
            metadataMask[getByteMaskKey(meta.index)]?.get(key) ?: meta.def
        } else {
            meta.getMetadataParser().parse(metadata[key] ?: meta.def)
        } as T
    }

    override fun setMetadata(key: String, value: Any): Boolean {
        val meta = getAvailableEntityMeta().firstOrNull { it.key == key } ?: errorBy("error-meta-not-found", key)
        if (meta.index == -1) {
            errorBy("error-meta-not-supported", key)
        }
        if (meta.index == -2) {
            errorBy("error-meta-not-allow", key)
        }
        this as DefaultEntityInstance
        val event = AdyeshachMetaUpdateEvent(this, meta, key, value)
        if (event.call()) {
            if (meta is MetaMasked) {
                metadataMask.computeIfAbsent(getByteMaskKey(meta.index)) { ConcurrentHashMap() }[key] = event.value as Boolean
            } else {
                metadata[key] = event.value
            }
            meta.updateEntityMetadata(this)
            return true
        }
        return false
    }

    override fun getAvailableEntityMeta(): List<Meta<*>> {
        return metaTypeLookup.computeIfAbsent(javaClass) { registeredEntityMeta.filterKeys { it.isAssignableFrom(javaClass) }.values.flatten() }
    }

    override fun updateEntityMetadata() {
        this as EntityInstance
        forViewers { updateEntityMetadata(it) }
    }

    override fun updateEntityMetadata(viewer: Player) {
        this as EntityInstance
        val metadata = generateEntityMetadata(viewer)
        if (metadata.isNotEmpty()) {
            Adyeshach.api().getMinecraftAPI().getEntityOperator().updateEntityMetadata(viewer, index, *metadata)
        }
    }

    override fun generateEntityMetadata(player: Player): Array<MinecraftMeta> {
        this as EntityInstance
        return getAvailableEntityMeta().mapNotNull { it.generateMetadata(player, this) }.toTypedArray()
    }

    fun getByteMaskKey(index: Int): String {
        return metaKeyLookup.computeIfAbsent(javaClass) { "\$${getAvailableEntityMeta().first { it.index == index }.key.digest("md5").substring(0, 8)}" }
    }
}