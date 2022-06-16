package ink.ptms.adyeshach.common.entity.ai

import ink.ptms.adyeshach.common.entity.EntityInstance
import java.util.function.Function

class ControllerGenerator(val type: Class<out Controller>, val generator: Function<EntityInstance, Controller>)