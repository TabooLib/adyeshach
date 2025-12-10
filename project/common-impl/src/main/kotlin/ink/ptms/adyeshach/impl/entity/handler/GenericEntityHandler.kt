package ink.ptms.adyeshach.impl.entity.handler

import ink.ptms.adyeshach.core.bukkit.BukkitPose
import ink.ptms.adyeshach.core.entity.type.AdyHuman
import ink.ptms.adyeshach.core.entity.type.AdyTextDisplay
import ink.ptms.adyeshach.core.util.Components
import ink.ptms.adyeshach.core.util.toReadable
import ink.ptms.adyeshach.impl.entity.DefaultEntityInstance

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.entity.handler.GenericEntityHandler
 *
 * 负责实体的通用属性处理（状态、姿态、名称等）
 *
 * @author 坏黑
 * @since 2022/6/19
 */
class GenericEntityHandler(private val self: DefaultEntityInstance) {

    var ticksFrozenInPowderedSnow: Int
        get() = self.getMetadata("ticksFrozenInPowderedSnow")
        set(value) { self.setMetadata("ticksFrozenInPowderedSnow", value) }

    fun getDisplayName(): String {
        return when (self) {
            is AdyHuman -> self.getName()
            is AdyTextDisplay -> self.getText().toPlainText()
            else -> getCustomName().ifEmpty { self.entityType.name.lowercase().toReadable() }
        }
    }

    fun isFired(): Boolean = self.getMetadata("onFire")
    fun isSneaking(): Boolean = self.getMetadata("isCrouched")
    fun isSprinting(): Boolean = self.getMetadata("isSprinting")
    fun isSwimming(): Boolean = self.getMetadata("isSwimming")
    fun isInvisible(): Boolean = self.getMetadata("isInvisible")
    fun isGlowing(): Boolean = self.getMetadata("isGlowing")
    fun isFlyingElytra(): Boolean = self.getMetadata("isFlyingElytra")
    fun isNoGravity(): Boolean = self.getMetadata("noGravity")

    fun setFired(onFire: Boolean) { self.setMetadata("onFire", onFire) }
    fun setSneaking(sneaking: Boolean) { self.setMetadata("isCrouched", sneaking) }
    fun setSprinting(sprinting: Boolean) { self.setMetadata("isSprinting", sprinting) }
    fun setSwimming(swimming: Boolean) { self.setMetadata("isSwimming", swimming) }
    fun setInvisible(invisible: Boolean) { self.setMetadata("isInvisible", invisible) }
    fun setGlowing(glowing: Boolean) { self.setMetadata("isGlowing", glowing) }
    fun setFlyingElytra(flyingElytra: Boolean) { self.setMetadata("isFlyingElytra", flyingElytra) }
    fun setNoGravity(noGravity: Boolean) { self.setMetadata("noGravity", noGravity) }

    fun setCustomNameVisible(value: Boolean) { self.setMetadata("isCustomNameVisible", value) }
    fun isCustomNameVisible(): Boolean = self.getMetadata("isCustomNameVisible")

    fun setCustomName(value: String) { self.setMetadata("customName", value) }
    fun getCustomName(): String = Components.toLegacyText(self.getMetadata("customName"))
    fun getCustomNameRaw(): String = self.getMetadata("customName")

    fun setPose(pose: BukkitPose) { self.setMetadata("pose", pose) }
    fun getPose(): BukkitPose = self.getMetadata("pose")
}
