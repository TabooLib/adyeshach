package ink.ptms.adyeshach.impl.entity

import ink.ptms.adyeshach.api.event.AdyeshachNaturalMetaGenerateEvent
import ink.ptms.adyeshach.common.api.Adyeshach
import ink.ptms.adyeshach.common.api.MinecraftMeta
import ink.ptms.adyeshach.common.api.MinecraftMetadataParser
import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.MetaNatural
import org.bukkit.entity.Player

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.entity.DefaultMetaNatural
 *
 * @author 坏黑
 * @since 2022/6/20 01:33
 */
class DefaultMetaNatural<T, E : EntityInstance>(index: Int, key: String, def: T) : MetaNatural<T, E>(index, key, def) {

    override fun getMetadataParser(): MinecraftMetadataParser<Any> {
        return Adyeshach.api().getMinecraftAPI().getEntityMetadataHandler().getParser(def)!!
    }

    @Suppress("UNCHECKED_CAST")
    override fun generateMetadata(player: Player, entityInstance: EntityInstance): MinecraftMeta? {
        if (index == -1) {
            return null
        }
        entityInstance as DefaultEntityInstance
        var obj = entityInstance.metadata[key] ?: return null
        obj = getMetadataParser().parse(obj)
        val event = AdyeshachNaturalMetaGenerateEvent(entityInstance, player, this as MetaNatural<Any, out EntityInstance>, obj)
        return if (event.call()) getMetadataParser().createMeta(index, event.value) else null
    }

    override fun updateEntityMetadata(entityInstance: EntityInstance) {
        val operator = Adyeshach.api().getMinecraftAPI().getEntityOperator()
        entityInstance.forViewers {
            operator.updateEntityMetadata(it, entityInstance.index, generateMetadata(it, entityInstance) ?: return@forViewers)
        }
    }
}