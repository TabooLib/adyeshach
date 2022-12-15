package ink.ptms.adyeshach.core.util

import com.google.common.base.Enums
import ink.ptms.adyeshach.core.Adyeshach
import org.bukkit.Location
import org.bukkit.util.NumberConversions
import taboolib.common.platform.function.console

/**
 * 获取语言文件文本
 */
fun asLang(node: String, vararg args: Any) = Adyeshach.api().getLanguage().getLang(console().cast(), node, *args) ?: node

/**
 * 使用语言文件文本抛出异常
 */
fun errorBy(node: String, vararg args: Any): Nothing = error(Adyeshach.api().getLanguage().getLang(console().cast(), node, *args) ?: node)

/**
 * 安全测距
 */
fun Location.safeDistance(loc: Location): Double {
    return if (world!!.name == loc.world!!.name) distance(loc) else Double.MAX_VALUE
}

/**
 * 通过字符串获取枚举值
 */
fun <T : Enum<T>> Class<T>.getEnum(vararg name: Any): T {
    name.forEach { Enums.getIfPresent(this, it.toString()).orNull()?.let { e -> return e } }
    errorBy("error-unable-to-find-enum", simpleName, name.joinToString(" "))
}

/**
 * 通过字符串获取枚举值
 */
fun <T : Enum<T>> Class<T>.getEnumOrNull(vararg name: Any): T? {
    name.forEach { Enums.getIfPresent(this, it.toString()).orNull()?.let { e -> return e } }
    return null
}

/**
 * 将字符串转换为可读的形式
 */
fun String?.toReadable(): String {
    val builder = StringBuilder()
    toString().toCharArray().forEachIndexed { index, c ->
        when {
            index == 0 -> builder.append(c.uppercaseChar())
            c.isUpperCase() -> builder.append(" $c")
            else -> builder.append(c)
        }
    }
    return builder.toString()
}

fun encodePos(d: Double): Long {
    return lfloor(d * 4096.0)
}

fun lfloor(value: Double): Long {
    val i = value.toLong()
    return if (value < i.toDouble()) i - 1L else i
}

fun ifloor(x: Double): Int {
    return NumberConversions.floor(x)
}

fun Location.modify(x: Double, y: Double, z: Double): Location {
    this.x = x
    this.y = y
    this.z = z
    return this
}

fun Location.modify(yaw: Float, pitch: Float): Location {
    this.yaw = yaw
    this.pitch = pitch
    return this
}