package ink.ptms.adyeshach.common.util.serializer.type

import com.google.gson.*
import ink.ptms.adyeshach.common.api.Adyeshach
import ink.ptms.adyeshach.common.entity.ai.Controller
import ink.ptms.adyeshach.common.entity.ai.ControllerGenerator
import ink.ptms.adyeshach.common.entity.ai.EmptyController
import ink.ptms.adyeshach.common.entity.ai.PrepareController
import ink.ptms.adyeshach.common.util.serializer.SerializerType
import java.lang.reflect.Type

/**
 * @author sky
 * @since 2020-08-20 20:10
 */
@SerializerType(baseClass = Controller::class)
class TypeController : JsonSerializer<Controller>, JsonDeserializer<Controller> {

    override fun serialize(a: Controller, p1: Type, p2: JsonSerializationContext): JsonElement {
        val controller = if (a is PrepareController) {
            Adyeshach.api().getEntityControllerHandler().getControllerGenerator().entries.firstOrNull { it.value.type == a.controller.type }
        } else {
            Adyeshach.api().getEntityControllerHandler().getControllerGenerator().entries.firstOrNull { it.value.type == a::class.java }
        }
        return JsonPrimitive(controller?.key ?: a.javaClass.name)
    }

    override fun deserialize(a: JsonElement, p1: Type?, p2: JsonDeserializationContext): Controller {
        val controller = Adyeshach.api().getEntityControllerHandler().getControllerGenerator().entries.firstOrNull { it.key == a.asString }
        return PrepareController(controller?.value ?: ControllerGenerator(EmptyController::class.java) { EmptyController(it, a.asString) })
    }
}