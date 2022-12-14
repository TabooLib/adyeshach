package ink.ptms.adyeshach.impl.description

import ink.ptms.adyeshach.common.entity.EntitySize
import ink.ptms.adyeshach.common.entity.path.PathType
import ink.ptms.adyeshach.common.entity.type.minecraftVersion
import taboolib.common.platform.function.info
import java.io.InputStream

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.entity.description.EntityTypesDesc
 *
 * @author 坏黑
 * @since 2022/6/19 18:07
 */
class DescEntityTypes(input: InputStream) : Description(input) {

    val types = ArrayList<Entity>()

    override val name: String = "entity_types.desc"

    override fun load(part: DescriptionBlock) {
        val namespace = part.next()
        val name = part.next().trim()
        val id = part.next().trim().toInt()
        val sizeArgs = part.next().trim().split(" ")
        val size = EntitySize(sizeArgs[0].toDouble(), sizeArgs[1].toDouble())
        val flying = sizeArgs.getOrNull(2) == "FLYING"
        val path = when {
            flying && minecraftVersion >= 11500 -> PathType.FLY
            size.height <= 1 -> PathType.WALK_1
            size.height <= 2 -> PathType.WALK_2
            else -> PathType.WALK_3
        }
        // 别名
        val alias = part.next().trim()
        val aliases = if (alias == "~") {
            emptyList()
        } else {
            alias.split("|").map { it.trim() }
        }
        // 实例类名称
        var instance = part.next().trim()
        var instanceWithInterface = false
        if (instance.endsWith('@')) {
            instance = instance.substring(0, instance.length - 1)
            instanceWithInterface = true
        }
        // 客户端更新间隔
        val updateInterval = part.next().trim().toInt()
        // 标签
        val flags = if (part.hasNext()){
            part.next().trim().split(" ")
        } else {
            emptyList()
        }
        types += Entity(namespace, name, id, size, path, aliases, instance, instanceWithInterface, updateInterval, flags)
    }

    override fun loaded() {
        info("Load ${types.size} entity types from the \"$name\"")
    }
}