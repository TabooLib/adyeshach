package ink.ptms.adyeshach.common.entity.type.impl

import ink.ptms.adyeshach.common.bukkit.BukkitParticles
import ink.ptms.adyeshach.common.entity.MetadataExtend
import ink.ptms.adyeshach.common.entity.element.EntityProperties
import ink.ptms.adyeshach.common.entity.type.EntityTypes
import ink.ptms.adyeshach.nms.NMS
import io.izzel.taboolib.internal.gson.annotations.Expose
import org.bukkit.Color
import org.bukkit.entity.Player

/**
 * @Author sky
 * @Since 2020-08-04 19:30
 */
class AdyArrow(owner: Player) : AdyEntity(owner, EntityTypes.ARROW), MetadataExtend {

    init {
        properties = ArrowProperties()
    }

    fun setCritical(value: Boolean) {
        getProperties().critical = value
        NMS.INSTANCE.updateEntityMetadata(owner, index, NMS.INSTANCE.getMetaEntityByte(7, baseData()))
    }

    fun isCritical(): Boolean {
        return getProperties().critical
    }

    fun setNoclip(value: Boolean) {
        getProperties().critical = value
        NMS.INSTANCE.updateEntityMetadata(owner, index, NMS.INSTANCE.getMetaEntityByte(7, baseData()))
    }

    fun isNoclip(): Boolean {
        return getProperties().noclip
    }

    fun setPiercingLevel(value: Byte) {
        getProperties().piercingLevel = value
        NMS.INSTANCE.updateEntityMetadata(owner, index, NMS.INSTANCE.getMetaEntityByte(8, value))
    }

    fun getPiercingLevel(): Byte {
        return getProperties().piercingLevel
    }

    fun setColor(value: Color) {
        getProperties().color = value.asRGB()
        NMS.INSTANCE.updateEntityMetadata(owner, index, NMS.INSTANCE.getMetaEntityInt(9, value.asRGB()))
    }

    fun getColor(): Color {
        return Color.fromRGB(getProperties().color)
    }

    fun baseData(): Byte {
        return getProperties().run {
            var bits = 0
            if (critical) bits += 0x01
            if (noclip) bits += 0x02
            bits.toByte()
        }
    }

    override fun metadata(): List<Any> {
        return getProperties().run {
            listOf(
                    NMS.INSTANCE.getMetaEntityByte(7, baseData()),
                    NMS.INSTANCE.getMetaEntityByte(8, piercingLevel),
                    NMS.INSTANCE.getMetaEntityInt(9, color)
            )
        }
    }

    private fun getProperties(): ArrowProperties = properties as ArrowProperties

    private class ArrowProperties : EntityProperties() {

        @Expose
        var critical = false
        @Expose
        var noclip = false
        @Expose
        var piercingLevel: Byte = 0
        @Expose
        var color = -1
    }
}