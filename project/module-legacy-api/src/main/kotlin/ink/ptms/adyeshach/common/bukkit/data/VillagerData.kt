package ink.ptms.adyeshach.common.bukkit.data

import org.bukkit.entity.Villager

/**
 * @author sky
 * @since 2020-08-19 17:24
 */
@Deprecated("Outdated but usable")
class VillagerData(val type: Villager.Type, val profession: Villager.Profession) {

    fun v2(): ink.ptms.adyeshach.core.bukkit.data.VillagerData {
        val type = ink.ptms.adyeshach.core.bukkit.data.VillagerData.Type.values()[type.ordinal]
        val profession = ink.ptms.adyeshach.core.bukkit.data.VillagerData.Profession.values()[profession.ordinal]
        return ink.ptms.adyeshach.core.bukkit.data.VillagerData(type, profession)
    }

    companion object {

        fun fromV2(v2: ink.ptms.adyeshach.core.bukkit.data.VillagerData): VillagerData {
            return VillagerData(v2.type.toBukkit(), v2.profession.toBukkit())
        }
    }
}