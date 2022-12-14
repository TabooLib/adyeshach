package ink.ptms.adyeshach.core.entity.manager.event

import ink.ptms.adyeshach.core.entity.EntityInstance
import ink.ptms.adyeshach.core.entity.Meta

/**
 * @author sky
 * @since 2020-08-14 19:21
 */
class MetaUpdateEvent(val entity: EntityInstance, val meta: Meta<*>, val key: String, var value: Any)