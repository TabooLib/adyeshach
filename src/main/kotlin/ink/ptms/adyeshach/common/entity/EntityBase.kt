package ink.ptms.adyeshach.common.entity

import ink.ptms.adyeshach.common.entity.element.EntityPosition
import ink.ptms.adyeshach.common.entity.type.EntityTypes
import ink.ptms.adyeshach.common.util.serializer.Converter
import ink.ptms.adyeshach.common.util.serializer.Serializer
import io.izzel.taboolib.internal.gson.annotations.Expose
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.configuration.ConfigurationSection

/**
 * @Author sky
 * @Since 2020-08-04 12:47
 */
abstract class EntityBase(@Expose val entityType: EntityTypes) : EntityMetaable() {

    @Expose
    var world: String? = null
        protected set

    @Expose
    var position = EntityPosition.empty()
        protected set
        get() = field.clone()

    protected fun initialize(world: String, entityPosition: EntityPosition) {
        this.world = world
        this.position = entityPosition
    }

    fun toJson(): String {
        return Serializer.gson.toJson(this)
    }

    fun toYaml(section: ConfigurationSection) {
        Converter.jsonToYaml(toJson(), section)
    }

    fun getLatestLocation(): Location {
        return Location(Bukkit.getWorld(world ?: "world"), position.x, position.y, position.z, position.yaw, position.pitch)
    }
}