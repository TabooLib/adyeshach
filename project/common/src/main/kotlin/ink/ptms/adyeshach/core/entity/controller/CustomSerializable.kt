package ink.ptms.adyeshach.core.entity.controller

import com.google.gson.JsonObject

/**
 * Adyeshach
 * ink.ptms.adyeshach.core.entity.controller.CustomSerializable
 *
 * @author 坏黑
 * @since 2023/1/3 23:06
 */
interface CustomSerializable {

    fun serialize(): JsonObject
}