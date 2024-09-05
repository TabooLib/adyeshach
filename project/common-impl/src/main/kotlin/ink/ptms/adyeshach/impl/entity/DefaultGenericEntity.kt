package ink.ptms.adyeshach.impl.entity

import ink.ptms.adyeshach.core.bukkit.BukkitPose
import ink.ptms.adyeshach.core.entity.EntityBase
import ink.ptms.adyeshach.core.entity.GenericEntity
import ink.ptms.adyeshach.core.entity.Metaable
import ink.ptms.adyeshach.core.entity.type.AdyHuman
import ink.ptms.adyeshach.core.entity.type.AdyTextDisplay
import ink.ptms.adyeshach.core.util.Components
import ink.ptms.adyeshach.core.util.toReadable

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.entity.DefaultGenericEntity
 *
 * @author 坏黑
 * @since 2022/6/19 21:58
 */
interface DefaultGenericEntity : GenericEntity {

    override var ticksFrozenInPowderedSnow: Int
        get() {
            this as Metaable
            return getMetadata("ticksFrozenInPowderedSnow")
        }
        set(value) {
            this as Metaable
            setMetadata("ticksFrozenInPowderedSnow", value)
        }

    override fun getDisplayName(): String {
        return when (this) {
            is AdyHuman -> getName()
            is AdyTextDisplay -> getText().toPlainText()
            is EntityBase -> getCustomName().ifEmpty { entityType.name.lowercase().toReadable() }
            else -> error("Unknown entity type.")
        }
    }

    override fun isFired(): Boolean {
        this as Metaable
        return getMetadata("onFire")
    }

    override fun isSneaking(): Boolean {
        this as Metaable
        return getMetadata("isCrouched")
    }

    override fun isSprinting(): Boolean {
        this as Metaable
        return getMetadata("isSprinting")
    }

    override fun isSwimming(): Boolean {
        this as Metaable
        return getMetadata("isSwimming")
    }

    override fun isInvisible(): Boolean {
        this as Metaable
        return getMetadata("isInvisible")
    }

    override fun isGlowing(): Boolean {
        this as Metaable
        return getMetadata("isGlowing")
    }

    override fun isFlyingElytra(): Boolean {
        this as Metaable
        return getMetadata("isFlyingElytra")
    }

    override fun isNoGravity(): Boolean {
        this as Metaable
        return getMetadata("noGravity")
    }

    override fun setFired(onFire: Boolean) {
        this as Metaable
        setMetadata("onFire", onFire)
    }

    override fun setSneaking(sneaking: Boolean) {
        this as Metaable
        setMetadata("isCrouched", sneaking)
    }

    override fun setSprinting(sprinting: Boolean) {
        this as Metaable
        setMetadata("isSprinting", sprinting)
    }

    override fun setSwimming(swimming: Boolean) {
        this as Metaable
        setMetadata("isSwimming", swimming)
    }

    override fun setInvisible(invisible: Boolean) {
        this as Metaable
        setMetadata("isInvisible", invisible)
    }

    override fun setGlowing(glowing: Boolean) {
        this as Metaable
        setMetadata("isGlowing", glowing)
    }

    override fun setFlyingElytra(flyingElytra: Boolean) {
        this as Metaable
        setMetadata("isFlyingElytra", flyingElytra)
    }

    override fun setNoGravity(noGravity: Boolean) {
        this as Metaable
        setMetadata("noGravity", noGravity)
    }

    override fun setCustomNameVisible(value: Boolean) {
        this as Metaable
        setMetadata("isCustomNameVisible", value)
    }

    override fun isCustomNameVisible(): Boolean {
        this as Metaable
        return getMetadata("isCustomNameVisible")
    }

    override fun setCustomName(value: String) {
        this as Metaable
        setMetadata("customName", value)
    }

    override fun getCustomName(): String {
        this as Metaable
        return Components.toLegacyText(getMetadata("customName"))
    }

    override fun getCustomNameRaw(): String {
        this as Metaable
        return getMetadata("customName")
    }

    override fun setPose(pose: BukkitPose) {
        this as Metaable
        setMetadata("pose", pose)
    }

    override fun getPose(): BukkitPose {
        this as Metaable
        return getMetadata("pose")
    }
}