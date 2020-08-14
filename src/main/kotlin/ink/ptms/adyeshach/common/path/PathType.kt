package ink.ptms.adyeshach.common.path

import io.izzel.taboolib.Version
import org.bukkit.entity.*

/**
 * @Author sky
 * @Since 2020-08-13 16:54
 */
enum class PathType(val entity: Class<out Mob>) {

    /**
     * 飞行单位（低版本 Bat 高版本 Phantom 为代表）
     */
    FLY(if (Version.isAfter(Version.v1_13)) Phantom::class.java else Bat::class.java),

    /**
     * 高度接近一格（以 Chicken 为代表）
     */
    WALK_1(Chicken::class.java),

    /**
     * 高度接近两格（以 Creeper 为代表）
     */
    WALK_2(Creeper::class.java),

    /**
     * 高度接近三格（以 Enderman 为代表）
     */
    WALK_3(Enderman::class.java)
}