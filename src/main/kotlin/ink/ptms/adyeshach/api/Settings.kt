package ink.ptms.adyeshach.api

import ink.ptms.adyeshach.Adyeshach
import ink.ptms.adyeshach.common.editor.EditorMode
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.configuration.ConfigurationSection

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

    fun getPathfinderProxySpawn(world: World): Location {
        val spawn = Adyeshach.conf.get("Settings.pathfinder-proxy-spawn.${world.name}")
        if (spawn == null || spawn.toString() == "~bukkit") {
            return world.spawnLocation
        }
        return Location(world, (spawn as ConfigurationSection).getDouble("x"), 0.0, spawn.getDouble("z"))
    }

    companion object {

        fun get() = Adyeshach.settings
    }
}