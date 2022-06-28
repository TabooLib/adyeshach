package ink.ptms.adyeshach.impl.entity

import ink.ptms.adyeshach.api.event.AdyeshachMaskedMetaGenerateEvent
import ink.ptms.adyeshach.common.api.Adyeshach
import ink.ptms.adyeshach.common.api.MinecraftMeta
import ink.ptms.adyeshach.common.api.MinecraftMetadataParser
import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.MetaMasked
import org.bukkit.entity.Player

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.entity.DefaultMetaMasked
 *
 * @author 坏黑
 * @since 2022/6/20 01:32
 */
class DefaultMetaMasked<T : EntityInstance>(index: Int, key: String, mask: Byte, def: Boolean) : MetaMasked<T>(index, key, mask, def) {

    override fun getMetadataParser(): MinecraftMetadataParser<Any> {
        TODO("Not yet implemented")
    }

    override fun generateMetadata(player: Player, entityInstance: EntityInstance): MinecraftMeta? {
        if (index == -1) {
            return null
        }
        entityInstance as DefaultEntityInstance
        val event = AdyeshachMaskedMetaGenerateEvent(entityInstance, player, this, HashMap())
        var bits = 0
        val byteMask = entityInstance.metadataMask[entityInstance.getByteMaskKey(index)] ?: return null
        entityInstance.getAvailableEntityMeta().filter { it.index == index && it is MetaMasked<*> }.forEach {
            event.byteMask[it as MetaMasked<*>] = byteMask[it.key] == true
        }
        if (event.call()) {
            event.byteMask.filter { it.value }.forEach { (k, _) -> bits += k.mask }
            return getMetadataParser().createMeta(index, bits.toByte())
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