package ink.ptms.adyeshach.impl

import ink.ptms.adyeshach.common.api.AdyeshachEntitySerializer
import ink.ptms.adyeshach.common.entity.EntityInstance
import taboolib.library.configuration.ConfigurationSection
import taboolib.module.configuration.Configuration
import taboolib.module.configuration.Type
import java.util.function.Function

/**
 * Adyeshach
 * ink.ptms.adyeshach.internal.DefaultAdyeshachEntitySerializer
 *
 * @author 坏黑
 * @since 2022/6/19 15:44
 */
class DefaultAdyeshachEntitySerializer : AdyeshachEntitySerializer {

    override fun fromYaml(source: String, transfer: Function<String, String>): EntityInstance? {
        val conf = Configuration.empty(Type.JSON)
        val section = Configuration.loadFromString(source, Type.YAML)
        section.getValues(true).forEach { (k, v) ->
            if (v !is ConfigurationSection) {
                conf[transfer.apply(k)] = v
            }
        }
        return fromJson(conf.saveToString())
    }

    override fun fromJson(source: String, transfer: Function<String, String>): EntityInstance? {
        val conf = Configuration.empty(Type.JSON)
        val section = Configuration.loadFromString(source, Type.JSON)
        section.getValues(true).forEach { (k, v) ->
            if (v !is ConfigurationSection) {
                conf[transfer.apply(k)] = v
            }
        }
        TODO("Not yet implemented")
    }
}