package ink.ptms.adyeshach.core.serializer.type

import com.google.gson.*
import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.entity.controller.*
import ink.ptms.adyeshach.core.serializer.SerializerType
import java.lang.reflect.Type

/**
 * @author sky
 * @since 2020-08-20 20:10
 */
@SerializerType(baseClass = Controller::class)
class TypeController : JsonSerializer<Controller>, JsonDeserializer<Controller> {

    override fun serialize(controller: Controller, type: Type, context: JsonSerializationContext): JsonElement {
        if (controller is ControllerSource) {
            return controller.source
        }
        return JsonObject().also {
            it.addProperty("type", controller::class.java.name)
            it.add("data", gson.toJsonTree(controller))
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun deserialize(element: JsonElement, type: Type, context: JsonDeserializationContext): Controller {
        if (element is JsonPrimitive) {
            val registry = Adyeshach.api().getEntityControllerRegistry()
            // 对旧版本进行兼容
            when (element.asString) {
                "LookAtPlayer" -> PrepareController(registry.getControllerGenerator("LOOK_AT_PLAYER")!!, element)
                "LookAtPlayerAlways" -> PrepareController(registry.getControllerGenerator("LOOK_AT_PLAYER_ALWAYS")!!, element)
                "RandomLookGround" -> PrepareController(registry.getControllerGenerator("RANDOM_LOOKAROUND")!!, element)
                else -> PrepareController(ControllerGenerator(LegacyController::class.java) { LegacyController(it, element) }, element)
            }
        }
        return try {
            val obj = element.asJsonObject
            val clazz = Class.forName(obj.get("type").asString) as Class<Controller>
            val controller = gson.fromJson(obj.get("data"), clazz)
            PrepareController(ControllerGenerator(clazz) {
                controller.entity = it
                controller
            }, element)
        } catch (ex: Throwable) {
            PrepareController(ControllerGenerator(ErrorController::class.java) { ErrorController(it, element, ex) }, element)
        }
    }

    companion object {

        val gson: Gson = GsonBuilder()
            .setPrettyPrinting()
            .excludeFieldsWithoutExposeAnnotation()
            .create()
    }
}