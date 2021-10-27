package ink.ptms.adyeshach.common.util.serializer.type

import com.google.gson.*
import ink.ptms.adyeshach.api.AdyeshachAPI
import ink.ptms.adyeshach.common.entity.ai.Controller
import ink.ptms.adyeshach.common.entity.ai.ControllerGenerator
import ink.ptms.adyeshach.common.entity.ai.ControllerNone
import ink.ptms.adyeshach.common.util.serializer.SerializerType
import java.lang.reflect.Type

/**
 * @author sky
 * @since 2020-08-20 20:10
 */
@SerializerType(baseClass = Controller::class)
class TypeController : JsonSerializer<Controller>, JsonDeserializer<Controller> {

    override fun serialize(a: Controller, p1: Type, p2: JsonSerializationContext): JsonElement {
        val controller = if (a is Controller.Pre) {
            AdyeshachAPI.getControllerGenerator().entries.firstOrNull { it.value.type == a.controller.type }
        } else {
            AdyeshachAPI.getControllerGenerator().entries.firstOrNull { it.value.type == a::class.java }
        }
        return JsonPrimitive(controller?.key ?: a.javaClass.name)
    }

    override fun deserialize(a: JsonElement, p1: Type?, p2: JsonDeserializationContext): Controller {
        val controller = AdyeshachAPI.getControllerGenerator().entries.firstOrNull { it.key == a.asString }
        return Controller.Pre(controller?.value ?: ControllerGenerator(ControllerNone::class.java) { ControllerNone(it, a.asString) })
    }
}