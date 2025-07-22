package ink.ptms.adyeshach.core.entity.manager.event

import ink.ptms.adyeshach.core.entity.EntityInstance
import org.bukkit.Location
import org.bukkit.entity.LivingEntity
import taboolib.common.event.CancelableInternalEvent

class ControllerLookEvent(
    val entity: EntityInstance,
    val lookAt: LivingEntity,
    val lookTarget: Location,
)