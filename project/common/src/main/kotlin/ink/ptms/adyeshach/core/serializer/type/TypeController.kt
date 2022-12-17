package ink.ptms.adyeshach.core.serializer.type

import com.google.gson.*
import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.entity.controller.Controller
import ink.ptms.adyeshach.core.entity.controller.ControllerGenerator
import ink.ptms.adyeshach.core.entity.controller.EmptyController
import ink.ptms.adyeshach.core.entity.controller.PrepareController
import ink.ptms.adyeshach.core.serializer.SerializerType
import java.lang.reflect.Type

/**
 * @author sky
 * @since 2020-08-20 20:10
 */
@SerializerType(baseClass = Controller::class)
class TypeController : JsonSerializer<Controller>, JsonDeserializer<Controller> {

    override fun serialize(a: Controller, p1: Type, p2: JsonSerializationContext): JsonElement {
        val controller = if (a is PrepareController) {
            Adyeshach.api().getEntityControllerRegistry().getControllerGenerator().entries.firstOrNull { it.value.type == a.generator.type }
        } else {
            Adyeshach.api().getEntityControllerRegistry().getControllerGenerator().entries.firstOrNull { it.value.type == a::class.java }
        }
        return JsonPrimitive(controller?.key ?: a.javaClass.name)
    }

    override fun deserialize(a: JsonElement, p1: Type?, p2: JsonDeserializationContext): Controller {
        val controller = Adyeshach.api().getEntityControllerRegistry().getControllerGenerator().entries.firstOrNull { it.key == a.asString }
        return PrepareController(controller?.value ?: ControllerGenerator(EmptyController::class.java) { EmptyController(it, a.asString) })
    }
}