package ink.ptms.adyeshach.common.entity.path

import io.izzel.taboolib.TabooLib
import io.izzel.taboolib.Version
import org.bukkit.entity.*

/**
 * @Author sky
 * @Since 2020-08-13 16:54
 */
enum class PathType(val entity: Class<out Creature>, val supportVersion: Int) {

    /**
     * 飞行单位（以 Bee 为代表，需要 1.15+ 版本以上）
     */
    FLY(if (Version.isAfter(Version.v1_15)) Bee::class.java else Chicken::class.java, 11500),

    /**
     * 高度接近一格（以 Chicken 为代表）
     */
    WALK_1(Chicken::class.java, 0),

    /**
     * 高度接近两格（以 Creeper 为代表）
     */
    WALK_2(Creeper::class.java, 0),

    /**
     * 高度接近三格（以 Enderman 为代表）
     */
    WALK_3(Enderman::class.java, 0);
}