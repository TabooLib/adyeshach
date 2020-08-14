package ink.ptms.adyeshach.internal.migrate

import ink.ptms.adyeshach.internal.migrate.Migrate

/**
 * @Author sky
 * @Since 2020-08-14 18:18
 */
class MigrateServerNPC : Migrate() {

    override fun depend(): String {
        return "ServerNPC"
    }

    override fun migrate() {
        // todo migrate logic
    }
}