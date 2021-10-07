package ink.ptms.adyeshach.api

import ink.ptms.adyeshach.Adyeshach
import ink.ptms.adyeshach.common.entity.editor.EditorMode
import taboolib.module.configuration.ConfigNode
import java.lang.Exception

object AdyeshachSettings {

    enum class SpawnTrigger {

        KEEP_ALIVE, JOIN
    }

    val editorMode: EditorMode
        get() = try {
            EditorMode.valueOf(Adyeshach.conf.getString("Settings.editor-mode", "BOOK")!!.uppercase())
        } catch (ignored: Exception) {
            EditorMode.BOOK
        }

    val spawnTrigger: SpawnTrigger
        get() = try {
            SpawnTrigger.valueOf(Adyeshach.conf.getString("Settings.spawn-trigger")!!.uppercase())
        } catch (ignored: Exception) {
            SpawnTrigger.KEEP_ALIVE
        }

    @ConfigNode("Settings.debug")
    var debug = false
        private set

    @ConfigNode("Settings.visible-distance")
    var visibleDistance = 64.0
        private set

    @ConfigNode("Settings.pathfinder-sync")
    var pathfinderSync = true
        private set

    @ConfigNode("Settings.delete-file-in-unknown-world")
    var deleteFileInUnknownWorld = emptyList<String>()

    fun isSpecifiedWorld(world: String): Boolean {
        return deleteFileInUnknownWorld.any { if (it.endsWith("?")) world.contains(it.substring(0, it.length - 1)) else world == it }
    }
}