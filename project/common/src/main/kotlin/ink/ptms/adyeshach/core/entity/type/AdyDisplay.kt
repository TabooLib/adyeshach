package ink.ptms.adyeshach.core.entity.type

import org.bukkit.Color
import org.bukkit.entity.Display.Billboard
import org.bukkit.entity.Display.Brightness
import org.bukkit.util.Vector
import taboolib.common5.Quat

/**
 * @author sky
 * @date 2020/8/4 22:18
 */
interface AdyDisplay : AdyEntity {

    fun setInterpolationDelay(value: Int) {
        setMetadata("interpolationDelay", value)
    }

    fun getInterpolationDelay(): Int {
        return getMetadata("interpolationDelay")
    }

    fun setInterpolationDuration(value: Int) {
        setMetadata("interpolationDuration", value)
    }

    fun getInterpolationDuration(): Int {
        return getMetadata("interpolationDuration")
    }

    fun setTeleportInterpolationDuration(value: Int) {
        setMetadata("teleportInterpolationDuration", value)
    }

    fun getTeleportInterpolationDuration(): Int {
        return getMetadata("teleportInterpolationDuration")
    }

    fun setTranslation(value: Vector) {
        setMetadata("translation", value)
    }

    fun getTranslation(): Vector {
        return getMetadata("translation")
    }

    fun setScale(value: Vector) {
        setMetadata("scale", value)
    }

    fun getScale(): Vector {
        return getMetadata("scale")
    }

    fun setRotationLeft(value: Quat) {
        setMetadata("rotationLeft", value)
    }

    fun getRotationLeft(): Quat {
        return getMetadata("rotationLeft")
    }

    fun setRotationRight(value: Quat) {
        setMetadata("rotationRight", value)
    }

    fun getRotationRight(): Quat {
        return getMetadata("rotationRight")
    }

    fun setBillboardConstraints(value: Billboard) {
        setMetadata("billboardConstraints", value.ordinal.toByte())
    }

    fun getBillboardConstraints(): Byte {
        return getMetadata("billboardConstraints")
    }

    fun setBrightnessOverride(value: Brightness?) {
        setMetadata("brightnessOverride", if (value != null) value.blockLight shl 4 or (value.skyLight shl 20) else -1)
    }

    fun getBrightnessOverride(): Int {
        return getMetadata("brightnessOverride")
    }

    fun setViewRange(value: Float) {
        setMetadata("viewRange", value)
    }

    fun getViewRange(): Float {
        return getMetadata("viewRange")
    }

    fun setShadowRadius(value: Float) {
        setMetadata("shadowRadius", value)
    }

    fun getShadowRadius(): Float {
        return getMetadata("shadowRadius")
    }

    fun setShadowStrength(value: Float) {
        setMetadata("shadowStrength", value)
    }

    fun getShadowStrength(): Float {
        return getMetadata("shadowStrength")
    }

    fun setWidth(value: Float) {
        setMetadata("width", value)
    }

    fun getWidth(): Float {
        return getMetadata("width")
    }

    fun setHeight(value: Float) {
        setMetadata("height", value)
    }

    fun getHeight(): Float {
        return getMetadata("height")
    }

    fun setGlowColorOverride(value: Color) {
        setMetadata("glowColorOverride", value.asRGB())
    }

    fun getGlowColorOverride(): Color {
        return Color.fromRGB(getMetadata<Int>("glowColorOverride").let { if (it == -1) 0 else it })
    }
}