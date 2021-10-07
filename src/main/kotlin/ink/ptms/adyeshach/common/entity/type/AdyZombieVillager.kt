package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.bukkit.BukkitProfession
import ink.ptms.adyeshach.common.bukkit.data.VillagerData
import ink.ptms.adyeshach.common.entity.EntityTypes
import ink.ptms.adyeshach.common.entity.EntityVillager

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
class AdyZombieVillager : AdyZombie(EntityTypes.ZOMBIE_VILLAGER), EntityVillager {

    override fun setVillagerData(villagerData: VillagerData) {
        setMetadata("villagerData", villagerData)
    }

    override fun getVillagerData(): VillagerData {
        return getMetadata("villagerData")
    }

    override fun setLegacyProfession(profession: BukkitProfession) {
        setMetadata("profession", profession)
    }

    override fun getLegacyProfession(): BukkitProfession {
        return getMetadata("profession")
    }

    var isConvertingToVillager: Boolean
        get() = getMetadata("isConverting")
        set(value) {
            setMetadata("isConverting", value)
        }
}