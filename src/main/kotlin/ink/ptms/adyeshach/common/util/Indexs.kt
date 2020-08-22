package ink.ptms.adyeshach.common.util

import io.izzel.taboolib.module.inject.PlayerContainer
import io.izzel.taboolib.util.lite.Numbers
import java.util.concurrent.ConcurrentHashMap

/**
 * @Author sky
 * @Since 2020-08-04 13:00
 */
object Indexs {

    var index = 449599 + Numbers.getRandomInteger(0, 702)

    /**
     * int 最大值           2,147,483,647
     * tr hologram               119,789 + (0~7763)
     * lib hologram          449,599,702
     * adyeshach npc             449,599 + (0~702)
     */
    fun nextIndex(): Int {
        return index++
    }
}