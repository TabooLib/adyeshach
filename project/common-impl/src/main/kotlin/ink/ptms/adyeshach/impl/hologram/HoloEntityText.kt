package ink.ptms.adyeshach.impl.hologram

import ink.ptms.adyeshach.core.AdyeshachHologram
import ink.ptms.adyeshach.core.entity.EntityTypes
import ink.ptms.adyeshach.core.entity.type.AdyArmorStand

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.hologram.HoloEntityText
 *
 * @author 坏黑
 * @since 2022/12/14 13:38
 */
class HoloEntityText(text: String, space: Double) : HoloEntity<AdyArmorStand>(space), AdyeshachHologram.ItemByText {

    override val type = EntityTypes.ARMOR_STAND
    override var text = text
        set(value) {
            if (field != value) {
                field = value
                entity?.setCustomName(field)
                entity?.setCustomNameVisible(text.isNotEmpty())
            }
        }

    override fun prepareSpawn(entity: AdyArmorStand) {
        entity.setSmall(true)
        entity.setMarker(true)
        entity.setBasePlate(false)
        entity.setInvisible(true)
        entity.setCustomName(text)
        entity.setCustomNameVisible(text.isNotEmpty())
    }
}