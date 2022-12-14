package ink.ptms.adyeshach.impl.entity

import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.MinecraftMeta
import ink.ptms.adyeshach.core.MinecraftMetadataParser
import ink.ptms.adyeshach.core.entity.EntityInstance
import ink.ptms.adyeshach.core.entity.MetaNatural
import ink.ptms.adyeshach.core.entity.manager.event.MetaNaturalGenerateEvent
import org.bukkit.entity.Player
import taboolib.common.util.unsafeLazy

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.entity.DefaultMetaNatural
 *
 * @author 坏黑
 * @since 2022/6/20 01:33
 */
@Suppress("UNCHECKED_CAST")
class DefaultMetaNatural<T, E : EntityInstance>(index: Int, key: String, def: T) : MetaNatural<T, E>(index, key, def) {

    val parser by unsafeLazy { Adyeshach.api().getMinecraftAPI().getEntityMetadataHandler().getParser(def) as MinecraftMetadataParser<Any> }

    override fun getMetadataParser(): MinecraftMetadataParser<Any> {
        return parser
    }

    @Suppress("UNCHECKED_CAST")
    override fun generateMetadata(player: Player, entityInstance: EntityInstance): MinecraftMeta? {
        if (index == -1) {
            return null
        }
        entityInstance as DefaultEntityInstance
        var obj = entityInstance.metadata[key] ?: return null
        obj = parser.parse(obj)
        val event = MetaNaturalGenerateEvent(entityInstance, player, this as MetaNatural<Any, out EntityInstance>, obj)
        val eventBus = entityInstance.getEventBus()
        if (eventBus == null || eventBus.callNaturalMetaGenerate(event)) {
            return parser.createMeta(index, event.value)
        }
        return null
    }

    override fun updateEntityMetadata(entityInstance: EntityInstance) {
        val operator = Adyeshach.api().getMinecraftAPI().getEntityOperator()
        entityInstance.forViewers {
            operator.updateEntityMetadata(it, entityInstance.index, generateMetadata(it, entityInstance) ?: return@forViewers)
        }
    }
}