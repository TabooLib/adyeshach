package ink.ptms.adyeshach.impl

import ink.ptms.adyeshach.core.entity.EntityInstance
import ink.ptms.adyeshach.core.entity.manager.Manager
import taboolib.common.platform.function.getDataFolder
import taboolib.common.util.unsafeLazy
import taboolib.library.kether.LocalizedException
import taboolib.module.kether.ScriptContext
import taboolib.module.kether.Workspace
import java.io.File

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.DefaultScriptManager
 *
 * @author 坏黑
 * @since 2022/12/17 21:58
 */
object DefaultScriptManager {

    val workspace by unsafeLazy { Workspace(File(getDataFolder(), "npc/script"), ".ady", listOf("adyeshach")) }
}

fun ScriptContext.getManager(): Manager? {
    return rootFrame().variables().get<Manager?>("@manager").orElse(null)
}

fun ScriptContext.setManager(manager: Manager?) {
    rootFrame().variables().set("@manager", manager)
}

fun ScriptContext.getEntity(): EntityInstance {
    return getEntities().first()
}

fun ScriptContext.getEntities(): List<EntityInstance> {
    return get("@entities", emptyList())!!
}

fun ScriptContext.setEntities(entities: List<EntityInstance>) {
    set("@entities", entities)
}

fun ScriptContext.isEntitySelected(): Boolean {
    return getEntities().isNotEmpty()
}

fun loadError(message: String): LocalizedException {
    return LocalizedException.of("load-error.custom", message)
}