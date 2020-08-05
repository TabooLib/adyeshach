package ink.ptms.adyeshach.common.entity.type.impl

import ink.ptms.adyeshach.common.entity.type.EntityTypes
import ink.ptms.adyeshach.api.nms.NMS
import io.izzel.taboolib.internal.gson.annotations.Expose
import org.bukkit.Color
import org.bukkit.entity.Player

/**
 * @Author sky
 * @Since 2020-08-04 19:30
 */
class AdyArrow(owner: Player) : AdyEntity(owner, EntityTypes.ARROW) {

    init {
        registerMetaByteMask(7, "isCritical", 0x01)
        registerMetaByteMask(7, "noclip", 0x02)
        registerMeta(8, "piercingLevel", 0.toByte())
        registerMeta(9, "color", -1)
    }

    fun setCritical(value: Boolean) {
        setMetadata("isCritical", value)
    }

    fun isCritical(): Boolean {
        return getMetadata("isCritical")
    }

    fun setNoclip(value: Boolean) {
        setMetadata("noclip", value)
    }

    fun isNoclip(): Boolean {
        return getMetadata("noclip")
    }

    fun setPiercingLevel(value: Byte) {
        setMetadata("piercingLevel", value)
    }

    fun getPiercingLevel(): Byte {
        return getMetadata("piercingLevel")
    }

    fun setColor(value: Color) {
        setMetadata("color", value.asRGB())
    }

    fun getColor(): Color {
        return Color.fromRGB(getMetadata("color"))
    }
}