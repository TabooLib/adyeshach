package ink.ptms.adyeshach.impl.description

import ink.ptms.adyeshach.core.entity.EntitySize
import ink.ptms.adyeshach.core.entity.EntityTypes
import ink.ptms.adyeshach.core.entity.path.PathType
import ink.ptms.adyeshach.core.util.getEnum
import ink.ptms.adyeshach.core.util.getEnumOrNull
import org.bukkit.entity.EntityType

/**
 * @author 坏黑
 * @since 2022/6/19 18:07
 */
data class Entity(
    val namespace: String,
    val name: String,
    val id: Int,
    val size: EntitySize,
    val path: PathType,
    val aliases: List<String>,
    val instance: String,
    val instanceWithInterface: Boolean,
    val updateInterval: Int,
    val flags: List<String>
) {

    val bukkitType = EntityType::class.java.getEnumOrNull(name, *aliases.toTypedArray())

    val adyeshachType = EntityTypes::class.java.getEnum(name)

    val adyeshachInterface: Class<*> = Class.forName(namespace)

    val instanceClass: Class<*> = Class.forName(instance)
}