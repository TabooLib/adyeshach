package ink.ptms.adyeshach.impl.entity

import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.entity.EntityInstance
import ink.ptms.adyeshach.core.entity.EntitySerializable
import taboolib.library.configuration.ConfigurationSection
import java.util.function.Function

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.entity.DefaultSerializable
 *
 * @author 坏黑
 * @since 2022/6/19 16:11
 */
interface DefaultSerializable : EntitySerializable {

    override fun toJson(): String {
        return Adyeshach.api().getEntitySerializer().toJson(this as EntityInstance)
    }

    override fun toYaml(transfer: Function<String, String>): ConfigurationSection {
        return Adyeshach.api().getEntitySerializer().toYaml(this as EntityInstance, transfer)
    }

    override fun toSection(section: ConfigurationSection, transfer: Function<String, String>) {
        Adyeshach.api().getEntitySerializer().toSection(this as EntityInstance, section, transfer)
    }
}