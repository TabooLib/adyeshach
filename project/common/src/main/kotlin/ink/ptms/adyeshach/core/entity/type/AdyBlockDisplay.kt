package ink.ptms.adyeshach.core.entity.type

import org.bukkit.material.MaterialData

/**
 * @author sky
 * @date 2023/4/5 22:18
 */
interface AdyBlockDisplay : AdyDisplay {

    fun setDisplayedBlockState(value: MaterialData) {
        setMetadata("displayedBlockState", value)
    }

    fun getDisplayedBlockState(): MaterialData {
        return getMetadata("displayedBlockState")
    }
}