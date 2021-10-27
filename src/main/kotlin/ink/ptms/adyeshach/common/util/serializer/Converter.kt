package ink.ptms.adyeshach.common.util.serializer

import com.google.gson.*
import taboolib.library.configuration.ConfigurationSection
import taboolib.module.kether.isInt

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
                else -> json.add(k, convert(section.get(k)!!))
            }
        }
    }

    fun jsonToYaml(source: String, section: ConfigurationSection) {
        JsonParser().parse(source).asJsonObject.entrySet().forEach {
            jsonToYaml(section, it.key, it.value)
        }
    }

    fun jsonToYaml(section: ConfigurationSection, key: String, value: JsonElement) {
        when {
            value.isJsonObject -> {
                val sub = section.createSection(key)
                value.asJsonObject.entrySet().forEach {
                    jsonToYaml(sub, it.key, it.value)
                }
            }
            value.isJsonArray -> {
                section.set(key, value.asJsonArray.map { convert(it.asJsonPrimitive) }.toList())
            }
            else -> {
                section.set(key, convert(value.asJsonPrimitive))
            }
        }
    }

    fun convert(value: Any): JsonPrimitive {
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

    fun convert(value: JsonPrimitive): Any {
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