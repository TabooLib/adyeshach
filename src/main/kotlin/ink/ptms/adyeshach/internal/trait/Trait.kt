package ink.ptms.adyeshach.internal.trait

import ink.ptms.adyeshach.common.entity.EntityInstance
import org.apache.commons.lang3.time.DateFormatUtils
import org.bukkit.entity.Player
import taboolib.common.platform.function.console
import taboolib.common.platform.function.warning
import taboolib.module.configuration.createLocal
import taboolib.module.lang.sendLang
import java.util.*

abstract class Trait {

    val data by lazy {
        try {
            createLocal("npc/traits/${getName()}.yml")
        } catch (ex: Throwable) {
            ex.printStackTrace()
            val file = "npc/traits/${getName()}-temp-${UUID.randomUUID().toString().replace("-", "").lowercase()}.yml"
            console().sendLang("trait-data-error", "npc/traits/${getName()}.yml", file)
            createLocal(file)
        }
    }

    abstract fun getName(): String

    abstract fun edit(player: Player, entityInstance: EntityInstance)
}