package ink.ptms.adyeshach.common.entity

import org.bukkit.entity.Player

@Deprecated("Outdated but usable")
open class MetaMasked<T>(index: Int, key: String, val mask: Byte, def: Boolean, v2: ink.ptms.adyeshach.core.entity.Meta<*>) : Meta<Any>(index, key, def, v2) {

    override fun generateMetadata(player: Player, entityInstance: EntityInstance): Any? {
        return v2.generateMetadata(player, entityInstance.v2)?.source()
    }

    override fun toString(): String {
        return "MetaMasked(mask=$mask, dataWatcher=$dataWatcher) ${super.toString()}"
    }
}