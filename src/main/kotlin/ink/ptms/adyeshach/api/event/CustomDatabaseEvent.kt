package ink.ptms.adyeshach.api.event

import ink.ptms.adyeshach.internal.database.Database
import io.izzel.taboolib.module.event.EventNormal

/**
 * @Author sky
 * @Since 2020-08-14 14:52
 */
class CustomDatabaseEvent(val name: String, var database: Database? = null) : EventNormal<CustomDatabaseEvent>() {


}