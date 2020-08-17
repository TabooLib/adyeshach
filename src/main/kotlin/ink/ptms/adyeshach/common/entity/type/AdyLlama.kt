package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityTypes

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
open class AdyLlama(entityTypes: EntityTypes) : AdyEntityLiving(entityTypes) {

    constructor(): this(EntityTypes.LLAMA)

    init {
        /*
        1.16,1.15
        19 ->Strength (number of columns of 3 slots in the llama's inventory once a chest is equipped)
        20 ->Carpet color (a dye color, or -1 if no carpet equipped)
        21 ->Variant (0: llama_creamy.png, 1: llama_white.png, 2: llama_brown.png, 3: llama_gray.png)
        1.14
        18 ->Strength (number of columns of 3 slots in the llama's inventory once a chest is equipped)
        19 ->Carpet color (a dye color, or -1 if no carpet equipped)
        20 ->Variant (0: llama_creamy.png, 1: llama_white.png, 2: llama_brown.png, 3: llama_gray.png)
        1.13,1.12,1.11
        16 ->Strength (number of columns of 3 slots in the llama's inventory once a chest is equipped)
        17 ->Carpet color (a dye color, or -1 if no carpet equipped)
        18 ->Variant (0: llama_creamy.png, 1: llama_white.png, 2: llama_brown.png, 3: llama_gray.png)
        1.10,1.9
        null
         */
        registerMeta(at(11500 to 20, 11400 to 19, 11100 to 17), "carpetColor", -1)
        registerMeta(at(11500 to 21, 11400 to 20, 11100 to 18), "color", 0)
    }
}