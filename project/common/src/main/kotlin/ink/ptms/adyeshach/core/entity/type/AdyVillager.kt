package ink.ptms.adyeshach.core.entity.type

import ink.ptms.adyeshach.core.bukkit.BukkitProfession
import ink.ptms.adyeshach.core.bukkit.data.VillagerData
import ink.ptms.adyeshach.core.entity.EntityVillager

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
interface AdyVillager : AdyEntityAgeable, EntityVillager {

    override fun setVillagerData(villagerData: VillagerData) {
        setMetadata("villagerData", villagerData)
    }

    override fun getVillagerData(): VillagerData {
        return getMetadata("villagerData")
    }

    override fun setLegacyProfession(profession: BukkitProfession) {
        setMetadata("profession", profession.ordinal)
    }

    override fun getLegacyProfession(): BukkitProfession {
        return BukkitProfession.of(getMetadata<BukkitProfession>("profession").ordinal)
    }
}