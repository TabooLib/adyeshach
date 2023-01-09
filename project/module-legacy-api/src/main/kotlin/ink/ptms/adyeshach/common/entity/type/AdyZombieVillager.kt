package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.bukkit.BukkitProfession
import ink.ptms.adyeshach.common.bukkit.data.VillagerData
import ink.ptms.adyeshach.common.entity.EntityTypes
import ink.ptms.adyeshach.common.entity.EntityVillager

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
@Deprecated("Outdated but usable")
class AdyZombieVillager(v2: ink.ptms.adyeshach.core.entity.EntityInstance):  AdyZombie(EntityTypes.ZOMBIE_VILLAGER, v2), EntityVillager {

    init {
        testing = true
    }

    override fun setVillagerData(villagerData: VillagerData) {
        setMetadata("villagerData", villagerData.v2())
    }

    override fun getVillagerData(): VillagerData {
        return VillagerData.fromV2(getMetadata("villagerData"))
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