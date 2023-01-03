package ink.ptms.adyeshach.common.entity

import org.bukkit.entity.Player

/**
 * @param index -1 = 不可用，-2 = 伪造
 */
@Deprecated("Outdated but usable")
open class MetaNatural<T, E>(index: Int, key: String, def: Any, v2: ink.ptms.adyeshach.core.entity.Meta<*>) : Meta<Any>(index, key, def, v2) {

    @Suppress("UNCHECKED_CAST")
    override fun generateMetadata(player: Player, entityInstance: EntityInstance): Any? {
        return v2.generateMetadata(player, entityInstance.v2)?.source()
    }

    override fun toString(): String {
        return "MetaNatural(dataWatcher=$dataWatcher) ${super.toString()}"
    }
}