package ink.ptms.adyeshach.api

import ink.ptms.adyeshach.Adyeshach
import ink.ptms.adyeshach.common.editor.EditorMode

class Settings {

    val debug: Boolean by lazy {
        Adyeshach.conf.getBoolean("Settings.debug")
    }

    val editorMode: EditorMode by lazy {
        try {
            EditorMode.valueOf(Adyeshach.conf.getString("Settings.editor-mode", "BOOK")!!.toUpperCase())
        } catch (t: Throwable) {
            EditorMode.BOOK
        }
    }

    val visibleDistance: Double by lazy {
        Adyeshach.conf.getDouble("Settings.visible-distance", 64.0)
    }

    val pathfinderProxy: Boolean by lazy {
        Adyeshach.conf.getBoolean("Settings.pathfinder-proxy", true)
    }

    val spawnTrigger: SpawnTrigger by lazy {
        when (Adyeshach.conf.getString("Settings.spawn-trigger")) {
            "KEEP_ALIVE" -> SpawnTrigger.KEEP_ALIVE
            "JOIN" -> SpawnTrigger.JOIN
            else -> SpawnTrigger.KEEP_ALIVE
        }
    }

    enum class SpawnTrigger {

        KEEP_ALIVE, JOIN
    }

    companion object {

        fun get() = Adyeshach.settings
    }
}