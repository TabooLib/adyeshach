package ink.ptms.adyeshach.impl

import ink.ptms.adyeshach.core.entity.EntityInstance
import ink.ptms.adyeshach.core.entity.manager.Manager
import ink.ptms.adyeshach.core.util.errorBy
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.function.getDataFolder
import taboolib.common.platform.function.warning
import taboolib.common.util.resettableLazy
import taboolib.common.util.unsafeLazy
import taboolib.library.kether.LocalizedException
import taboolib.module.kether.ScriptContext
import taboolib.module.kether.Workspace
import taboolib.module.kether.printKetherErrorMessage
import java.io.File

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.DefaultScriptManager
 *
 * @author 坏黑
 * @since 2022/12/17 21:58
 */
object DefaultScriptManager {

    /** 脚本工作空间 */
    val workspace by unsafeLazy { Workspace(File(getDataFolder(), "npc/script"), ".ady", listOf("adyeshach")) }

    /** 脚本加载器 */
    val workspaceLoader by resettableLazy {
        try {
            workspace.loadAll()
        } catch (e: Exception) {
            warning("An error occurred while loading the script")
            e.printKetherErrorMessage()
        }
    }

    @Awake(LifeCycle.ACTIVE)
    private fun active() {
        workspaceLoader
    }
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

fun ScriptContext.setEntitySelectId(id: String) {
    set("@entities_id", id)
}

fun ScriptContext.isEntitySelected(): Boolean {
    return getEntities().isNotEmpty()
}

fun ScriptContext.getEntitySelectId(): String? {
    return get("@entities_id", null)
}

fun ScriptContext.throwUndefinedError() {
    val selectId = getEntitySelectId()
    if (selectId != null) {
        errorBy("error-entity-undefined", selectId)
    }
    errorBy("error-no-manager-or-entity-selected")
}

fun loadError(message: String): LocalizedException {
    return LocalizedException.of("load-error.custom", message)
}