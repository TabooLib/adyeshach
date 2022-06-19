package ink.ptms.adyeshach.impl.entity

import ink.ptms.adyeshach.common.bukkit.BukkitPose
import ink.ptms.adyeshach.common.entity.GenericEntity

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.entity.DefaultGenericEntity
 *
 * @author 坏黑
 * @since 2022/6/19 21:58
 */
interface DefaultGenericEntity : GenericEntity {

    override var ticksFrozenInPowderedSnow: Int
        get() = TODO("Not yet implemented")
        set(value) {}

    override fun getDisplayName(): String {
        TODO("Not yet implemented")
    }

    override fun isFired(): Boolean {
        TODO("Not yet implemented")
    }

    override fun isSneaking(): Boolean {
        TODO("Not yet implemented")
    }

    override fun isSprinting(): Boolean {
        TODO("Not yet implemented")
    }

    override fun isSwimming(): Boolean {
        TODO("Not yet implemented")
    }

    override fun isInvisible(): Boolean {
        TODO("Not yet implemented")
    }

    override fun isGlowing(): Boolean {
        TODO("Not yet implemented")
    }

    override fun isFlyingElytra(): Boolean {
        TODO("Not yet implemented")
    }

    override fun isNoGravity(): Boolean {
        TODO("Not yet implemented")
    }

    override fun setFired(onFire: Boolean) {
        TODO("Not yet implemented")
    }

    override fun setSneaking(sneaking: Boolean) {
        TODO("Not yet implemented")
    }

    override fun setSprinting(sprinting: Boolean) {
        TODO("Not yet implemented")
    }

    override fun setSwimming(swimming: Boolean) {
        TODO("Not yet implemented")
    }

    override fun setInvisible(invisible: Boolean) {
        TODO("Not yet implemented")
    }

    override fun setGlowing(glowing: Boolean) {
        TODO("Not yet implemented")
    }

    override fun setFlyingElytra(flyingElytra: Boolean) {
        TODO("Not yet implemented")
    }

    override fun setNoGravity(noGravity: Boolean) {
        TODO("Not yet implemented")
    }

    override fun setCustomNameVisible(value: Boolean) {
        TODO("Not yet implemented")
    }

    override fun isCustomNameVisible(): Boolean {
        TODO("Not yet implemented")
    }

    override fun setCustomName(value: String) {
        TODO("Not yet implemented")
    }

    override fun getCustomName(): String {
        TODO("Not yet implemented")
    }

    override fun setPose(pose: BukkitPose) {
        TODO("Not yet implemented")
    }

    override fun getPose(): BukkitPose {
        TODO("Not yet implemented")
    }
}