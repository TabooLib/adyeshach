package ink.ptms.adyeshach.internal.trait.impl

import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.internal.trait.Trait
import org.bukkit.entity.Player

class Command : Trait() {

    override fun getName(): String {
        return "command"
    }

    override fun edit(player: Player, entityInstance: EntityInstance) {
        TODO("Not yet implemented")
    }

}