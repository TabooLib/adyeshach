package ink.ptms.adyeshach.api

import ink.ptms.adyeshach.Adyeshach
import ink.ptms.adyeshach.common.editor.EditorMode

object Settings {

    enum class SpawnTrigger {

        KEEP_ALIVE, JOIN
    }

    enum class Pathfinder {

        PROXY, NATIVE
    }

    val debug: Boolean
        get() = Adyeshach.conf.getBoolean("Settings.debug")

    val editorMode: EditorMode
        get() = try {
            EditorMode.valueOf(Adyeshach.conf.getString("Settings.editor-mode", "BOOK")!!.toUpperCase())
        } catch (t: Throwable) {
            EditorMode.BOOK
        }

    val visibleDistance: Double
        get() = Adyeshach.conf.getDouble("Settings.visible-distance", 64.0)

    val spawnTrigger: SpawnTrigger
        get() = when (Adyeshach.conf.getString("Settings.spawn-trigger")) {
            "KEEP_ALIVE" -> SpawnTrigger.KEEP_ALIVE
            "JOIN" -> SpawnTrigger.JOIN
            else -> SpawnTrigger.KEEP_ALIVE
        }

    val pathfinder: Pathfinder
        get() = when (Adyeshach.conf.getString("Settings.pathfinder")) {
            "PROXY" -> Pathfinder.PROXY
            else -> Pathfinder.NATIVE
        }

    val pathfinderSync: Boolean
        get() = Adyeshach.conf.getBoolean("Settings.pathfinder-sync", true)
}