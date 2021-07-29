package ink.ptms.adyeshach.internal.trait

import ink.ptms.adyeshach.common.entity.EntityInstance
import org.bukkit.entity.Player
import taboolib.module.configuration.SecuredFile
import taboolib.module.configuration.createLocal

abstract class Trait {

    val data: SecuredFile
        get() = createLocal("npc/traits/${getName()}.yml")

    abstract fun getName(): String

    abstract fun edit(player: Player, entityInstance: EntityInstance)
}