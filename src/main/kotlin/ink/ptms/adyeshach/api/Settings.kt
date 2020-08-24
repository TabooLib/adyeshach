package ink.ptms.adyeshach.api

import ink.ptms.adyeshach.Adyeshach
import ink.ptms.adyeshach.common.editor.EditorMode

class Settings {

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

    companion object {

        fun get() = Adyeshach.settings
    }
}