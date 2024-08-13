package ink.ptms.adyeshach.core.util

import com.google.common.base.Enums
import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.event.AdyeshachItemHookEvent
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.function.console
import taboolib.common.platform.function.submit
import taboolib.common5.Demand
import taboolib.common5.cint
import taboolib.library.xseries.parseToMaterial
import taboolib.platform.util.buildItem

/**
 * 使用 AdyeshachLanguage 发送语言文件
 */
fun CommandSender.sendLang(node: String, vararg args: Any) {
    Adyeshach.api().getLanguage().sendLang(this, node, *args)
}

/**
 * 使用 AdyeshachLanguage 获取语言文件
 */
fun CommandSender.asLang(node: String, vararg args: Any): String? {
    return Adyeshach.api().getLanguage().getLang(this, node, *args)
}

/**
 * 使用 AdyeshachLanguage 获取语言文件
 */
fun CommandSender.asLangList(node: String, vararg args: Any): List<String> {
    return Adyeshach.api().getLanguage().getLangList(this, node, *args)
}

/**
 * 通过 AdyeshachLanguage 获取语言文件文本
 */
fun asLang(node: String, vararg args: Any) = Adyeshach.api().getLanguage().getLang(console().cast(), node, *args) ?: node

/**
 * 通过 AdyeshachLanguage 获取语言文件文本
 */
fun asLangList(node: String, vararg args: Any) = Adyeshach.api().getLanguage().getLangList(console().cast(), node, *args)

/**
 * 使用语言文件文本抛出异常
 */
fun errorBy(node: String, vararg args: Any): Nothing = error(Adyeshach.api().getLanguage().getLang(console().cast(), node, *args) ?: node)

/**
 * 安全测距
 */
fun Location.safeDistance(loc: Location): Double {
    return if (world != null && world?.name == loc.world?.name) distance(loc) else Double.MAX_VALUE
}

/**
 * 安全测距，并忽略 Y 轴
 */
fun Location.safeDistanceIgnoreY(loc: Location): Double {
    return if (world != null && world?.name == loc.world?.name) distance(Location(world, loc.x, y, loc.z)) else Double.MAX_VALUE
}

/**
 * 通过字符串获取枚举值
 */
fun <T : Enum<T>> Class<T>.getEnum(vararg name: Any): T {
    name.forEach { Enums.getIfPresent(this, it.toString().uppercase()).orNull()?.let { e -> return e } }
    errorBy("error-unable-to-find-enum", simpleName, name.joinToString(" "))
}

/**
 * 通过字符串获取枚举值
 */
fun <T : Enum<T>> Class<T>.getEnumOrNull(vararg name: Any): T? {
    name.forEach { Enums.getIfPresent(this, it.toString().uppercase()).orNull()?.let { e -> return e } }
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

fun ifloor(value: Double): Int {
    val i = value.toInt()
    return if (value < i.toDouble()) i - 1 else i
}

fun lfloor(value: Double): Long {
    val i = value.toLong()
    return if (value < i.toDouble()) i - 1L else i
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
    var itemStack = ItemStack(Material.STONE)
    val demand = Demand(source)
    itemStack.type = demand.namespace.parseToMaterial()
    itemStack.amount = demand.get("amount")?.cint ?: 1
    itemStack = buildItem(itemStack) {
        // 名称
        demand.get("name")?.let { name = it }
        // 描述
        demand.get("lore")?.let { lore.addAll(it.split("\\n")) }
        // 耐久
        demand.get("data")?.let { damage = it.cint }
        // 模型
        demand.get("model")?.let { customModelData = it.cint }
        // 颜色
        demand.get("color")?.let {
            val args = it.split(",")
            color = Color.fromRGB(args.getOrNull(0).cint, args.getOrNull(1).cint, args.getOrNull(2).cint)
        }
        // 附魔效果
        if (demand.tags.contains("shiny")) {
            shiny()
        }
        // 无法破坏
        if (demand.tags.contains("unbreakable")) {
            isUnbreakable = true
        }
    }
    if (namespace == "minecraft") {
        return itemStack
    }
    val event = AdyeshachItemHookEvent(namespace, source, itemStack)
    event.call()
    return event.itemStack
}

fun submitRepeat(times: Int, async: Boolean = false, func: Runnable) {
    var i = 0
    submit(async = async, period = 1) {
        if (i++ < times) {
            func.run()
        } else {
            cancel()
        }
    }
}

fun rgb(r: Int, g: Int, b: Int): Int {
    return (r shl 16) or (g shl 8) or b
}

fun argb(a: Int, r: Int, g: Int, b: Int): Int {
    return a shl 24 or (r shl 16) or (g shl 8) or b
}