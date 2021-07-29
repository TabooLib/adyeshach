package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityTypes

/**
 * @author sky
 * @date 2020/8/4 23:15
 * 1.14+
 */
class AdyPanda : AdyEntityAgeable(EntityTypes.PANDA) {

    init {
        /**
         *
         * 1.14 -> 15
         * 1.15, 1.16 -> 16
         *
        16	Integer	Breed timer? Set to 32 when something happens, and then counts down to 0 again. At 29 and 14 (before counting down), will play the entity.panda.cant_breed sound event.	0
        17	Integer	Sneeze timer. Counts up from 0; when it hits 1 the entity.panda.pre_sneeze event plays and when it hits 21 the entity.panda.sneeze event plays (and it is set back to 0 and the sneeze flag is cleared).	0
        18	Integer	Eat timer. If nonzero, counts upwards.	0
        19	Byte	Main Gene	0
        20	Byte	Hidden Gene	0
        21	Byte	Bit mask	Meaning	0
        0x01	Unused
        0x02	Is sneezing
        0x04	Is rolling
        0x08	Is sitting [1.16 only]
        0x10	Is on back [1.16 only]
         */
        val index = at(11500 to 21, 11400 to 20)
        registerMetaByteMask(index, "isSneezing", 0x02)
        registerMetaByteMask(index, "isEating", 0x04)
        registerMetaByteMask(index, "isSitting", 0x08)
        registerMetaByteMask(index, "isOnBack", 0x10)
    }

    fun isSneezing(): Boolean {
        return getMetadata("isSneezing")
    }

    fun setSneezing(value: Boolean) {
        setMetadata("isSneezing", value)
    }

    fun isEating(): Boolean {
        return getMetadata("isEating")
    }

    fun setEating(value: Boolean) {
        setMetadata("isEating", value)
    }

    fun isSitting(): Boolean {
        if (version < 11600) {
            error("Metadata \"isSitting\" not supported this minecraft version.")
        }
        return getMetadata("isSitting")
    }

    fun setSitting(value: Boolean) {
        if (version < 11600) {
            error("Metadata \"isSitting\" not supported this minecraft version.")
        }
        setMetadata("isSitting", value)
    }

    fun isOnBack(): Boolean {
        if (version < 11600) {
            error("Metadata \"isOnBack\" not supported this minecraft version.")
        }
        return getMetadata("isOnBack")
    }

    fun setIsOnBack(value: Boolean) {
        if (version < 11600) {
            error("Metadata \"isOnBack\" not supported this minecraft version.")
        }
        setMetadata("isOnBack", value)
    }

}