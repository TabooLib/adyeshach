package ink.ptms.adyeshach.common.bukkit.data

/**
 * @author sky
 * @since 2020-08-19 17:24
 */
class VillagerData(val type: Type, val profession: Profession) {

    enum class Type {

        DESERT, JUNGLE, PLAINS, SAVANNA, SNOW, SWAMP, TAIGA
    }

    enum class Profession {

        NONE, ARMORER, BUTCHER, CARTOGRAPHER, CLERIC, FARMER, FISHERMAN, FLETCHER, LEATHERWORKER, LIBRARIAN, MASON, NITWIT, SHEPHERD, TOOLSMITH, WEAPONSMITH;
    }
}