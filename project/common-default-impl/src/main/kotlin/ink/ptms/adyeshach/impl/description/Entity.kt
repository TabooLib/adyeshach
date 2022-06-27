package ink.ptms.adyeshach.impl.description

import ink.ptms.adyeshach.common.entity.EntitySize
import ink.ptms.adyeshach.common.entity.EntityTypes
import ink.ptms.adyeshach.common.entity.path.PathType
import ink.ptms.adyeshach.common.util.getEnum
import ink.ptms.adyeshach.common.util.getEnumOrNull
import org.bukkit.entity.EntityType

/**
 * @author 坏黑
 * @since 2022/6/19 18:07
 */
data class Entity(val namespace: String, val name: String, val id: Int, val size: EntitySize, val path: PathType, val aliases: List<String>) {

    val bukkitType = EntityType::class.java.getEnumOrNull(name, *aliases.toTypedArray())

    val adyeshachType = EntityTypes::class.java.getEnum(name)

    val adyeshachInterface: Class<*> = Class.forName(namespace)
}