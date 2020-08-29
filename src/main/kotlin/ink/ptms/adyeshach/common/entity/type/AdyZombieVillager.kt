package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.bukkit.BukkitProfession
import ink.ptms.adyeshach.common.editor.Editors
import ink.ptms.adyeshach.common.entity.EntityTypes
import ink.ptms.adyeshach.common.entity.EntityVillager
import ink.ptms.adyeshach.common.bukkit.data.VillagerData
import org.bukkit.entity.Villager

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
class AdyZombieVillager() : AdyZombie(EntityTypes.ZOMBIE_VILLAGER), EntityVillager {

    init {
        if (version >= 11400) {
            registerMeta(at(11500 to 19, 11400 to 18), "villagerData", VillagerData(Villager.Type.PLAINS, Villager.Profession.NONE))
            registerEditor("villagerType")
                    .from(Editors.enums(Villager.Type::class) { _, entity, meta, _, e -> "/adyeshachapi edit villager_type ${entity.uniqueId} ${meta.key} $e" })
                    .display { _, entity, _ ->
                        entity.getMetadata<VillagerData>("villagerData").type.name
                    }.build()
            registerEditor("villagerProfession")
                    .from(Editors.enums(Villager.Profession::class) { _, entity, meta, _, e -> "/adyeshachapi edit villager_profession ${entity.uniqueId} ${meta.key} $e" })
                    .display { _, entity, _ ->
                        entity.getMetadata<VillagerData>("villagerData").profession.name
                    }.build()
        } else {
            registerMeta(at(11300 to 17, 11100 to 16), "profession", BukkitProfession.FARMER.ordinal)
            registerEditor("villagerProfession")
                    .from(Editors.enums(Villager.Type::class) { _, entity, meta, _, e -> "/adyeshachapi edit villager_profession_legacy ${entity.uniqueId} ${meta.key} $e" })
                    .display { _, entity, _ ->
                        BukkitProfession.values()[entity.getMetadata("profession")].name
                    }.build()
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