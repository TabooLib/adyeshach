package ink.ptms.adyeshach.common.entity

import ink.ptms.adyeshach.api.event.AdyeshachMaskedMetaGenerateEvent
import ink.ptms.adyeshach.common.bukkit.data.DataWatcher
import org.bukkit.entity.Player

open class MetaMasked<E : EntityInstance>(index: Int, key: String, val mask: Byte, def: Boolean) : Meta<E>(index, key, def) {

    init {
        dataWatcher = DataWatcher.DataByte
    }

    override fun generateMetadata(player: Player, entityInstance: EntityInstance): Any? {
        if (index == -1) {
            return null
        }
        val event = AdyeshachMaskedMetaGenerateEvent(entityInstance, player, this, HashMap())
        var bits = 0
        val byteMask = entityInstance.metadataMask[entityInstance.getByteMaskKey(index)] ?: return null
        entityInstance.getAvailableEntityMeta().filter { it.index == index && it is MetaMasked<*> }.forEach {
            event.byteMask[it as MetaMasked<*>] = byteMask[it.key] == true
        }
        event.call()
        event.byteMask.filter { it.value }.forEach { (k, _) ->
            bits += k.mask
        }
        return dataWatcher.createMetadata(index, bits.toByte())
    }

    override fun toString(): String {
        return "MetaMasked(mask=$mask, dataWatcher=$dataWatcher) ${super.toString()}"
    }
}