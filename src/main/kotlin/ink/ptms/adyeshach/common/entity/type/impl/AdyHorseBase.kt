package ink.ptms.adyeshach.common.entity.type.impl

import ink.ptms.adyeshach.common.entity.type.EntityTypes
import ink.ptms.adyeshach.common.entity.element.EntityProperties
import ink.ptms.adyeshach.api.nms.NMS
import org.bukkit.entity.Player

/**
 * @Author sky
 * @Since 2020-08-04 18:28
 */
abstract class AdyHorseBase(owner: Player, entityTypes: EntityTypes) : AdyEntityAgeable(owner, entityTypes) {

    fun setTamed(value: Boolean) {
        getProperties().tamed = value
        NMS.INSTANCE.updateEntityMetadata(owner, index, NMS.INSTANCE.getMetaEntityByte(16, baseData()))
    }

    fun isTamed(): Boolean {
        return getProperties().tamed
    }

    fun setSaddled(value: Boolean) {
        getProperties().saddled = value
        NMS.INSTANCE.updateEntityMetadata(owner, index, NMS.INSTANCE.getMetaEntityByte(16, baseData()))
    }

    fun isSaddled(): Boolean {
        return getProperties().saddled
    }

    fun setHasBred(value: Boolean) {
        getProperties().bred = value
        NMS.INSTANCE.updateEntityMetadata(owner, index, NMS.INSTANCE.getMetaEntityByte(16, baseData()))
    }

    fun isHasBred(): Boolean {
        return getProperties().bred
    }

    fun setEating(value: Boolean) {
        getProperties().eating = value
        NMS.INSTANCE.updateEntityMetadata(owner, index, NMS.INSTANCE.getMetaEntityByte(16, baseData()))
    }

    fun isEating(): Boolean {
        return getProperties().eating
    }

    fun setRearing(value: Boolean) {
        getProperties().rearing = value
        NMS.INSTANCE.updateEntityMetadata(owner, index, NMS.INSTANCE.getMetaEntityByte(16, baseData()))
    }

    fun isRearing(): Boolean {
        return getProperties().rearing
    }

    fun setMouthOpen(value: Boolean) {
        getProperties().mouthOpen = value
        NMS.INSTANCE.updateEntityMetadata(owner, index, NMS.INSTANCE.getMetaEntityByte(16, baseData()))
    }

    fun isMouthOpen(): Boolean {
        return getProperties().mouthOpen
    }

    fun baseData(): Byte {
        return getProperties().run {
            var bits = 0
            if (tamed) bits += 0x02
            if (saddled) bits += 0x04
            if (bred) bits += 0x08
            if (eating) bits += 0x10
            if (rearing) bits += 0x20
            if (mouthOpen) bits += 0x40
            bits.toByte()
        }
    }

    override fun metadata(): List<Any> {
        return getProperties().run {
            listOf(
                    *super.metadata().toTypedArray(),
                    NMS.INSTANCE.getMetaEntityByte(16, baseData())
            )
        }
    }

    private fun getProperties(): EntityProperties.Horse = properties as EntityProperties.Horse
}