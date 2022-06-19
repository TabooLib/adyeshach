package ink.ptms.adyeshach.impl.description

import ink.ptms.adyeshach.common.entity.EntitySize
import ink.ptms.adyeshach.common.entity.EntityTypes
import ink.ptms.adyeshach.common.entity.path.PathType
import ink.ptms.adyeshach.common.entity.type.minecraftVersion
import ink.ptms.adyeshach.common.util.getEnum
import ink.ptms.adyeshach.common.util.getEnumOrNull
import org.bukkit.entity.EntityType
import taboolib.common.platform.function.info
import java.io.InputStream
import java.nio.charset.StandardCharsets

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.entity.description.EntityTypesDesc
 *
 * @author 坏黑
 * @since 2022/6/19 18:07
 */
class DescEntityTypes(input: InputStream) {

    val types = ArrayList<Entity>()

    init {
        try {
            val lines = input.readBytes().toString(StandardCharsets.UTF_8).lines()
            var i = 0
            while (i < lines.size) {
                // 类型声明
                val line = lines[i++]
                if (line.isNotEmpty() && line[0] != ' ') {
                    val name = lines[i++].trim()
                    val id = lines[i++].trim().toInt()
                    val sizeArgs = lines[i++].trim().split(" ")
                    val size = EntitySize(sizeArgs[0].toDouble(), sizeArgs[1].toDouble())
                    val flying = sizeArgs.getOrNull(2) == "FLYING"
                    val path = when {
                        flying && minecraftVersion >= 11500 -> PathType.FLY
                        size.height <= 1 -> PathType.WALK_1
                        size.height <= 2 -> PathType.WALK_2
                        else -> PathType.WALK_3
                    }
                    val alias = lines[i++].trim()
                    val aliases = if (alias == "~") emptyList() else alias.split("|").map { it.trim() }
                    types += Entity(line, name, id, size, path, aliases)
                }
            }
            info("Load ${types.size} entity types from the \"entity_types.desc\"")
        } catch (ex: Throwable) {
            throw IllegalStateException("Unable to load description file: entity_types.desc", ex)
        }
    }

    data class Entity(val namespace: String, val name: String, val id: Int, val size: EntitySize, val path: PathType, val aliases: List<String>) {

        val bukkitType = EntityType::class.java.getEnumOrNull(name, *aliases.toTypedArray())

        val adyeshachType = EntityTypes::class.java.getEnum(name)
    }
}