package ink.ptms.adyeshach.core.entity.type

import org.bukkit.material.MaterialData

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
interface AdyEnderman : AdyMob {

    fun setCarriedBlock(materialData: MaterialData) {
        setMetadata("carriedBlock", materialData)
    }

    fun getCarriedBlock(): MaterialData {
        return getMetadata("carriedBlock")
    }

    fun isScreaming(): Boolean {
        return getMetadata("isScreaming")
    }

    fun setScreaming(screaming: Boolean) {
        setMetadata("isScreaming", screaming)
    }

    fun isStaring(): Boolean {
        return getMetadata("isStaring")
    }

    fun setStaring(staring: Boolean) {
        setMetadata("isStaring", staring)
    }
}