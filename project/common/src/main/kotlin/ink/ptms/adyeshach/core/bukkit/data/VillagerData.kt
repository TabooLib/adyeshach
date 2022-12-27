package ink.ptms.adyeshach.core.bukkit.data

import org.bukkit.Material

/**
 * @author sky
 * @since 2020-08-19 17:24
 */
data class VillagerData(val type: Type, val profession: Profession) {

    override fun toString(): String {
        return "$type $profession"
    }

    enum class Type(val material: Material) {

        DESERT(Material.SAND),

        JUNGLE(Material.JUNGLE_LOG),

        PLAINS(Material.GRASS_BLOCK),

        SAVANNA(Material.ACACIA_LOG),

        SNOW(Material.SNOW_BLOCK),

        SWAMP(Material.SLIME_BLOCK),

        TAIGA(Material.SPRUCE_LOG),
    }

    enum class Profession(val material: Material) {

        NONE(Material.STONE),

        ARMORER(Material.BLAST_FURNACE),

        BUTCHER(Material.SMOKER),

        CARTOGRAPHER(Material.CARTOGRAPHY_TABLE),

        CLERIC(Material.BREWING_STAND),

        FARMER(Material.COMPOSTER),

        FISHERMAN(Material.BARREL),

        FLETCHER(Material.FLETCHING_TABLE),

        LEATHERWORKER(Material.CAULDRON),

        LIBRARIAN(Material.ENCHANTING_TABLE),

        MASON(Material.STONECUTTER),

        NITWIT(Material.GREEN_WOOL),

        SHEPHERD(Material.LOOM),

        TOOLSMITH(Material.SMITHING_TABLE),

        WEAPONSMITH(Material.GRINDSTONE);
    }
}