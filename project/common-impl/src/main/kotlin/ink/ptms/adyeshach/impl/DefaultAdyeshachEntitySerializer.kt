package ink.ptms.adyeshach.impl

import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.AdyeshachEntitySerializer
import ink.ptms.adyeshach.core.entity.EntityInstance
import ink.ptms.adyeshach.core.entity.EntityTypes
import ink.ptms.adyeshach.core.serializer.Serializer
import ink.ptms.adyeshach.core.util.getEnum
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.PlatformFactory
import taboolib.library.configuration.ConfigurationSection
import taboolib.module.configuration.Configuration
import taboolib.module.configuration.Type
import java.util.function.Function

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.DefaultAdyeshachEntitySerializer
 *
 * @author 坏黑
 * @since 2022/6/19 15:44
 */
@Suppress("DuplicatedCode")
class DefaultAdyeshachEntitySerializer : AdyeshachEntitySerializer {

    override fun toJson(entity: EntityInstance): String {
        return Serializer.gson.toJson(entity)
    }

    override fun toYaml(entity: EntityInstance, transfer: Function<String, String>): ConfigurationSection {
        val conf = Configuration.loadFromString(Serializer.gson.toJson(entity), Type.JSON)
        conf.getValues(true).forEach { (k, v) ->
            if (v !is ConfigurationSection) {
                conf[transfer.apply(k)] = v
            }
        }
        conf.changeType(Type.YAML)
        return conf
    }

    override fun toSection(entity: EntityInstance, section: ConfigurationSection, transfer: Function<String, String>) {
        return toYaml(entity, transfer).getValues(false).forEach { (k, v) -> section[k] = v }
    }

    override fun fromYaml(source: String, transfer: Function<String, String>): EntityInstance {
        return fromSection(Configuration.loadFromString(source, Type.YAML), transfer)
    }

    override fun fromJson(source: String, transfer: Function<String, String>): EntityInstance {
        return fromSection(Configuration.loadFromString(source, Type.JSON), transfer)
    }

    override fun fromSection(section: ConfigurationSection, transfer: Function<String, String>): EntityInstance {
        val output = Configuration.empty(Type.JSON)
        // 对节点进行转换
        section.getValues(true).forEach { (k, v) ->
            // 移除村民职业数据，会崩溃客户端
            // TODO 2023/4/17
            if (k == "metadata.profession") {
                return@forEach
            }
            if (v !is ConfigurationSection) {
                output[transfer.apply(k)] = v
            }
        }
        // 加载单位
        val type = EntityTypes::class.java.getEnum(output.getString("entityType")!!)
        val typeRegistry = Adyeshach.api().getEntityTypeRegistry()
        return Serializer.gson.fromJson(output.toString(), typeRegistry.getEntityClass(type)) as EntityInstance
    }

    companion object {

        @Awake(LifeCycle.CONST)
        fun init() {
            PlatformFactory.registerAPI<AdyeshachEntitySerializer>(DefaultAdyeshachEntitySerializer())
        }
    }
}