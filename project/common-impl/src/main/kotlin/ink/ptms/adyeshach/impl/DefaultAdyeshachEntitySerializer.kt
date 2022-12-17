package ink.ptms.adyeshach.impl

import ink.ptms.adyeshach.core.AdyeshachEntitySerializer
import ink.ptms.adyeshach.core.entity.EntityInstance
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

    override fun fromYaml(source: String, transfer: Function<String, String>): EntityInstance? {
        val conf = Configuration.loadFromString(source, Type.YAML)
        conf.changeType(Type.JSON)
        return fromJson(conf.saveToString(), transfer)
    }

    override fun fromJson(source: String, transfer: Function<String, String>): EntityInstance? {
        val conf = Configuration.empty(Type.JSON)
        val section = Configuration.loadFromString(source, Type.JSON)
        // 对节点进行转换
        section.getValues(true).forEach { (k, v) ->
            if (v !is ConfigurationSection) {
                conf[transfer.apply(k)] = v
            }
        }
        // TODO
        return null
    }

    companion object {

        @Awake(LifeCycle.LOAD)
        fun init() {
            PlatformFactory.registerAPI<AdyeshachEntitySerializer>(DefaultAdyeshachEntitySerializer())
        }
    }
}