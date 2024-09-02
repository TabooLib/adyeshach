package ink.ptms.adyeshach.impl.entity

import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.MinecraftMeta
import ink.ptms.adyeshach.core.MinecraftMetadataParser
import ink.ptms.adyeshach.core.entity.EntityInstance
import ink.ptms.adyeshach.core.entity.MetaNatural
import ink.ptms.adyeshach.core.entity.manager.event.MetaNaturalGenerateEvent
import ink.ptms.adyeshach.impl.DefaultAdyeshachAPI
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
class DefaultMetaNatural<T, E : EntityInstance>(index: Int, key: String, group: String, def: T, parserId: String) :
    MetaNatural<T, E>(index, key, group, def, parserId) {

    val parser by unsafeLazy {
        Adyeshach.api().getMinecraftAPI().getEntityMetadataHandler().getParser(parserId) as MinecraftMetadataParser<Any>
    }

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
        val eventBus = DefaultAdyeshachAPI.localEventBus
        if (eventBus.callNaturalMetaGenerate(event)) {
            return parser.createMeta(index, event.value)
        }
        return null
    }

    override fun generateMetadata(entityInstance: EntityInstance): MinecraftMeta {
        if (index == -1) {
            error("Meta not supported")
        }
        entityInstance as DefaultEntityInstance
        return parser.createMeta(index, parser.parse(entityInstance.metadata[key] ?: error("Meta not supported")))
    }

    override fun updateEntityMetadata(entityInstance: EntityInstance) {
        val handler = Adyeshach.api().getMinecraftAPI().getPacketHandler()
        entityInstance.forViewers {
            handler.bufferMetadataPacket(it, entityInstance.index, generateMetadata(it, entityInstance) ?: return@forViewers)
        }
    }
}