package ink.ptms.adyeshach.internal.migrate

import org.bukkit.Bukkit

/**
 * @Author sky
 * @Since 2020-08-14 18:17
 */
abstract class Migrate {

    abstract fun depend(): String

    abstract fun migrate()

    fun isEnabled(): Boolean {
        return Bukkit.getPluginManager().isPluginEnabled(depend())
    }

    companion object {

        val migrates = hashMapOf("Citizens" to MigrateCitizens(), "ServerNPC" to MigrateServerNPC(), "QuestCreator" to MigrateQuestCreator())
    }
}