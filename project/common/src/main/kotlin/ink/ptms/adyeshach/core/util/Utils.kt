package ink.ptms.adyeshach.core.util

import com.google.common.base.Enums
import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.event.AdyeshachItemHookEvent
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.util.NumberConversions
import taboolib.common.platform.function.console
import taboolib.common5.Demand
import taboolib.common5.cint
import taboolib.library.xseries.parseToMaterial
import taboolib.platform.util.modifyMeta

/**
 * 使用 AdyeshachLanguage 发送语言文件
 */
fun CommandSender.sendLang(node: String, vararg args: Any) {
    Adyeshach.api().getLanguage().sendLang(this, node, *args)
}

/**
 * 通过 AdyeshachLanguage 获取语言文件文本
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

fun Location.plus(x: Double = 0.0, y: Double = 0.0, z: Double = 0.0): Location {
    this.x += x
    this.y += y
    this.z += z
    return this
}

fun Location.modify(x: Double = this.x, y: Double = this.y, z: Double = this.z): Location {
    this.x = x
    this.y = y
    this.z = z
    return this
}

fun Location.modify(yaw: Float = this.yaw, pitch: Float = this.pitch): Location {
    this.yaw = yaw
    this.pitch = pitch
    return this
}

fun String.toItem(): ItemStack {
    val namespace = if (contains(":")) substringBefore(":") else "minecraft"
    val source = substringAfter(":")
    val itemStack = ItemStack(Material.STONE)
    if (namespace == "minecraft") {
        val demand = Demand(source)
        itemStack.type = demand.namespace.parseToMaterial()
        itemStack.amount = demand.get("amount")?.cint ?: 1
        itemStack.modifyMeta<ItemMeta> {
            demand.get("name")?.let { setDisplayName(it) }
            demand.get("lore")?.let { lore = it.split("\\n") }
            demand.get("model")?.let { setCustomModelData(it.cint) }
        }
    }
    val event = AdyeshachItemHookEvent(namespace, source, itemStack)
    event.call()
    return event.itemStack
}