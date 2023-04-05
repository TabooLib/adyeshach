package ink.ptms.adyeshach.core.bukkit.data

import org.bukkit.entity.Villager
import taboolib.library.xseries.XMaterial

/**
 * @author sky
 * @since 2020-08-19 17:24
 */
data class VillagerData(val type: Type = Type.PLAINS, val profession: Profession = Profession.NONE) {

    override fun toString(): String {
        return "$type $profession"
    }

    enum class Type(val material: XMaterial) {

        DESERT(XMaterial.SAND),

        JUNGLE(XMaterial.JUNGLE_LOG),

        PLAINS(XMaterial.GRASS_BLOCK),

        SAVANNA(XMaterial.ACACIA_LOG),

        SNOW(XMaterial.SNOW_BLOCK),

        SWAMP(XMaterial.SLIME_BLOCK),

        TAIGA(XMaterial.SPRUCE_LOG);

        fun toBukkit(): Villager.Type {
            return Villager.Type.valueOf(name)
        }
    }

    enum class Profession(val material: XMaterial) {

        NONE(XMaterial.STONE),

        ARMORER(XMaterial.BLAST_FURNACE),

        BUTCHER(XMaterial.SMOKER),

        CARTOGRAPHER(XMaterial.CARTOGRAPHY_TABLE),

        CLERIC(XMaterial.BREWING_STAND),

        FARMER(XMaterial.COMPOSTER),

        FISHERMAN(XMaterial.BARREL),

        FLETCHER(XMaterial.FLETCHING_TABLE),

        LEATHERWORKER(XMaterial.CAULDRON),

        LIBRARIAN(XMaterial.ENCHANTING_TABLE),

        MASON(XMaterial.STONECUTTER),

        NITWIT(XMaterial.GREEN_WOOL),

        SHEPHERD(XMaterial.LOOM),

        TOOLSMITH(XMaterial.SMITHING_TABLE),

        WEAPONSMITH(XMaterial.GRINDSTONE);

        fun toBukkit(): Villager.Profession {
            return Villager.Profession.valueOf(name)
        }
    }
}