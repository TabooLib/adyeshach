package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.editor.Editors
import ink.ptms.adyeshach.common.entity.EntityThrowable
import ink.ptms.adyeshach.common.entity.EntityTypes
import org.bukkit.Color
import org.bukkit.entity.Arrow

/**
 * @Author sky
 * @Since 2020-08-04 19:30
 */
class AdyArrow : AdyEntity(EntityTypes.ARROW), EntityThrowable {

    /**
     * 1.15 Index=7 一致
     *              Type       Meaning       Def
     *      Index=8 OptUUID  Shooter UUID  Empty
     *      Index=9 Byte    Peircing level   0
     *
     * 1.13 Index=6 isCritical 0x01
     *      Index=7 OptUUID  Shooter UUID  Empty
     *
     * 1.12 Index=6 isCritical 0x01
     *
     * 1.9 Index=5 isCritical 0x01
     */

    init {
        registerMetaByteMask(at(11600 to 7, 11000 to 6, 10900 to 5), "isCritical", 0x01)
        registerMetaByteMask(at(11400 to 7), "noclip", 0x02)
        registerMeta(at(11600 to 8, 11500 to 9), "piercingLevel", 0.toByte())
        registerMeta(at(11600 to 9), "color", -1)
                .from(Editors.COLOR)
                .build()
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