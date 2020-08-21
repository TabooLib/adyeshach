package ink.ptms.adyeshach.common.entity

import ink.ptms.adyeshach.common.entity.element.EntityPosition
import ink.ptms.adyeshach.common.util.serializer.Converter
import ink.ptms.adyeshach.common.util.serializer.Serializer
import io.izzel.taboolib.internal.gson.annotations.Expose
import org.bukkit.configuration.ConfigurationSection
import java.util.*

/**
 * @Author sky
 * @Since 2020-08-04 12:47
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

    fun toJson(): String {
        return Serializer.gson.toJson(this)
    }

    fun toYaml(section: ConfigurationSection) {
        Converter.jsonToYaml(toJson(), section)
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