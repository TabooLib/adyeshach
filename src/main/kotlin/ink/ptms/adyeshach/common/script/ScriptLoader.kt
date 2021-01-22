package ink.ptms.adyeshach.common.script

import ink.ptms.adyeshach.Adyeshach
import io.izzel.kether.common.api.Quest
import io.izzel.kether.common.loader.SimpleQuestLoader
import java.nio.charset.StandardCharsets
import java.util.*

object ScriptLoader {

    fun load(str: String): Quest {
        return SimpleQuestLoader().load(ScriptService, Adyeshach.plugin.logger, "temp_${UUID.randomUUID()}", str.toByteArray(StandardCharsets.UTF_8))
    }

    fun load(str: List<String>): Quest {
        return load(str.joinToString("\n"))
    }
}