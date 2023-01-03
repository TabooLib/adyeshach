package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.bukkit.BukkitProfession
import ink.ptms.adyeshach.common.bukkit.data.VillagerData
import ink.ptms.adyeshach.common.entity.EntityTypes
import ink.ptms.adyeshach.common.entity.EntityVillager

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
@Suppress("LeakingThis")
@Deprecated("Outdated but usable")
open class AdyVillager(entityTypes: EntityTypes, v2: ink.ptms.adyeshach.core.entity.EntityInstance) : AdyEntityAgeable(entityTypes, v2), EntityVillager {

    constructor(v2: ink.ptms.adyeshach.core.entity.EntityInstance) : this(EntityTypes.VILLAGER, v2)

    init {
        testing = true
    }

    override fun setVillagerData(villagerData: VillagerData) {
        val type = ink.ptms.adyeshach.core.bukkit.data.VillagerData.Type.values()[villagerData.type.ordinal]
        val profession = ink.ptms.adyeshach.core.bukkit.data.VillagerData.Profession.values()[villagerData.profession.ordinal]
        val v1 = ink.ptms.adyeshach.core.bukkit.data.VillagerData(type, profession)
        setMetadata("villagerData", v1)
    }

    override fun getVillagerData(): VillagerData {
        val v2 = getMetadata<ink.ptms.adyeshach.core.bukkit.data.VillagerData>("villagerData")
        return VillagerData(v2.type.toBukkit(), v2.profession.toBukkit())
    }

    override fun setLegacyProfession(profession: BukkitProfession) {
        setMetadata("profession", profession.ordinal)
    }

    override fun getLegacyProfession(): BukkitProfession {
        return BukkitProfession.of(getMetadata<BukkitProfession>("profession").ordinal)
    }
}