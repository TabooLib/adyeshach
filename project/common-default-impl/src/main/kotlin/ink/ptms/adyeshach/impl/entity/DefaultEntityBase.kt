package ink.ptms.adyeshach.impl.entity

import com.google.gson.annotations.Expose
import ink.ptms.adyeshach.common.bukkit.data.EntityPosition
import ink.ptms.adyeshach.common.entity.EntityBase
import ink.ptms.adyeshach.common.entity.EntityTypes
import org.bukkit.Location
import org.bukkit.World
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * Adyeshach
 * ink.ptms.adyeshach.internal.entity.DefaultEntityBase
 *
 * @author 坏黑
 * @since 2022/6/19 15:59
 */
abstract class DefaultEntityBase(@Expose override val entityType: EntityTypes) : EntityBase, DefaultMetaable, DefaultTagContainer, DefaultSerializable {

    val tag = ConcurrentHashMap<String, String>()

    @Expose
    val persistentTag = ConcurrentHashMap<String, String>()

    @Expose
    val metadata = ConcurrentHashMap<String, Any>()

    @Expose
    val metadataMask = ConcurrentHashMap<String, MutableMap<String, Boolean>>()

    @Expose
    override var id = UUID.randomUUID().toString().replace("-", "").substring(0, 8)

    @Expose
    override val uniqueId = UUID.randomUUID().toString().replace("-", "")

    @Expose
    override var position = EntityPosition.empty()
        get() = field.clone()

    override var testing = false

    override var invalid = false

    override val x: Double
        get() = position.x

    override val y: Double
        get() = position.y

    override val z: Double
        get() = position.z

    override val yaw: Float
        get() = position.yaw

    override val pitch: Float
        get() = position.pitch

    override val normalizeUniqueId: UUID
        get() {
            val id = uniqueId
            return UUID.fromString("${id.substring(0, 8)}-${id.substring(8, 12)}-${id.substring(12, 16)}-${id.substring(16, 20)}-${id.substring(20)}")
        }

    override fun getWorld(): World {
        return position.world
    }

    override fun getLocation(): Location {
        return position.toLocation()
    }
}