package ink.ptms.adyeshach.common.entity

import ink.ptms.adyeshach.common.bukkit.BukkitProfession
import ink.ptms.adyeshach.common.bukkit.data.VillagerData

/**
 * @author sky
 * @since 2020-08-04 15:30
 */
interface EntityVillager {

    fun setVillagerData(villagerData: VillagerData)

    fun getVillagerData(): VillagerData

    fun setLegacyProfession(profession: BukkitProfession)

    fun getLegacyProfession(): BukkitProfession
}