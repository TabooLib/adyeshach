package ink.ptms.adyeshach.common.util.serializer

import com.google.gson.*
import taboolib.library.configuration.ConfigurationSection
import taboolib.module.kether.isInt
import java.util.function.Function

/**
 * @author sky
 * @since 2020-08-14 15:01
 */
object Converter {

    fun yamlToJson(section: ConfigurationSection): JsonObject {
        return JsonObject().run {
            yamlToJson(section, this)
            this
        }
    }

    fun yamlToJson(section: ConfigurationSection, json: JsonObject) {
        section.getKeys(false).forEach { k ->
            when {
                section.isConfigurationSection(k) -> {
                    json.add(k, JsonObject().run {
                        yamlToJson(section.getConfigurationSection(k)!!, this)
                        this
                    })
                }
                section.isList(k) -> json.add(k, JsonArray().run {
                    section.getList(k)!!.filterNotNull().forEach {
                        this.add(convert(it))
                    }
                    this
                })
                else -> json.add(k, convert(section[k]!!))
            }
        }
    }

    fun jsonToYaml(source: String, section: ConfigurationSection, transfer: Function<String, String> = Function { it }) {
        JsonParser().parse(source).asJsonObject.entrySet().forEach {
            jsonToYaml(section, transfer.apply(it.key), it.value, transfer)
        }
    }

    private fun jsonToYaml(section: ConfigurationSection, key: String, value: JsonElement, transfer: Function<String, String>) {
        when {
            value.isJsonObject -> {
                val sub = section.createSection(key)
                value.asJsonObject.entrySet().forEach {
                    jsonToYaml(sub, transfer.apply(it.key), it.value, transfer)
                }
            }
            value.isJsonArray -> {
                section[key] = value.asJsonArray.map { convert(it.asJsonPrimitive) }.toList()
            }
            else -> {
                section[key] = convert(value.asJsonPrimitive)
            }
        }
    }

    private fun convert(value: Any): JsonPrimitive {
        return when (value) {
            is Number -> {
                if (value.isInt()) {
                    JsonPrimitive(value.toInt())
                } else {
                    JsonPrimitive(value)
                }
            }
            is String -> JsonPrimitive(value)
            is Boolean -> JsonPrimitive(value)
            else -> JsonPrimitive("$value")
        }
    }

    private fun convert(value: JsonPrimitive): Any {
        return when {
            value.isNumber -> {
                if (value.asNumber.isInt()) {
                    value.asNumber.toInt()
                } else {
                    value.asNumber.toDouble()
                }
            }
            value.isString -> value.asString
            value.isBoolean -> value.asBoolean
            else -> "$value"
        }
    }
}