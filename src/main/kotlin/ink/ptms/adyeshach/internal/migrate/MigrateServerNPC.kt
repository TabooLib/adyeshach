package ink.ptms.adyeshach.internal.migrate

import com.isnakebuzz.servernpc.Main
import io.izzel.taboolib.module.inject.TInject

/**
 * @Author sky
 * @Since 2020-08-14 18:18
 */
class MigrateServerNPC : Migrate() {

    @TInject("ServerNPC")
    lateinit var plugin: Main
        private set

    override fun depend(): String {
        return "ServerNPC"
    }

    override fun migrate() {
        plugin.npcManager.npcList.forEach {

        }
    }
}