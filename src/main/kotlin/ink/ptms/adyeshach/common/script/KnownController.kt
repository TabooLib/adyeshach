package ink.ptms.adyeshach.common.script

import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.ai.Controller
import java.util.function.Function
import kotlin.reflect.KClass

/**
 * @Author sky
 * @Since 2020-08-30 19:22
 */
class KnownController(val controllerClass: KClass<out Controller>, val get: Function<EntityInstance, Controller>)