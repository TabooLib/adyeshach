package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.bukkit.BukkitProfession
import ink.ptms.adyeshach.common.bukkit.data.VillagerData
import ink.ptms.adyeshach.common.entity.EntityTypes
import ink.ptms.adyeshach.common.entity.EntityVillager
import org.bukkit.entity.Villager

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
class AdyZombieVillager : AdyZombie(EntityTypes.ZOMBIE_VILLAGER), EntityVillager {

    init {
        if (version >= 11400) {
            registerMeta(at(11700 to 19), "isConverting", false)
            registerMeta(at(11700 to 20, 11500 to 19, 11400 to 18), "villagerData", VillagerData(Villager.Type.PLAINS, Villager.Profession.NONE))
        } else {
            registerMeta(at(11300 to 17, 11100 to 16), "profession", BukkitProfession.FARMER.ordinal)
                    .canEdit(false)
                    .build()
        }
        registerEditor(this)
    }

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