package ink.ptms.adyeshach.common.entity.type

import taboolib.module.nms.MinecraftVersion

val minecraftVersion: Int
    get() = MinecraftVersion.majorLegacy

fun assert(condition: Boolean, metadata: String) {
    if (condition) error("Method \"$metadata\" not supported for this minecraft version.")
}

fun assert(condition: Boolean, metadata: String, replace: String) {
    if (condition) error("Method \"$metadata\" not supported for this minecraft version, Use \"$replace\" instead.")
}