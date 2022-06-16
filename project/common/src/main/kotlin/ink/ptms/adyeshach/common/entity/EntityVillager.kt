package ink.ptms.adyeshach.common.entity

import ink.ptms.adyeshach.common.bukkit.BukkitProfession
import ink.ptms.adyeshach.common.bukkit.data.VillagerData

/**
 * @author sky
 * @since 2020-08-04 15:30
 */
interface EntityVillager {

    /**
     * 设置村民数据（高版本）
     */
    fun setVillagerData(villagerData: VillagerData)

    /**
     * 获取村民数据（高版本）
     */
    fun getVillagerData(): VillagerData

    /**
     * 设置村民数据（低版本）
     */
    fun setLegacyProfession(profession: BukkitProfession)

    /**
     * 获取村民数据（低版本）
     */
    fun getLegacyProfession(): BukkitProfession
}