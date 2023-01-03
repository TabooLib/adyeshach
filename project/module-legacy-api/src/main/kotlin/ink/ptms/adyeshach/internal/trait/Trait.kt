package ink.ptms.adyeshach.internal.trait

import ink.ptms.adyeshach.common.entity.EntityInstance
import org.bukkit.entity.Player
import taboolib.module.configuration.Configuration

@Deprecated("Outdated and unavailable")
abstract class Trait {

    val data by lazy { Configuration.empty() }

    abstract fun getName(): String

    abstract fun edit(player: Player, entityInstance: EntityInstance)
}