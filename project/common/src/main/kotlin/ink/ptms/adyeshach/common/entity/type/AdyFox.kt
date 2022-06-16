package ink.ptms.adyeshach.common.entity.type

import org.bukkit.entity.Fox

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
@Suppress("SpellCheckingInspection")
abstract class AdyFox : AdyEntityAgeable() {

    open fun getType(): Fox.Type {
        return Fox.Type.values()[getMetadata("type")]
    }

    open fun setType(type: Fox.Type) {
        setMetadata("type", type)
    }

    open fun isSitting(): Boolean {
        return getMetadata("isSitting")
    }

    open fun setSitting(value: Boolean) {
        setMetadata("isSitting", value)
    }

    open fun isCrouching(): Boolean {
        return getMetadata("isCrouching")
    }

    open fun setCrouching(value: Boolean) {
        setMetadata("isCrouching", value)
    }

    open fun isInterested(): Boolean {
        return getMetadata("isInterested")
    }

    open fun setInterested(value: Boolean) {
        setMetadata("isInterested", value)
    }

    open fun isPouncing(): Boolean {
        return getMetadata("isPouncing")
    }

    open fun setPouncing(value: Boolean) {
        setMetadata("isPouncing", value)
    }

    open fun isSleeping(): Boolean {
        return getMetadata("isSleeping")
    }

    open fun setSleeping(value: Boolean) {
        setMetadata("isSleeping", value)
    }

    open fun isFaceplanted(): Boolean {
        return getMetadata("isFaceplanted")
    }

    open fun setFaceplanted(value: Boolean) {
        setMetadata("isFaceplanted", value)
    }

    open fun isDefending(): Boolean {
        return getMetadata("isDefending")
    }

    open fun setDefending(value: Boolean) {
        setMetadata("isDefending", value)
    }
}