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
class DefaultAdyeshachEntitySerializer : AdyeshachEntitySerializer {

    override fun fromYaml(source: String, transfer: Function<String, String>): EntityInstance {
        val copy = Configuration.loadFromString(source, Type.YAML)
        copy.changeType(Type.JSON)
        return fromJson(copy.saveToString(), transfer)
    }

    override fun fromJson(source: String, transfer: Function<String, String>): EntityInstance {
        val copy = Configuration.empty(Type.JSON)
        val section = Configuration.loadFromString(source, Type.JSON)
        // 对节点进行转换
        section.getValues(true).forEach { (k, v) ->
            if (v !is ConfigurationSection) {
                copy[transfer.apply(k)] = v
            }
        }
        // 加载单位
        val type = EntityTypes::class.java.getEnum(copy.getString("entityType")!!)
        val typeRegistry = Adyeshach.api().getEntityTypeRegistry()
        return Serializer.gson.fromJson(copy.toString(), typeRegistry.getEntityClass(type)) as EntityInstance
    }

    companion object {

        @Awake(LifeCycle.INIT)
        fun init() {
            PlatformFactory.registerAPI<AdyeshachEntitySerializer>(DefaultAdyeshachEntitySerializer())
        }
    }
}