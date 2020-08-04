package ink.ptms.adyeshach.common.entity.type.impl

import ink.ptms.adyeshach.common.entity.MetadataExtend
import ink.ptms.adyeshach.common.entity.element.EntityProperties
import ink.ptms.adyeshach.common.entity.type.EntityTypes
import ink.ptms.adyeshach.api.nms.NMS
import ink.ptms.adyeshach.common.bukkit.BukkitCreeperState
import io.izzel.taboolib.internal.gson.annotations.Expose
import org.bukkit.entity.Creeper
import org.bukkit.entity.Player

/**
 * @Author sky
 * @Since 2020-08-04 19:30
 */
class AdyCreeper(owner: Player) : AdyEntityLiving(owner, EntityTypes.CREEPER), MetadataExtend {

    init {
        properties = CreeperProperties()
    }

    fun setState(value: BukkitCreeperState) {
        getProperties().state = value
        NMS.INSTANCE.updateEntityMetadata(owner, index, NMS.INSTANCE.getMetaEntityInt(15, if (value == BukkitCreeperState.IDLE) -1 else 1))
    }

    fun getState(): BukkitCreeperState {
        return getProperties().state
    }

    fun setCharged(value: Boolean) {
        getProperties().charged = value
        NMS.INSTANCE.updateEntityMetadata(owner, index, NMS.INSTANCE.getMetaEntityBoolean(16, value))
    }

    fun isCharged(): Boolean {
        return getProperties().charged
    }

    fun setIgnited(value: Boolean) {
        getProperties().ignited = value
        NMS.INSTANCE.updateEntityMetadata(owner, index, NMS.INSTANCE.getMetaEntityBoolean(17, value))
    }

    fun isIgnited(): Boolean {
        return getProperties().ignited
    }

    override fun metadata(): List<Any> {
        return getProperties().run {
            listOf(
                    NMS.INSTANCE.getMetaEntityInt(15, if (state == BukkitCreeperState.IDLE) -1 else 1),
                    NMS.INSTANCE.getMetaEntityBoolean(16, charged),
                    NMS.INSTANCE.getMetaEntityBoolean(17, ignited)
            )
        }
    }

    private fun getProperties(): CreeperProperties = properties as CreeperProperties

    private class CreeperProperties : EntityProperties() {

        @Expose
        var state = BukkitCreeperState.IDLE
        @Expose
        var charged = false
        @Expose
        var ignited = false
    }
}