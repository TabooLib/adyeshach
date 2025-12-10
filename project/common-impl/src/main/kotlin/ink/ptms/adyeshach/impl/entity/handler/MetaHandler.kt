package ink.ptms.adyeshach.impl.entity.handler

import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.MinecraftMeta
import ink.ptms.adyeshach.core.entity.Meta
import ink.ptms.adyeshach.core.entity.MetaMasked
import ink.ptms.adyeshach.core.entity.manager.event.MetaUpdateEvent
import ink.ptms.adyeshach.core.util.errorBy
import ink.ptms.adyeshach.impl.DefaultAdyeshachAPI
import ink.ptms.adyeshach.impl.DefaultAdyeshachEntityMetadataRegistry.Companion.metaKeyLookup
import ink.ptms.adyeshach.impl.DefaultAdyeshachEntityMetadataRegistry.Companion.metaTypeLookup
import ink.ptms.adyeshach.impl.DefaultAdyeshachEntityMetadataRegistry.Companion.registeredEntityMeta
import ink.ptms.adyeshach.impl.entity.DefaultEntityInstance
import org.bukkit.entity.Player
import taboolib.common.io.digest
import java.util.concurrent.ConcurrentHashMap

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.entity.handler.MetaHandler
 *
 * 负责实体元数据的管理
 *
 * @author 坏黑
 * @since 2022/6/19
 */
@Suppress("UNCHECKED_CAST")
class MetaHandler(private val self: DefaultEntityInstance) {

    /**
     * 获取实体元数据
     */
    fun <T> getMetadata(key: String): T {
        val meta = getAvailableEntityMeta().firstOrNull { it.key == key } ?: errorBy("error-meta-not-found", key)
        if (meta.index == -1) {
            errorBy("error-meta-not-supported", key)
        }
        return if (meta is MetaMasked) {
            self.metadataMask[getByteMaskKey(meta.index)]?.get(key) ?: meta.def
        } else {
            meta.getMetadataParser().parse(self.metadata[key] ?: meta.def)
        } as T
    }

    /**
     * 设置实体元数据
     */
    fun setMetadata(key: String, value: Any): Boolean {
        val meta = getAvailableEntityMeta().firstOrNull { it.key == key } ?: errorBy("error-meta-not-found", key)
        if (meta.index == -1) {
            errorBy("error-meta-not-supported", key)
        }
        if (meta.index == -2) {
            errorBy("error-meta-not-allow", key)
        }
        val eventBus = DefaultAdyeshachAPI.localEventBus
        val event = MetaUpdateEvent(self, meta, key, if (meta is MetaMasked) value else meta.getMetadataParser().parse(value))
        if (eventBus.callMetaUpdate(event)) {
            if (meta is MetaMasked) {
                self.metadataMask.computeIfAbsent(getByteMaskKey(meta.index)) { ConcurrentHashMap() }[key] = event.value as Boolean
            } else {
                self.metadata[key] = meta.getMetadataParser().parse(event.value)
            }
            eventBus.postMetaUpdate(event)
            meta.updateEntityMetadata(self)
            return true
        }
        return false
    }

    /**
     * 获取实体所有元数据模型
     */
    fun getAvailableEntityMeta(): List<Meta<*>> {
        return metaTypeLookup.computeIfAbsent(self.javaClass) { 
            registeredEntityMeta.filterKeys { it.isAssignableFrom(self.javaClass) }.values.flatten() 
        }
    }

    /**
     * 更新实体元数据
     */
    fun updateEntityMetadata() {
        self.forViewers { updateEntityMetadata(it) }
    }

    /**
     * 向给定玩家更新实体元数据
     */
    fun updateEntityMetadata(viewer: Player) {
        val metadata = generateEntityMetadata(viewer)
        if (metadata.isNotEmpty()) {
            Adyeshach.api().getMinecraftAPI().getEntityOperator().updateEntityMetadata(viewer, self.index, metadata.toList())
        }
    }

    /**
     * 基于给定玩家生成实体元数据
     */
    fun generateEntityMetadata(player: Player): Array<MinecraftMeta> {
        return getAvailableEntityMeta().mapNotNull { it.generateMetadata(player, self) }.toTypedArray()
    }

    /**
     * 获取字节掩码键
     */
    fun getByteMaskKey(index: Int): String {
        return metaKeyLookup.computeIfAbsent(self.javaClass) { 
            "\$${getAvailableEntityMeta().first { it.index == index }.key.digest("md5").substring(0, 8)}" 
        }
    }
}
