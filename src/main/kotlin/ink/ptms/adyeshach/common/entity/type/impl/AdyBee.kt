package ink.ptms.adyeshach.common.entity.type.impl

import ink.ptms.adyeshach.common.entity.MetadataExtend
import ink.ptms.adyeshach.common.entity.element.EntityProperties
import ink.ptms.adyeshach.common.entity.type.EntityTypes
import ink.ptms.adyeshach.api.nms.NMS
import io.izzel.taboolib.internal.gson.annotations.Expose
import org.bukkit.entity.Player

/**
 * @author Arasple
 * @date 2020/8/4 22:18
 */
class AdyBee(owner: Player) : AdyEntityLiving(owner, EntityTypes.BEE), MetadataExtend {

    init {
        properties = BeeProperties()
    }

    fun setFlip(anger: Boolean) {
        getProperties().isAnger = anger
        NMS.INSTANCE.updateEntityMetadata(owner, index, NMS.INSTANCE.getMetaEntityByte(16, baseData()))
    }

    fun isFlip(): Boolean {
        return getProperties().isAnger
    }

    fun setStung(stung: Boolean) {
        getProperties().hasStung = stung
        NMS.INSTANCE.updateEntityMetadata(owner, index, NMS.INSTANCE.getMetaEntityByte(16, baseData()))
    }

    fun hasStung(): Boolean {
        return getProperties().hasStung
    }

    fun setNectar(nectar: Boolean) {
        getProperties().hasNectar = nectar
        NMS.INSTANCE.updateEntityMetadata(owner, index, NMS.INSTANCE.getMetaEntityByte(16, baseData()))
    }

    fun hasNectar(): Boolean {
        return getProperties().hasNectar
    }

    fun setAngered(value: Boolean) {
        getProperties().angerTicks = if (value) 999 else 0
        NMS.INSTANCE.updateEntityMetadata(owner, index, NMS.INSTANCE.getMetaEntityInt(17, if (value) 999 else 0))
    }

    fun isAngered(): Boolean {
        return getProperties().angerTicks > 0
    }

    fun baseData(): Byte {
        return getProperties().run {
            var bits = 0
            if (unused) bits += 0x01
            if (isAnger) bits += 0x02
            if (hasStung) bits += 0x04
            if (hasNectar) bits += 0x08
            bits.toByte()
        }
    }

    override fun metadata(): List<Any> {
        return getProperties().run {
            listOf(
                    NMS.INSTANCE.getMetaEntityByte(16, baseData()),
                    NMS.INSTANCE.getMetaEntityInt(17, angerTicks),
            )
        }
    }

    private fun getProperties(): BeeProperties = properties as BeeProperties

    private class BeeProperties : EntityProperties() {

        @Expose
        var unused = false

        /**
         * 倒立
         */
        @Expose
        var isAnger = false

        /**
         * 是否已蜇过东西
         */
        @Expose
        var hasStung = false

        /**
         * 是否携带花蜜
         */
        @Expose
        var hasNectar = false

        /**
         * > 0 愤怒状态
         * <=0 取消愤怒状态, 需要手动更新
         */
        @Expose
        var angerTicks = 0
    }
}