package ink.ptms.adyeshach.core.entity.type

import org.bukkit.entity.Fox

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
interface AdyFox : AdyEntityAgeable {

    fun getType(): Fox.Type {
        return Fox.Type.values()[getMetadata("type")]
    }

    fun setType(type: Fox.Type) {
        setMetadata("type", type)
    }

    fun isSitting(): Boolean {
        return getMetadata("isSitting")
    }

    fun setSitting(value: Boolean) {
        setMetadata("isSitting", value)
    }

    fun isCrouching(): Boolean {
        return getMetadata("isCrouching")
    }

    fun setCrouching(value: Boolean) {
        setMetadata("isCrouching", value)
    }

    fun isInterested(): Boolean {
        return getMetadata("isInterested")
    }

    fun setInterested(value: Boolean) {
        setMetadata("isInterested", value)
    }

    fun isPouncing(): Boolean {
        return getMetadata("isPouncing")
    }

    fun setPouncing(value: Boolean) {
        setMetadata("isPouncing", value)
    }

    fun isSleeping(): Boolean {
        return getMetadata("isSleeping")
    }

    fun setSleeping(value: Boolean) {
        setMetadata("isSleeping", value)
    }

    fun isFaceplanted(): Boolean {
        return getMetadata("isFaceplanted")
    }

    fun setFaceplanted(value: Boolean) {
        setMetadata("isFaceplanted", value)
    }

    fun isDefending(): Boolean {
        return getMetadata("isDefending")
    }

    fun setDefending(value: Boolean) {
        setMetadata("isDefending", value)
    }
}