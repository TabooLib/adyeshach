package ink.ptms.adyeshach.common.entity.type

import org.bukkit.material.MaterialData

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
abstract class AdyEnderman : AdyMob() {

    open fun setCarriedBlock(materialData: MaterialData) {
        setMetadata("carriedBlock", materialData)
    }

    open fun getCarriedBlock(): MaterialData {
        return getMetadata("carriedBlock")
    }

    open fun isScreaming(): Boolean {
        return getMetadata("isScreaming")
    }

    open fun setScreaming(screaming: Boolean) {
        setMetadata("isScreaming", screaming)
    }

    open fun isStaring(): Boolean {
        return getMetadata("isStaring")
    }

    open fun setStaring(staring: Boolean) {
        setMetadata("isStaring", staring)
    }
}