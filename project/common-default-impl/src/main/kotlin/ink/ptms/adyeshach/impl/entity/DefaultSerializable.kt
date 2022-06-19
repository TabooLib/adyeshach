package ink.ptms.adyeshach.impl.entity

import ink.ptms.adyeshach.common.entity.EntitySerializable
import taboolib.library.configuration.ConfigurationSection
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
        TODO("Not yet implemented")
    }

    override fun toYaml(transfer: Function<String, String>): ConfigurationSection {
        TODO("Not yet implemented")
    }

    override fun toYaml(section: ConfigurationSection, transfer: Function<String, String>) {
        TODO("Not yet implemented")
    }
}