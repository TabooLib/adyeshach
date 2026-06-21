package ink.ptms.adyeshach.impl.util

import ink.ptms.adyeshach.core.AdyeshachSettings
import taboolib.common.util.random
import java.util.concurrent.atomic.AtomicInteger

/**
 * @author sky
 * @since 2020-08-04 13:00
 */
@Suppress("SpellCheckingInspection", "GrazieInspection")
object Indexs {

    /**
     * 是否使用负数虚拟实体 ID（取配置 Settings.negative-entity-id，初始化时缓存，修改后需重启生效）。
     */
    private val negative = AdyeshachSettings.negativeEntityId

    /**
     * int 最大值           2,147,483,647
     * tr hologram               119,789 + (0~7763)
     * lib hologram          449,599,702
     * adyeshach npc             449,599 + (0~702)
     *
     * 客户端只需要同一次会话内唯一的实体 ID。
     * 默认使用负数可以避开服务端真实实体 ID；
     * 若客户端 mod 无法处理负数实体 ID（如 DragonCore），可改用正数，
     * 基数抬到 1_500_000_000 以尽量避免与服务端真实实体 ID 冲突。
     */
    var index = AtomicInteger((if (negative) 449599 else 1_500_000_000) + random(0, 702))

    fun nextIndex(): Int {
        return if (negative) -index.getAndIncrement() else index.getAndIncrement()
    }
}
