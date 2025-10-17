package ink.ptms.adyeshach.impl.script

import ink.ptms.adyeshach.core.util.errorBy
import ink.ptms.adyeshach.impl.getEntities
import ink.ptms.adyeshach.impl.getManager
import ink.ptms.adyeshach.impl.isEntitySelected
import ink.ptms.adyeshach.impl.throwUndefinedError
import taboolib.module.kether.KetherParser
import taboolib.module.kether.combinationParser
import taboolib.module.kether.script

@KetherParser(["rename"], namespace = "adyeshach", shared = true)
private fun actionName() = combinationParser {
    it.group(text()).apply(it) { newId ->
        now {
            val script = script()
            if (script.getManager() == null || !script.isEntitySelected()) {
                script.throwUndefinedError()
            }
            script.getEntities().forEach { e -> e.id = newId }
        }
    }
}