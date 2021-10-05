package ink.ptms.adyeshach.internal.migrate

import org.bukkit.Bukkit

/**
 * @author sky
 * @since 2020-08-14 18:17
 */
abstract class Migrate {

    abstract fun depend(): String

    abstract fun migrate()

    fun isEnabled(): Boolean {
        return Bukkit.getPluginManager().isPluginEnabled(depend())
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> getPlugin(): T {
        return Bukkit.getPluginManager().getPlugin(depend()) as T
    }

    companion object {

        val migrates = HashMap<String, Migrate>()

        init {
            migrates["Citizens"] = MigrateCitizens()
            migrates["ServerNPC"] = MigrateServerNPC()
        }
    }
}