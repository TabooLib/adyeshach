package ink.ptms.adyeshach.internal.trait.impl

import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.internal.trait.Trait
import org.bukkit.entity.Player

class Patrol : Trait() {

    override fun getName(): String {
        return "patrol"
    }

    override fun edit(player: Player, entityInstance: EntityInstance) {
        TODO("Not yet implemented")
    }

}