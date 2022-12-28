package ink.ptms.adyeshach.impl.entity.trait

import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.AdyeshachLanguage
import ink.ptms.adyeshach.core.entity.EntityInstance
import org.bukkit.entity.Player
import taboolib.common.platform.function.console
import taboolib.common.util.unsafeLazy
import taboolib.module.configuration.createLocal
import java.util.*

abstract class Trait {

    val data by unsafeLazy {
        try {
            createLocal("npc/traits/${getName()}.yml")
        } catch (ex: Throwable) {
            ex.printStackTrace()
            val file = "npc/traits/${getName()}-temp-${UUID.randomUUID().toString().replace("-", "").lowercase()}.yml"
            Adyeshach.api().getLanguage().sendLang(console().cast(), "trait-data-error", "npc/traits/${getName()}.yml", file)
            createLocal(file)
        }
    }

    val language: AdyeshachLanguage
        get() = Adyeshach.api().getLanguage()

    abstract fun getName(): String

    abstract fun edit(player: Player, entityInstance: EntityInstance)
}