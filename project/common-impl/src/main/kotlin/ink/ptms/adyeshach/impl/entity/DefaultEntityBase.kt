package ink.ptms.adyeshach.impl.entity

import com.eatthepath.uuid.FastUUID
import com.google.gson.annotations.Expose
import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.bukkit.data.EntityPosition
import ink.ptms.adyeshach.core.entity.EntityBase
import ink.ptms.adyeshach.core.entity.EntityTypes
import ink.ptms.adyeshach.core.util.plus
import org.bukkit.Location
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.entity.DefaultEntityBase
 *
 * @author 坏黑
 * @since 2022/6/19 15:59
 */
abstract class DefaultEntityBase(
    @Expose final override val entityType: EntityTypes = EntityTypes.ZOMBIE
) : EntityBase, DefaultMetaable, DefaultTagContainer, DefaultSerializable {

    /** 临时标签 */
    val tag = ConcurrentHashMap<String, Any>()

    /** 持久化标签 */
    @Expose
    val persistentTag = ConcurrentHashMap<String, String>()

    @Expose
    val metadata = ConcurrentHashMap<String, Any>()

    @Expose
    val metadataMask = ConcurrentHashMap<String, MutableMap<String, Boolean>>()

    /**
     * 实体鉴别 ID
     */
    @Expose
    final override var id = UUID.randomUUID().toString().substring(0, 8)

    /**
     * 实体唯一 ID
     */
    @Expose
    final override val uniqueId = UUID.randomUUID().toString()

    /**
     * 将实体唯一 ID 转换为 UUID 类型
     * 历史遗留问题
     */
    final override val normalizeUniqueId: UUID
        get() = if (uniqueId.contains('-')) {
            FastUUID.parseUUID(uniqueId)
        } else {
            val id = uniqueId
            FastUUID.parseUUID("${id.substring(0, 8)}-${id.substring(8, 12)}-${id.substring(12, 16)}-${id.substring(16, 20)}-${id.substring(20)}")
        }

    /**
     * 实体在服务端中的确定位置
     */
    @Expose
    override var position = EntityPosition.empty()
        get() = field.clone()

    /**
     * 是否为测试类型
     */
    override val isTesting = Adyeshach.api().getEntityTypeRegistry().getEntityFlags(entityType).contains("TESTING")

    /**
     * 是否为无效类型
     */
    override val isInvalid = Adyeshach.api().getEntityTypeRegistry().getEntityFlags(entityType).contains("INVALID")
}