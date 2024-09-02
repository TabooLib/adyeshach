package ink.ptms.adyeshach.impl.entity

import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.MinecraftMeta
import ink.ptms.adyeshach.core.MinecraftMetadataParser
import ink.ptms.adyeshach.core.entity.EntityInstance
import ink.ptms.adyeshach.core.entity.MetaMasked
import ink.ptms.adyeshach.core.entity.manager.event.MetaMaskedGenerateEvent
import ink.ptms.adyeshach.impl.DefaultAdyeshachAPI
import org.bukkit.entity.Player
import taboolib.common.util.unsafeLazy

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.entity.DefaultMetaMasked
 *
 * @author 坏黑
 * @since 2022/6/20 01:32
 */
@Suppress("UNCHECKED_CAST")
class DefaultMetaMasked<T : EntityInstance>(index: Int, key: String, group: String, mask: Byte, def: Boolean) : MetaMasked<T>(index, key, group, mask, def) {

    val parser by unsafeLazy { Adyeshach.api().getMinecraftAPI().getEntityMetadataHandler().getParser("Byte") as MinecraftMetadataParser<Any> }

    override fun getMetadataParser(): MinecraftMetadataParser<Any> {
        return parser
    }

    override fun generateMetadata(player: Player, entityInstance: EntityInstance): MinecraftMeta? {
        if (index == -1) {
            return null
        }
        entityInstance as DefaultEntityInstance
        val event = MetaMaskedGenerateEvent(entityInstance, player, this, HashMap())
        var bits = 0
        val byteMask = entityInstance.metadataMask[entityInstance.getByteMaskKey(index)] ?: return null
        entityInstance.getAvailableEntityMeta().filter { it.index == index && it is MetaMasked<*> }.forEach {
            event.byteMask[it as MetaMasked<*>] = byteMask[it.key] == true
        }
        val eventBus = DefaultAdyeshachAPI.localEventBus
        if (eventBus.callMaskedMetaGenerate(event)) {
            event.byteMask.filter { it.value }.forEach { (k, _) -> bits += k.mask }
            return parser.createMeta(index, bits.toByte())
        }
        return null
    }

    override fun generateMetadata(entityInstance: EntityInstance): MinecraftMeta {
        if (index == -1) {
            error("Meta not supported")
        }
        entityInstance as DefaultEntityInstance
        val generateByteMask = HashMap<MetaMasked<*>, Boolean>()
        var bits = 0
        val byteMask = entityInstance.metadataMask[entityInstance.getByteMaskKey(index)] ?: error("Meta not supported")
        entityInstance.getAvailableEntityMeta().filter { it.index == index && it is MetaMasked<*> }.forEach {
            generateByteMask[it as MetaMasked<*>] = byteMask[it.key] == true
        }
        generateByteMask.filter { it.value }.forEach { (k, _) -> bits += k.mask }
        return parser.createMeta(index, bits.toByte())
    }

    override fun updateEntityMetadata(entityInstance: EntityInstance) {
        val handler = Adyeshach.api().getMinecraftAPI().getPacketHandler()
        entityInstance.forViewers {
            handler.bufferMetadataPacket(it, entityInstance.index, generateMetadata(it, entityInstance) ?: return@forViewers)
        }
    }
}