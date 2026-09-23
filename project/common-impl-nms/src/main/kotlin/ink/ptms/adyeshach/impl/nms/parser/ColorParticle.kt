package ink.ptms.adyeshach.impl.nms.parser

import ink.ptms.adyeshach.core.MinecraftMeta
import ink.ptms.adyeshach.core.MinecraftMetadataParser
import ink.ptms.adyeshach.impl.nms.DefaultMeta
import ink.ptms.adyeshach.impl.nms.specific.NMS21
import org.bukkit.Color
import taboolib.common5.cint

class ColorParticle : MinecraftMetadataParser<Any>() {
    override fun parse(value: Any): Any {
        return if (value is Color) value.asRGB() else value.cint
    }

    override fun createMeta(index: Int, value: Any): MinecraftMeta {
        return DefaultMeta(
            if (value == 0) {
                NMS21.instance.createColorParticle(index)
            } else {
                NMS21.instance.createColorParticle(index, Color.fromRGB(value.cint))
            }
        )
    }
}