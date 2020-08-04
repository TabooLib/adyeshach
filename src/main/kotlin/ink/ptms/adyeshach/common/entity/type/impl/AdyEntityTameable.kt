package ink.ptms.adyeshach.common.entity.type.impl

import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.MetadataExtend
import ink.ptms.adyeshach.common.entity.type.EntityTypes
import ink.ptms.adyeshach.common.entity.element.EntityPosition
import ink.ptms.adyeshach.common.entity.element.EntityProperties
import ink.ptms.adyeshach.nms.NMS
import io.izzel.taboolib.internal.gson.annotations.Expose
import net.minecraft.server.v1_16_R1.EntityLiving
import org.bukkit.DyeColor
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Cat
import org.bukkit.entity.Player
import java.util.*

/**
 * @Author sky
 * @Since 2020-08-04 18:28
 */
abstract class AdyEntityTameable(owner: Player, entityTypes: EntityTypes) : AdyEntityAgeable(owner, entityTypes) {

    fun setSitting(value: Boolean) {
        getProperties().sitting = value
        NMS.INSTANCE.updateEntityMetadata(owner, index, NMS.INSTANCE.getMetaEntityByte(16, baseData()))
    }

    fun isSitting(): Boolean {
        return getProperties().sitting
    }

    fun setAngry(value: Boolean) {
        getProperties().angry = value
        NMS.INSTANCE.updateEntityMetadata(owner, index, NMS.INSTANCE.getMetaEntityByte(16, baseData()))
    }

    fun isAngry(): Boolean {
        return getProperties().angry
    }

    fun setTamed(value: Boolean) {
        getProperties().tamed = value
        NMS.INSTANCE.updateEntityMetadata(owner, index, NMS.INSTANCE.getMetaEntityByte(16, baseData()))
    }

    fun isTamed(): Boolean {
        return getProperties().tamed
    }

    fun baseData(): Byte {
        return getProperties().run {
            var bits = 0
            if (sitting) bits += 0x01
            if (angry) bits += 0x02
            if (tamed) bits += 0x04
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

    private fun getProperties(): EntityProperties.Tameable = properties as EntityProperties.Tameable
}