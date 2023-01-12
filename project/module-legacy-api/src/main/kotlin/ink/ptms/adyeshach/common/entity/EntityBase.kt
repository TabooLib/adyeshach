package ink.ptms.adyeshach.common.entity

import ink.ptms.adyeshach.common.bukkit.data.EntityPosition
import ink.ptms.adyeshach.common.util.serializer.Serializer
import ink.ptms.adyeshach.core.entity.EntityInstance
import org.bukkit.Location
import org.bukkit.World
import taboolib.library.configuration.ConfigurationSection
import java.util.*
import java.util.function.Function

/**
 * @author sky
 * @since 2020-08-04 12:47
 */
@Deprecated("Outdated but usable")
abstract class EntityBase(val entityType: EntityTypes, val v2: EntityInstance) : EntityMetaable() {

    var id: String
        get() = v2.id
        set(value) {
            v2.id = value
        }

    val uniqueId: String
        get() = v2.uniqueId

    val normalizeUniqueId: UUID
        get() = v2.normalizeUniqueId

    var position: EntityPosition
        get() = EntityPosition.fromLocation(v2.position.toLocation())
        set(value) = run { v2.position = value.v2() }

    var testing = false

    var invalid = false

    val x: Double
        get() = position.x

    val y: Double
        get() = position.y

    val z: Double
        get() = position.z

    val yaw: Float
        get() = position.yaw

    val pitch: Float
        get() = position.pitch

    fun getWorld(): World {
        return position.world
    }

    fun getLocation(): Location {
        return position.toLocation()
    }

    fun toJson(): String {
        return Serializer.gson.toJson(this)
    }

    fun toYaml(section: ConfigurationSection, transfer: Function<String, String> = Function { it }) {
        return v2.toSection(section, transfer)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is EntityBase) return false
        if (uniqueId != other.uniqueId) return false
        return true
    }

    override fun hashCode(): Int {
        return uniqueId.hashCode()
    }
}