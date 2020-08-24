package ink.ptms.adyeshach.common.entity

import ink.ptms.adyeshach.common.bukkit.BukkitProfession
import ink.ptms.adyeshach.common.editor.Editor
import ink.ptms.adyeshach.common.editor.Editors
import ink.ptms.adyeshach.common.entity.element.VillagerData
import org.bukkit.entity.Villager
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack

/**
 * @Author sky
 * @Since 2020-08-04 15:30
 */
interface EntityVillager {

    fun setVillagerData(villagerData: VillagerData)

    fun getVillagerData(): VillagerData

    fun setLegacyProfession(profession: BukkitProfession)

    fun getLegacyProfession(): BukkitProfession

    fun registerEditor(entityInstance: EntityInstance) {
        if (Editor.version >= 11400) {
            entityInstance.registerEditor("villagerType")
                    .from(Editors.enums(Villager.Type::class) { _, entity, meta, _, e -> "/adyeshachapi edit villager_type ${entity.uniqueId} ${meta.key} $e" })
                    .reset { player, entity, meta ->
                        setVillagerData(VillagerData(Villager.Type.PLAINS, getVillagerData().profession))
                    }
                    .display { _, entity, _ ->
                        entity.getMetadata<VillagerData>("villagerData").type.name
                    }.build()
            entityInstance.registerEditor("villagerProfession")
                    .from(Editors.enums(Villager.Profession::class) { _, entity, meta, _, e -> "/adyeshachapi edit villager_profession ${entity.uniqueId} ${meta.key} $e" })
                    .reset { player, entity, meta ->
                        setVillagerData(VillagerData(getVillagerData().type, Villager.Profession.NONE))
                    }
                    .display { _, entity, _ ->
                        entity.getMetadata<VillagerData>("villagerData").profession.name
                    }.build()
        } else {
            entityInstance.registerEditor("villagerProfession")
                    .from(Editors.enums(BukkitProfession::class) { _, entity, meta, _, e -> "/adyeshachapi edit villager_profession_legacy ${entity.uniqueId} ${meta.key} $e" })
                    .reset { player, entity, meta ->
                        setLegacyProfession(BukkitProfession.FARMER)
                    }
                    .display { _, entity, _ ->
                        BukkitProfession.values()[entity.getMetadata("profession")].name
                    }.build()
        }
    }
}