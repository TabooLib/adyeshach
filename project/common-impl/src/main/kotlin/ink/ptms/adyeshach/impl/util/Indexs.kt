package ink.ptms.adyeshach.impl.util

import taboolib.common.util.random
import java.util.concurrent.atomic.AtomicInteger

/**
 * @author sky
 * @since 2020-08-04 13:00
 */
@Suppress("SpellCheckingInspection", "GrazieInspection")
object Indexs {

    var index = AtomicInteger(449599 + random(0, 702))

    /**
     * int 最大值           2,147,483,647
     * tr hologram               119,789 + (0~7763)
     * lib hologram          449,599,702
     * adyeshach npc             449,599 + (0~702)
     *
     * 客户端只需要同一次会话内唯一的实体 ID，使用负数可以避开服务端真实实体 ID。
     */
    fun nextIndex(): Int {
        return -index.getAndIncrement()
    }
}
