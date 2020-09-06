package ink.ptms.adyeshach.internal.migrate

/**
 * @Author sky
 * @Since 2020-08-14 18:18
 */
class MigrateCitizens : Migrate() {

    override fun depend(): String {
        return "Citizens"
    }

    override fun migrate() {
    }
}