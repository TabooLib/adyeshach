package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.bukkit.BukkitProfession
import ink.ptms.adyeshach.common.editor.Editors
import ink.ptms.adyeshach.common.entity.EntityTypes
import ink.ptms.adyeshach.common.entity.element.VillagerData
import org.bukkit.entity.Villager

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
open class AdyVillager(entityTypes: EntityTypes) : AdyEntityAgeable(entityTypes) {

    constructor() : this(EntityTypes.VILLAGER)

    init {
        if (version >= 11400) {
            registerMeta(at(11500 to 17, 11400 to 16), "villagerData", VillagerData(Villager.Type.PLAINS, Villager.Profession.NONE))
            registerEditor("villagerType")
                    .clone(Editors.enums(Villager.Type::class) { _, entity, meta, e -> "/adyeshachapi edit villager_type ${entity.uniqueId} ${meta.key} $e" })
                    .onDisplay { _, entity, _ ->
                        entity.getMetadata<VillagerData>("villagerData").type.name
                    }.build()
            registerEditor("villagerProfession")
                    .clone(Editors.enums(Villager.Profession::class) { _, entity, meta, e -> "/adyeshachapi edit villager_profession ${entity.uniqueId} ${meta.key} $e" })
                    .onDisplay { _, entity, _ ->
                        entity.getMetadata<VillagerData>("villagerData").profession.name
                    }.build()
        } else {
            registerMeta(at(11000 to 13, 10900 to 12), "profession", BukkitProfession.FARMER.ordinal)
            registerEditor("villagerProfession")
                    .clone(Editors.enums(Villager.Type::class) { _, entity, meta, e -> "/adyeshachapi edit villager_profession_legacy ${entity.uniqueId} ${meta.key} $e" })
                    .onDisplay { _, entity, _ ->
                        BukkitProfession.values()[entity.getMetadata("profession")].name
                    }.build()
        }
    }

    fun setVillagerData(villagerData: VillagerData) {
        setMetadata("villagerData", villagerData)
    }

    fun getVillagerData(): VillagerData {
        return getMetadata("villagerData")
    }

    fun setLegacyProfession(profession: BukkitProfession) {
        setMetadata("profession", profession)
    }

    fun getLegacyProfession(): BukkitProfession {
        return getMetadata("profession")
    }
}