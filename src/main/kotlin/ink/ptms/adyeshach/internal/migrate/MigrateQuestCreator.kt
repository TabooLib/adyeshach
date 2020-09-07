package ink.ptms.adyeshach.internal.migrate

import com.guillaumevdn.gcore.GCore

/**
 * @Author sky
 * @Since 2020-08-14 18:18
 */
class MigrateQuestCreator : Migrate() {

    override fun depend(): String {
        return "QuestCreator"
    }

    override fun migrate() {
        GCore.inst().npcManager.npcsData
    }
}