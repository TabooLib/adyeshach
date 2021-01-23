package ink.ptms.adyeshach.internal.trait

import ink.ptms.adyeshach.common.entity.EntityInstance
import io.izzel.taboolib.module.db.local.Local
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player

abstract class Trait {

    val data: FileConfiguration
        get() = Local.get().get("npc/traits/${getName()}.yml")

    abstract fun getName(): String

    abstract fun edit(player: Player, entityInstance: EntityInstance)

}