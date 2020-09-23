package ink.ptms.adyeshach.internal.trait

import ink.ptms.adyeshach.common.entity.EntityInstance
import org.bukkit.entity.Player

abstract class Trait {

    abstract fun getName(): String

    abstract fun edit(player: Player, entityInstance: EntityInstance)

}