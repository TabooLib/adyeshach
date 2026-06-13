package ink.ptms.adyeshach.impl.description

import ink.ptms.adyeshach.core.entity.EntitySize
import ink.ptms.adyeshach.core.entity.EntityTypes
import ink.ptms.adyeshach.core.entity.path.PathType
import ink.ptms.adyeshach.core.entity.type.minecraftVersion
import ink.ptms.adyeshach.core.util.getEnumOrNull
import taboolib.common.platform.function.info
import java.io.InputStream

/**
 * 解析 entity_types.desc，并构建 EntityTypes 到 Entity 的查询表
 * 主记录名与别名行中可解析为 EntityTypes 的字符串都会映射到同一条 Entity，代理类仍只按主记录生成一份
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
        info("Loaded ${types.size} entity type(s) from the \"$name\"")
    }

    /**
     * 将每条 desc 主记录及其别名行里能识别的 EntityTypes 枚举项都指向同一 Entity
     * 后写入的键覆盖先前的，便于同文件内显式修正
     *
     * @return EntityTypes 查询表
     */
    fun entityTypeMap(): Map<EntityTypes, Entity> {
        val map = HashMap<EntityTypes, Entity>()
        types.forEach { entity ->
            map[entity.adyeshachType] = entity
            entity.aliases.forEach { alias ->
                EntityTypes::class.java.getEnumOrNull(alias)?.let { map[it] = entity }
            }
        }
        return map
    }
}