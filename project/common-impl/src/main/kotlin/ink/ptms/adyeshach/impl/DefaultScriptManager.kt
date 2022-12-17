package ink.ptms.adyeshach.impl

import taboolib.common.platform.function.getDataFolder
import taboolib.common.util.unsafeLazy
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