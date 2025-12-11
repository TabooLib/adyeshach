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
open class GenericEntityHandler(protected val self: DefaultEntityInstance) {

    open var ticksFrozenInPowderedSnow: Int
        get() = self.getMetadata("ticksFrozenInPowderedSnow")
        set(value) { self.setMetadata("ticksFrozenInPowderedSnow", value) }

    open fun getDisplayName(): String {
        return when (self) {
            is AdyHuman -> self.getName()
            is AdyTextDisplay -> self.getText().toPlainText()
            else -> getCustomName().ifEmpty { self.entityType.name.lowercase().toReadable() }
        }
    }

    open fun isFired(): Boolean = self.getMetadata("onFire")
    open fun isSneaking(): Boolean = self.getMetadata("isCrouched")
    open fun isSprinting(): Boolean = self.getMetadata("isSprinting")
    open fun isSwimming(): Boolean = self.getMetadata("isSwimming")
    open fun isInvisible(): Boolean = self.getMetadata("isInvisible")
    open fun isGlowing(): Boolean = self.getMetadata("isGlowing")
    open fun isFlyingElytra(): Boolean = self.getMetadata("isFlyingElytra")
    open fun isNoGravity(): Boolean = self.getMetadata("noGravity")

    open fun setFired(onFire: Boolean) { self.setMetadata("onFire", onFire) }
    open fun setSneaking(sneaking: Boolean) { self.setMetadata("isCrouched", sneaking) }
    open fun setSprinting(sprinting: Boolean) { self.setMetadata("isSprinting", sprinting) }
    open fun setSwimming(swimming: Boolean) { self.setMetadata("isSwimming", swimming) }
    open fun setInvisible(invisible: Boolean) { self.setMetadata("isInvisible", invisible) }
    open fun setGlowing(glowing: Boolean) { self.setMetadata("isGlowing", glowing) }
    open fun setFlyingElytra(flyingElytra: Boolean) { self.setMetadata("isFlyingElytra", flyingElytra) }
    open fun setNoGravity(noGravity: Boolean) { self.setMetadata("noGravity", noGravity) }

    open fun setCustomNameVisible(value: Boolean) { self.setMetadata("isCustomNameVisible", value) }
    open fun isCustomNameVisible(): Boolean = self.getMetadata("isCustomNameVisible")

    open fun setCustomName(value: String) { self.setMetadata("customName", value) }
    open fun getCustomName(): String = Components.toLegacyText(self.getMetadata("customName"))
    open fun getCustomNameRaw(): String = self.getMetadata("customName")

    open fun setPose(pose: BukkitPose) { self.setMetadata("pose", pose) }
    open fun getPose(): BukkitPose = self.getMetadata("pose")
}
