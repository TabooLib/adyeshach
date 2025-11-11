package ink.ptms.adyeshach.impl.script

import ink.ptms.adyeshach.impl.getEntities
import ink.ptms.adyeshach.impl.getManager
import ink.ptms.adyeshach.impl.isEntitySelected
import ink.ptms.adyeshach.impl.throwUndefinedError
import taboolib.module.kether.*

@KetherParser(["vehicle"], namespace = "adyeshach", shared = true)
internal fun vehicleParser() = scriptParser {
    val method = it.expects("get", "get_cache", "has", "has_cache")
    actionNow {
        val script = script()
        if (script.getManager() == null || !script.isEntitySelected()) {
            script.throwUndefinedError()
        }
        when (method) {
            "get" -> script.getEntities().firstNotNullOfOrNull { e -> e.getVehicle() }
            "get_cache" -> script.getEntities().firstNotNullOfOrNull { e -> e.getVehicleCache() }
            "has" -> script.getEntities().any { e -> e.hasVehicle() }
            "has_cache" -> script.getEntities().any { e -> e.getVehicleCache() != null }
            else -> error("out of case")
        }
    }
}