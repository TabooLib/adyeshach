package ink.ptms.adyeshach.compat.modelengine4

import com.ticxo.modelengine.api.entity.Dummy
import com.ticxo.modelengine.api.nms.entity.EntityHandler
import ink.ptms.adyeshach.core.entity.EntityInstance
import ink.ptms.adyeshach.core.entity.StandardTags
import ink.ptms.adyeshach.core.event.AdyeshachEntityDamageEvent
import ink.ptms.adyeshach.core.event.AdyeshachEntityInteractEvent
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.util.Vector

/**
 * Adyeshach
 * ink.ptms.adyeshach.compat.modelengine3.EntityModeled
 *
 * @author 坏黑
 * @since 2024/1/14 22:30
 */
internal class EntityModeled(val entity: EntityInstance) : Dummy<EntityInstance>(entity.index, entity.normalizeUniqueId, entity) {

    override fun hurt(human: HumanEntity?, p1: Any, p2: Float): Boolean {
        if (human is Player) {
            AdyeshachEntityDamageEvent(entity, human).call()
            return true
        }
        return false
    }

    override fun interact(human: HumanEntity, slot: EquipmentSlot): EntityHandler.InteractionResult {
        val isMainHand = slot == EquipmentSlot.HAND
        val clickedPos = Vector(0, 0, 0)
        AdyeshachEntityInteractEvent(entity, human as Player, isMainHand, clickedPos).call()
        return EntityHandler.InteractionResult.PASS
    }

    override fun getLocation(): Location {
        return entity.getLocation()
    }

    override fun isVisible(): Boolean {
        return !entity.isInvisible()
    }

    override fun setVisible(p0: Boolean) {
        entity.setInvisible(!p0)
    }

    override fun isRemoved(): Boolean {
        return entity.isRemoved || entity.manager?.isValid() != true
    }

    override fun isAlive(): Boolean {
        return !isRemoved()
    }

    override fun getRenderRadius(): Int {
        return entity.visibleDistance.toInt()
    }

    override fun setRenderRadius(p0: Int) {
        entity.visibleDistance = p0.toDouble()
    }

    override fun isWalking(): Boolean {
        return entity.hasTag(StandardTags.IS_MOVING)
    }

    override fun isGlowing(): Boolean {
        return entity.isGlowing()
    }

    override fun getGlowColor(): Int {
        return Color.WHITE.asRGB()
    }
}