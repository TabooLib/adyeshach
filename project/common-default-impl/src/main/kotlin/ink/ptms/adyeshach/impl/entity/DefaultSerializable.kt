package ink.ptms.adyeshach.impl.entity

import ink.ptms.adyeshach.common.entity.EntitySerializable
import ink.ptms.adyeshach.common.util.serializer.Serializer
import taboolib.library.configuration.ConfigurationSection
import taboolib.module.configuration.Configuration
import taboolib.module.configuration.Type
import java.util.function.Function

/**
 * Adyeshach
 * ink.ptms.adyeshach.internal.entity.DefaultSerializable
 *
 * @author 坏黑
 * @since 2022/6/19 16:11
 */
interface DefaultSerializable : EntitySerializable {

    override fun toJson(): String {
        return Serializer.gson.toJson(this)
    }

    override fun toYaml(transfer: Function<String, String>): ConfigurationSection {
        val conf = Configuration.loadFromString(Serializer.gson.toJson(this), Type.JSON)
        conf.getValues(true).forEach { (k, v) ->
            if (v !is ConfigurationSection) {
                conf[transfer.apply(k)] = v
            }
        }
        conf.changeType(Type.YAML)
        return conf
    }

    override fun toYaml(section: ConfigurationSection, transfer: Function<String, String>) {
        return toYaml(transfer).getValues(false).forEach { (k, v) -> section[k] = v }
    }
}