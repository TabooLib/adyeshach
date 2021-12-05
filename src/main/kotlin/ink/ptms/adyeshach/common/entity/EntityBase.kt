package ink.ptms.adyeshach.common.entity

import com.google.gson.annotations.Expose
import ink.ptms.adyeshach.common.bukkit.data.EntityPosition
import ink.ptms.adyeshach.common.util.serializer.Converter
import ink.ptms.adyeshach.common.util.serializer.Serializer
import org.bukkit.Location
import org.bukkit.World
import taboolib.library.configuration.ConfigurationSection
import java.util.*
import java.util.function.Function

/**
 * @author sky
 * @since 2020-08-04 12:47
 */
abstract class EntityBase(@Expose val entityType: EntityTypes) : EntityMetaable() {

    /**
     * 用于指令，且可重命名的序列号
     */
    @Expose
    var id = UUID.randomUUID().toString().replace("-", "").substring(0, 8)

    /**
     * 用作储存，且不可重复的序列号
     */
    @Expose
    val uniqueId = UUID.randomUUID().toString().replace("-", "")

    @Expose
    var position = EntityPosition.empty()
        protected set
        get() = field.clone()

    var testing = false

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
        Converter.jsonToYaml(toJson(), section, transfer)
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