package ink.ptms.adyeshach.core.event

import ink.ptms.adyeshach.core.entity.EntityInstance
import ink.ptms.adyeshach.core.entity.controller.Controller
import taboolib.platform.type.BukkitProxyEvent

/**
 * @author sky
 * @since 2020-08-14 19:21
 */
class AdyeshachControllerAddEvent(val entity: EntityInstance, val controller: Controller) : BukkitProxyEvent()