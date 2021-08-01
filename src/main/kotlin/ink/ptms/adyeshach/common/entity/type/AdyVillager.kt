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
@Suppress("LeakingThis")
open class AdyVillager(entityTypes: EntityTypes) : AdyEntityAgeable(entityTypes), EntityVillager {

    constructor() : this(EntityTypes.VILLAGER)

    init {
        if (version >= 11400) {
            registerMeta(at(11700 to 17), "headShakeTimer", 0)
            registerMeta(at(11700 to 18, 11500 to 17, 11400 to 16), "villagerData", VillagerData(Villager.Type.PLAINS, Villager.Profession.NONE))
        } else {
            registerMeta(at(11000 to 13, 10900 to 12), "profession", BukkitProfession.FARMER.ordinal)
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
}