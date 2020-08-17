package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityTypes
import org.bukkit.Material
import org.bukkit.material.MaterialData

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
class AdyEnderman() : AdyEntityLiving(EntityTypes.ENDERMAN) {


    init {
        /*
        1.16,1.15
        15 ->Carried block
        16 ->Is screaming
        17 ->Is stared at
        1.14
        14 ->Carried block
        15 ->Is screaming
        1.13,1.12,1.11,1.10
        12 ->Carried block
        13 ->Is screaming
        1.9
        11 ->Carried block
        12 ->Is screaming
         */
        registerMeta(at(11500 to 15, 11400 to 14, 11000 to 12, 10900 to 11), "carriedBlock", MaterialData(Material.AIR))
        registerMeta(at(11500 to 16, 11400 to 15, 11000 to 13, 10900 to 12), "isScreaming", false)
        registerMeta(at(11500 to 17), "isStaring", false)
    }

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